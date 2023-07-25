package examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;

import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTNS;
import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTopSeqRules;
import ca.pfv.spmf.datastructures.redblacktree.RedBlackTree;
import ca.pfv.spmf.input.sequence_database_array_integers.Sequence;
import ca.pfv.spmf.input.sequence_database_array_integers.SequenceDatabase;
import commons.mining.model.Item;
import commons.mining.model.Rule;


/**
 * Example of how to use the TNS algorithm in source code.
 * @author Philippe Fournier-Viger (Copyright 2008)
 */
public class MainTestTNS {

    public static void main(String [] arg) throws Exception{
        // Load database into memory
        SequenceDatabase database = new SequenceDatabase();
        database.loadFile("C:\\Users\\lucaa\\Desktop\\DataMining\\AIDA_TopK_TNS\\paper.txt");

/*
        int k = 10;
        double minConf = 0.5;
        //int delta =  2;

        AlgoTopSeqRules algo = new AlgoTopSeqRules();
        algo.runAlgorithm(k, database, minConf);

        algo.writeResultTofile("output_paper.txt");   // to save results to file

        algo.printStats();*/


        List<Rule> rules = readRulesFromFile("output_paper.txt");
        List<Sequence> seqs = database.getSequences();

        for (Rule r : rules) {
            int ant_count = 0;
            int cons_count = 0;
            int seq_ok = 0;
            boolean matched = false;

            List<Integer> antecedent = r.getAntecedent();
            List<Integer> consequent = r.getConsequent();

            for (Sequence s : seqs) {

                matched = false;
                List<Integer> combinedList = new ArrayList<>();
                for (Integer[] array : s.getItemsets()) {
                    combinedList.addAll(Arrays.asList(array));
                }

                int index = getLastMatch(combinedList, antecedent);
                if (index != -1) {
                    combinedList = combinedList.subList(index,combinedList.size());
                    matched = new HashSet<>(combinedList).containsAll(consequent);
                    ant_count++;
                }
                if (matched) {
                    cons_count++;
                    seq_ok++;

                }

            }
            System.out.println("\t----\t");
            System.out.println(r);
            System.out.println("Support " + seq_ok);
            double confidence = (double) cons_count / ant_count;
            System.out.println("Confidence " + confidence);
            System.out.println("\t----\t");



        }


    }
    private static int getLastMatch(List<Integer> firstList, List<Integer> secondList) {

        List<Integer> temp = new ArrayList<>(secondList);

        int index = -1;

        for (int i = 0; i < firstList.size(); i++) {
            if (temp.size() == 0)
                return index;

            int item = firstList.get(i);

            if (temp.contains(item)) {
                try {
                    temp.remove(Integer.valueOf(item));
                }catch (Exception e){
                    e.printStackTrace();
                }
                index = i;
            }

        }
        if (temp.size() != 0)
            return -1;
        else
            return index;
    }

    public static List<Rule> readRulesFromFile(String filename) {
        List<Rule> rules = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String antecedent = line.split(" ==> ")[0];
                String consequent = line.split(" ==> ")[1].split(" ")[0];

                List<Integer> ant = new ArrayList<>();
                List<Integer> cons = new ArrayList<>();

                for(String a : antecedent.split(",")){
                    ant.add(Integer.valueOf(a));
                }

                for(String c : consequent.split(",")){
                    cons.add(Integer.valueOf(c));
                }

                rules.add(new Rule(ant,cons));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rules;
    }

    public static class Rule{

        private List<Integer> antecedent;
        private List<Integer> consequent;

        public Rule(List<Integer> antecedent, List<Integer> consequent) {
            this.antecedent = antecedent;
            this.consequent = consequent;
        }

        public List<Integer> getAntecedent() {
            return antecedent;
        }

        public void setAntecedent(List<Integer> antecedent) {
            this.antecedent = antecedent;
        }

        public List<Integer> getConsequent() {
            return consequent;
        }

        public void setConsequent(List<Integer> consequent) {
            this.consequent = consequent;
        }

        @Override
        public String toString() {
            return "Rule{" +
                    "antecedent=" + antecedent +
                    ", consequent=" + consequent +
                    '}';
        }
    }



}
