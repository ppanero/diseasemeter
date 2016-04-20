package com.diseasemeter.data_colector.newspaper;


import com.diseasemeter.data_colector.bbdd.mysql.DiseaseTransaction;
import com.diseasemeter.data_colector.bbdd.mysql.GeneralTransaction;
import com.diseasemeter.data_colector.bbdd.resources.mysql.Disease;
import com.diseasemeter.data_colector.common.MACRO;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;


/**
 * Created by Light on 13/12/15.
 */
public abstract class Newspaper implements Serializable {


    private static Logger log = Logger.getLogger(Newspaper.class);
    private static String SPARK_MASTER = "local[2]";
    protected String url;

    //Spark functions
    private FlatMapFunction<Disease,String> processDiseaseNames;
    private Function<Tuple2<News, String>, Boolean> newsContainsDisease;

    public Newspaper(){
        this.url = "";
    }

    public Newspaper(String url) {
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public abstract List<News> getWebConent();

    protected boolean saveNews(List<News> news){
        //Create Spark Configuration
        SparkConf conf = new SparkConf().setAppName("Data Colector: Newspaper [Beca Colaboracion UCM]").
                set("spark.shuffle.consolidateFiles", "false").setMaster(SPARK_MASTER);
        //Initialize functions
        init();
        //Create Spark Context
        JavaSparkContext sc = new JavaSparkContext(conf);

        //List names
        GeneralTransaction<Disease> diseaseGeneralTransaction = new DiseaseTransaction();
        Set<String> columns = new HashSet<String>(Arrays.asList(new String[]{"diseaseKey._name"}));
        List<Disease> diseaseList = diseaseGeneralTransaction.getAll(Disease.class);

        JavaRDD<String> diseaseNames = sc.parallelize(diseaseList).flatMap(processDiseaseNames).distinct();
        JavaPairRDD<News, String> newsJoinDiseases = sc.parallelize(news).cartesian(diseaseNames).filter(newsContainsDisease);

        Iterator<Tuple2<News, String>> it = newsJoinDiseases.toLocalIterator();
        while(it.hasNext()){
            Tuple2<News, String> t = it.next();
            System.out.println(t._1().getTitle()+ " "+t._2());
        }
        diseaseGeneralTransaction.shutdown();
        sc.stop();
        //JavaRDD<String> list = sc.parallelize(Arrays.asList(new String[]{"a","b","c"}));
        return true;
    }

    private void init() {
        processDiseaseNames = new FlatMapFunction<Disease, String>() {
            @Override
            public Iterable<String> call(Disease disease) throws Exception {
                Set<String> ret = new HashSet<String>();

                String[] parts = disease.getDiseaseKey().getName().split(MACRO.SPACE);
                for(String part : parts){
                    ret.add(part.trim());
                }

                return ret;
            }
        };

        newsContainsDisease = new Function<Tuple2<News, String>, Boolean>() {
            @Override
            public Boolean call(Tuple2<News, String> newsStringTuple2) throws Exception {

                return newsStringTuple2._1().getTitle().toLowerCase().contains(newsStringTuple2._2().toLowerCase());
            }
        };

    }

}
