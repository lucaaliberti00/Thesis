package it.unisa;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import commons.idea.Idea;

public class MainIDEA {

    public static void main(String[] args) {
        List<Idea> ideas = readIdeasFromFile("data/raw/dataset.idea");

        // Do something with the list of ideas
        System.out.println(ideas.size());
    }

    public static List<Idea> readIdeasFromFile(String filePath) {
        List<Idea> ideas = new ArrayList<>();
        String line="";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            ObjectMapper objectMapper = new ObjectMapper();

            while ((line = reader.readLine()) != null) {
                try{
                    Idea idea = objectMapper.readValue(line, Idea.class);
                    ideas.add(idea);
                } catch (Exception e){

                }

            }
        } catch (IOException e) {

        }

        return ideas;
    }
}






