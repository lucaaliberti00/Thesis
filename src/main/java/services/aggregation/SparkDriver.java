package services.aggregation;
import commons.idea.Idea;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
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

        System.out.println("ciao");




    }

}
