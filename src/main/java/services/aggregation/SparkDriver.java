package services.aggregation;
import commons.idea.Idea;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static commons.idea.Idea.*;


public class SparkDriver {


    public static void main(String[] args) {
        System.setProperty("hadoop.home.dir", "C:\\Users\\lucaa\\Desktop\\DataMining\\AIDA_TopK_TNS\\hadoop");
        System.load("C:\\Users\\lucaa\\Desktop\\DataMining\\AIDA_TopK_TNS\\hadoop\\bin\\hadoop.dll");


        String inputFile = "data\\sanitized\\dataset.idea";
        String outputPath = "data\\aggregated\\output";

        // Create a configuration object and set the name of the application
        SparkConf conf = new SparkConf().setAppName("Aggregation").setMaster("local");

        // Create a Spark Context object
        JavaSparkContext sc = new JavaSparkContext(conf);



        List<Idea> ideaList = readIdeasFromFormattedFile(new File(inputFile));
        JavaRDD<Idea> ideaRDD = sc.parallelize(ideaList);

        // Create a Function to extract the key from an Idea object
        Function<Idea, String> keyExtractor = idea -> idea.getCategory() + idea.getSource().get(0).getIP4().get(0) +
              idea.getTarget().get(0).getIP4() + idea.getNode().get(0).getName() + idea.getDetectTime();

        // Remove duplicates based on the key
        JavaRDD<Idea> deduplicatedRDD = ideaRDD.distinct().mapToPair(idea -> new Tuple2<>(keyExtractor.call(idea), idea))
                .reduceByKey((idea1, idea2) -> idea1)
                .map(Tuple2::_2);

        // Convert RDD back to a List
        List<Idea> deduplicatedList = deduplicatedRDD.collect();

        System.out.println(deduplicatedList.size());
        System.out.println(ideaList.size());

        System.out.println("ciao");

        sc.close();



    }



}
