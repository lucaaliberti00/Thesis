package services.sanitization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import commons.idea.*;
;
import commons.idea.Idea;
import commons.idea.Source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static commons.idea.Idea.*;

public class Sanitization {

    public static void main(String[] args) {
        Sanitization sanitization = new Sanitization();
        sanitization.run();
    }

    private void run() {


        File inputFile = new File("data/raw/dataset.idea");
        File outputFile = new File("data/sanitized/dataset.idea");

        List<Idea> ideas = readIdeasFromRawFile(inputFile);

        List<Idea> sanitizedIdeas = new ArrayList<>();

        AtomicInteger incomingEventsCount = new AtomicInteger();
        AtomicInteger outgoingEventsCount = new AtomicInteger();

        for (Idea idea : ideas) {
            if (idea != null) {
                incomingEventsCount.incrementAndGet();

                if (!hasCategoryWithPrefix(idea, "Vulnerable") && !hasCategoryWithPrefix(idea, "Abusive.Sexual")) {
                    if (hasSourceIp(idea)) {
                        idea = removeNodesWithoutName(idea);
                        idea = removeWardenFilerNodes(idea);
                        idea = removeCategory(idea, "Test");
                        idea = getFieldsOfInterest(idea);
                        outgoingEventsCount.incrementAndGet();
                        sanitizedIdeas.add(idea);
                    }
                }
            }
        }

        System.out.println("Incoming events count: " + incomingEventsCount.get());
        System.out.println("Outgoing events count: " + outgoingEventsCount.get());

        writeIdeasFormatted(outputFile, sanitizedIdeas);

    }

    private boolean hasCategoryWithPrefix(Idea idea, String prefix) {
        return idea.getCategory() != null &&
                idea.getCategory().stream().anyMatch(val -> val.startsWith(prefix));
    }

    private boolean hasSourceIp(Idea idea) {
        return idea.getSource() != null &&
                idea.getSource().stream()
                        .anyMatch(source ->
                                source != null &&
                                        source.getIP4() != null &&
                                        !source.getIP4().isEmpty());
    }

    private Idea removeNodesWithoutName(Idea idea) {
        if (idea.getNode() != null) {
            List<Node> nodesWithoutName = new ArrayList<>();
            for (Node node : idea.getNode()) {
                if (node != null && node.getName() != null && !node.getName().isEmpty()) {
                    nodesWithoutName.add(node);
                }
            }
            idea.setNode(nodesWithoutName);
        }
        return idea;
    }

    private Idea removeWardenFilerNodes(Idea idea) {
        if (idea.getNode() != null) {
            List<Node> nodesWithoutWardenFiler = new ArrayList<>();
            for (Node node : idea.getNode()) {
                if (node == null || node.getName() == null || !node.getName().contains("warden_filer")) {
                    nodesWithoutWardenFiler.add(node);
                }
            }
            idea.setNode(nodesWithoutWardenFiler);
        }
        return idea;
    }

    private Idea removeCategory(Idea idea, String categoryPrefix) {
        if (idea.getCategory() != null) {
            List<String> filteredCategories = new ArrayList<>();
            for (String category : idea.getCategory()) {
                if (!category.startsWith(categoryPrefix)) {
                    filteredCategories.add(category);
                }
            }
            idea.setCategory(filteredCategories);
        }
        return idea;
    }

    private Idea getFieldsOfInterest(Idea idea) {
        Idea newIdea = new Idea();
        newIdea.setFormat(idea.getFormat());
        newIdea.setID(idea.getID());
        newIdea.setDetectTime(idea.getDetectTime());
        newIdea.setCategory(idea.getCategory());
        newIdea.setSource(idea.getSource());
        newIdea.setTarget(idea.getTarget());
        newIdea.setNode(idea.getNode());
        return newIdea;
    }


}