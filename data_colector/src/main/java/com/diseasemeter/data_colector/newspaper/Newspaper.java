package com.diseasemeter.data_colector.newspaper;


import com.diseasemeter.data_colector.bbdd.mysql.DiseaseTransaction;
import com.diseasemeter.data_colector.bbdd.mysql.GeneralTransaction;
import com.diseasemeter.data_colector.bbdd.resources.mysql.Disease;
import com.diseasemeter.data_colector.bbdd.resources.mysql.DiseaseKey;
import com.diseasemeter.data_colector.common.MACRO;
import com.diseasemeter.data_colector.microsoft_api.Translator;
import com.diseasemeter.data_colector.monkey_learn.Processor;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;


/**
 * Created by Light on 13/12/15.
 */
public abstract class Newspaper implements Serializable {


    private static final String NO_LOCATION = "NO LOCATION";
    private static Logger log = Logger.getLogger(Newspaper.class);
    private static String SPARK_MASTER = "local[2]";
    protected String url;

    //Spark functions
    private PairFlatMapFunction<Disease,String,String> processDiseaseNames;
    private PairFlatMapFunction<Tuple2<String,String>,String,String> addTranslations;


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
        List<Disease> diseaseList = diseaseGeneralTransaction.getAll(Disease.class);

        JavaPairRDD<String, String> diseaseNames = sc.parallelize(diseaseList)
                                                    .flatMapToPair(processDiseaseNames).distinct()
                                                    .flatMapToPair(addTranslations).distinct().cache();

        //JavaRDD<String> diseaseNamesWithTranslation = diseaseNames;
        //JavaPairRDD<News, String> newsJoinDiseases = sc.parallelize(news).cartesian(diseaseNamesWithTranslation).filter(newsContainsDisease);
        //JavaRDD<Tuple2<News, String>> processedNews = newsJoinDiseases.map(obtainValues);
        //JavaRDD<Tuple2<News, Disease>> newsAndDisease = processedNews.flatMap(obtainDiseases).cache();
        Iterator<Tuple2<String, String>> it2 = diseaseNames.toLocalIterator();
        while(it2.hasNext()){
            Tuple2<String, String> t = it2.next();
            System.out.println(t._1()+" "+t._2());
        }
        diseaseGeneralTransaction.shutdown();
        sc.stop();
        return true;
    }

    private void init() {

        processDiseaseNames = new PairFlatMapFunction<Disease,String,String>() {

            @Override
            public Iterable<Tuple2<String, String>> call(Disease disease) throws Exception {
                Set<Tuple2<String, String>> ret = new HashSet<Tuple2<String, String>>();

                String diseaseName = disease.getDiseaseKey().getName();
                String[] parts = diseaseName.split(MACRO.SPACE);
                for(String part : parts){
                    ret.add(new Tuple2<String, String>(part.trim(), diseaseName));
                }
                ret.add(new Tuple2<String, String>(diseaseName, diseaseName));

                return ret;
            }
        };

        addTranslations = new PairFlatMapFunction<Tuple2<String,String>,String,String>(){

            @Override
            public Iterable<Tuple2<String, String>> call(Tuple2<String, String> stringStringTuple2) throws Exception {
                Set<Tuple2<String, String>> ret = new HashSet<Tuple2<String, String>>();

                Integer[] partsLanguage = new Integer[2];
                Processor.Processor();
                Processor.detectLanguage(new String[]{stringStringTuple2._1(), stringStringTuple2._2()}).toArray(partsLanguage);

                boolean es = false;
                if(partsLanguage != null && partsLanguage.length == 2){
                    String key = stringStringTuple2._1();
                    String val = stringStringTuple2._2();
                    if(partsLanguage[0] == 1){
                        es = true;
                        key = Translator.translate(stringStringTuple2._1().trim());
                    }
                    if(partsLanguage[1] == 1){
                        es = true;
                        val = Translator.translate(stringStringTuple2._2().trim());
                    }
                    ret.add(new Tuple2<String, String>(key, val));
                }
                if(es){
                    ret.add(new Tuple2<String, String>(stringStringTuple2._1(), stringStringTuple2._2()));
                }
                return ret;
            }
        };
        /*
        newsContainsDisease = new Function<Tuple2<News, String>, Boolean>() {
            @Override
            public Boolean call(Tuple2<News, String> newsStringTuple2) throws Exception {

                return newsStringTuple2._1().getTitle().toLowerCase().contains(newsStringTuple2._2().toLowerCase());
            }
        };

        obtainValues = new Function<Tuple2<News, String>, Tuple2<News, String>>() {


            @Override
            public Tuple2<News, String> call(Tuple2<News, String> newsStringTuple2) throws Exception {
                News news = newsStringTuple2._1();
                String[] textList = new String[]{news.getTitle()};
                Processor.Processor();
                List<Integer> languageDetections = Processor.detectLanguage(textList);
                if(languageDetections != null && languageDetections.size() == 1){
                    List<Integer> sentimentDetections = null;
                    if(languageDetections.get(0) == 1){
                        sentimentDetections = Processor.sentimentAnalysis(1, textList);
                    }
                    else{
                        sentimentDetections = Processor.sentimentAnalysis(2, textList);
                    }
                    if(sentimentDetections != null && sentimentDetections.size() == 1){
                        news.setLanguage(languageDetections.get(0));
                        news.setSentiment(sentimentDetections.get(0));
                        news.setWeight(News.getWeightFromSentiment(sentimentDetections.get(0)));
                    }
                }
                return new Tuple2<News, String>(news, newsStringTuple2._2());
            }
        };

        obtainDiseases = new FlatMapFunction<Tuple2<News, String>, Tuple2<News, Disease>>(){

            @Override
            public Iterable<Tuple2<News, Disease>> call(Tuple2<News, String> newsStringTuple2) throws Exception {
                News news = newsStringTuple2._1();
                Processor.Processor();
                Set<Tuple2<News, Disease>> ret = new HashSet<Tuple2<News, Disease>>();
                Set<String> locations = Processor.locationExtractor(news.getLanguage(), new String[]{news.getTitle()});
                if(locations != null && locations.size() > 0){
                    for(String location : locations){
                        ret.add(new Tuple2<News, Disease>(news, new Disease(new DiseaseKey(newsStringTuple2._2(), location))));
                    }
                }
                else{
                    ret.add(new Tuple2<News, Disease>(news, new Disease(new DiseaseKey(newsStringTuple2._2(), NO_LOCATION))));
                }
                return ret;
            }
        };*/
    }

}
