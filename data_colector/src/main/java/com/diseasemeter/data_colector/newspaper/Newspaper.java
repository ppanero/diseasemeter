package com.diseasemeter.data_colector.newspaper;


import com.diseasemeter.data_colector.bbdd.mongodb.GeneralOperation;
import com.diseasemeter.data_colector.bbdd.mongodb.MongoComparation;
import com.diseasemeter.data_colector.bbdd.mongodb.MongoDBController;
import com.diseasemeter.data_colector.bbdd.mysql.DiseaseTransaction;
import com.diseasemeter.data_colector.bbdd.mysql.GeneralTransaction;
import com.diseasemeter.data_colector.bbdd.mysql.NewsTransaction;
import com.diseasemeter.data_colector.bbdd.resources.mongodb.Center;
import com.diseasemeter.data_colector.bbdd.resources.mongodb.HeatPoint;
import com.diseasemeter.data_colector.bbdd.resources.mongodb.Location;
import com.diseasemeter.data_colector.bbdd.resources.mysql.Disease;
import com.diseasemeter.data_colector.bbdd.resources.mysql.DiseaseKey;
import com.diseasemeter.data_colector.bbdd.resources.mysql.News;
import com.diseasemeter.data_colector.common.MACRO;
import com.diseasemeter.data_colector.common.UtilsCommon;
import com.diseasemeter.data_colector.google_api.Geocoder;
import com.diseasemeter.data_colector.microsoft_api.Translator;
import com.diseasemeter.data_colector.monkey_learn.Processor;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.springframework.data.mongodb.core.query.Criteria;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;


/**
 * Created by Light on 13/12/15.
 */
public abstract class Newspaper implements Serializable {

    private static Logger log = Logger.getLogger(Newspaper.class);
    private static final String outputDateFormat = "dd/MM/yyyy";
    private static String SPARK_MASTER = "local[2]";
    protected String source;
    protected String url;

    //Spark functions
    private PairFlatMapFunction<Disease,String,String> processDiseaseNames;
    private PairFlatMapFunction<Tuple2<String,String>,String,String> addTranslations;
    private Function<Tuple2<News, Tuple2<String, String>>, Boolean> newsContainsDisease;
    private PairFunction<Tuple2<News, Tuple2<String, String>>, News, Tuple2<String, String>> obtainValues;
    private PairFlatMapFunction<Tuple2<News, Tuple2<String, String>>, News, Disease> obtainDiseases;
    private PairFlatMapFunction<Tuple2<News, Disease>, News, Disease> insertInMysql;
    private PairFlatMapFunction<Tuple2<News,Disease>,News,Disease> insertInMongoDB;

    public Newspaper(){
        this.url = "";
    }

    public Newspaper(String url, String source) {
        this.url = url;
        this.source = source;
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

        JavaPairRDD<News, Tuple2<String, String>> newsJoinDiseases = sc.parallelize(news)
                                                                        .cartesian(diseaseNames)
                                                                        .filter(newsContainsDisease)
                                                                        .mapToPair(obtainValues).cache();

        JavaPairRDD<News, Disease> result = newsJoinDiseases.flatMapToPair(obtainDiseases)
                                                            .flatMapToPair(insertInMysql).cache()
                                                            .flatMapToPair(insertInMongoDB).cache();

        log.info("Number of news found for ".concat(this.source).concat(" is ").concat(String.valueOf(result.count())));
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
                    ret.add(new Tuple2<String, String>(diseaseName, part.trim()));
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

                if(partsLanguage != null && partsLanguage.length == 2){
                    if(partsLanguage[0] == 1){
                        String key = Translator.translate(stringStringTuple2._1().trim());
                        ret.add(new Tuple2<String, String>(stringStringTuple2._1(), key));
                    }
                    if(partsLanguage[1] == 1){
                        String val = Translator.translate(stringStringTuple2._2().trim());
                        ret.add(new Tuple2<String, String>(stringStringTuple2._1(), val));
                    }
                }
                ret.add(new Tuple2<String, String>(stringStringTuple2._1(), stringStringTuple2._2()));

                return ret;
            }
        };


        newsContainsDisease = new Function<Tuple2<News, Tuple2<String, String>>, Boolean>() {

            @Override
            public Boolean call(Tuple2<News, Tuple2<String, String>> newsTuple2Tuple2) throws Exception {
                String title = newsTuple2Tuple2._1().getNewsKey().getTitle().toLowerCase();
                String key = newsTuple2Tuple2._2()._1().toLowerCase();
                String val = newsTuple2Tuple2._2()._2().toLowerCase();

                if(title.contains(val) && title.contains(key))
                    return false;
                return title.contains(val);
            }
        };


        obtainValues = new PairFunction<Tuple2<News, Tuple2<String, String>>, News, Tuple2<String, String>> () {

            @Override
            public Tuple2<News, Tuple2<String, String>> call(Tuple2<News, Tuple2<String, String>> newsTuple2Tuple2) throws Exception {

                News news = newsTuple2Tuple2._1();
                String[] textList = new String[]{news.getNewsKey().getTitle()};
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
                if(news.getDate()== null){
                    news.setDate(UtilsCommon.getCurrentDate(outputDateFormat));
                }
                news.getNewsKey().setDisease(newsTuple2Tuple2._2()._1());
                return new Tuple2<News, Tuple2<String, String>>(news, newsTuple2Tuple2._2());
            }

        };

        obtainDiseases = new PairFlatMapFunction<Tuple2<News, Tuple2<String, String>>, News, Disease>(){

            @Override
            public Iterable<Tuple2<News, Disease>> call(Tuple2<News, Tuple2<String, String>> newsTuple2Tuple2) throws Exception {
                News news = newsTuple2Tuple2._1();
                Processor.Processor();
                Set<Tuple2<News, Disease>> ret = new HashSet<Tuple2<News, Disease>>();
                Set<String> locations = Processor.locationExtractor(news.getLanguage(), new String[]{news.getNewsKey().getTitle()});
                if(locations != null && locations.size() > 0){
                    for(String location : locations){
                        if(!newsTuple2Tuple2._2()._1().contains(location)) {
                            news.setLocation(location);
                            ret.add(new Tuple2<News, Disease>(news, new Disease(new DiseaseKey(newsTuple2Tuple2._2()._1(), location))));
                        }
                    }
                }
                else{
                    ret.add(new Tuple2<News, Disease>(news, new Disease(new DiseaseKey(newsTuple2Tuple2._2()._1(), null))));
                }
                return ret;
            }
        };

        insertInMysql = new PairFlatMapFunction<Tuple2<News, Disease>, News, Disease>(){

            @Override
            public Iterable<Tuple2<News, Disease>> call(Tuple2<News, Disease> newsDiseaseTuple2) throws Exception {
                Set<Tuple2<News, Disease>> ret = new HashSet<Tuple2<News, Disease>>();

                Disease disease = newsDiseaseTuple2._2();
                News news = newsDiseaseTuple2._1();

                GeneralTransaction<Disease> diseaseTransaction = new DiseaseTransaction();
                GeneralTransaction<News> newsTransaction = new NewsTransaction();

                if(disease.getDiseaseKey().noLocation()){
                    if(!newsTransaction.exists(news)){
                        List<Disease> diseaseList = ((DiseaseTransaction)diseaseTransaction).getAllByName(disease);
                        for(Disease dinList : diseaseList){
                            dinList.setNewsCount(dinList.getNewsCount()+1);
                            int nWeight = dinList.getWeight() + news.getWeight();
                            dinList.setWeight(nWeight);
                            disease.setLevel(Disease.getLevelFromNewWeight(nWeight));
                            dinList.setLastUpdate(UtilsCommon.getCurrentDate(outputDateFormat));

                            if(!diseaseTransaction.update(dinList)){
                                log.error("Error when updating disease");
                            }

                            ret.add(new Tuple2<News, Disease>(news, dinList));
                        }
                        newsTransaction.insert(news);
                    }
                    else{
                        List<Disease> diseaseList = ((DiseaseTransaction)diseaseTransaction).getAllByName(disease);
                        for(Disease dinList : diseaseList){
                            dinList.setNewsCount(dinList.getNewsCount()+1);
                            dinList.setLastUpdate(UtilsCommon.getCurrentDate(outputDateFormat));

                            if(!diseaseTransaction.update(dinList)){
                                log.error("Error when updating disease");
                            }
                            ret.add(new Tuple2<News, Disease>(news, dinList));
                        }
                        newsTransaction.insert(news);
                    }
                }
                else{
                    if(!newsTransaction.exists(news)){
                        if(!diseaseTransaction.exists(disease)){
                            disease.setWeight(news.getWeight());
                            disease.setNewsCount(disease.getNewsCount()+1);
                            diseaseTransaction.insert(disease);
                        }
                        else{
                            disease = diseaseTransaction.findByKey(disease);
                            disease.setNewsCount(disease.getNewsCount()+1);
                            int nWeight = disease.getWeight() + news.getWeight();
                            disease.setWeight(nWeight);
                            disease.setLevel(Disease.getLevelFromNewWeight(nWeight));
                            disease.setLastUpdate(UtilsCommon.getCurrentDate(outputDateFormat));

                            if(!diseaseTransaction.update(disease)){
                                log.error("Error when updating disease");
                            }

                            ret.add(new Tuple2<News, Disease>(news, disease));
                        }
                        newsTransaction.insert(news);
                    }
                    else{

                        disease.setNewsCount(disease.getNewsCount()+1);
                        disease.setLastUpdate(UtilsCommon.getCurrentDate(outputDateFormat));
                        if(!diseaseTransaction.update(disease)){
                            log.error("Error when updating disease");
                        }
                    }
                    ret.add(new Tuple2<News, Disease>(news, disease));
                }

                diseaseTransaction.shutdown();
                newsTransaction.shutdown();

                return ret;
            }
        };


        insertInMongoDB = new PairFlatMapFunction<Tuple2<News,Disease>,News,Disease>(){

            @Override
            public Iterable<Tuple2<News, Disease>> call(Tuple2<News, Disease> newsDiseaseTuple2) throws Exception {
                Set<Tuple2<News, Disease>> ret = new HashSet<Tuple2<News, Disease>>();
                News news = newsDiseaseTuple2._1();
                Disease disease = newsDiseaseTuple2._2();

                GeneralOperation<Center> centerOperation = new GeneralOperation<Center>();
                GeneralOperation<HeatPoint> heatpointOperation = new GeneralOperation<HeatPoint>();
                String location = newsDiseaseTuple2._2().getDiseaseKey().getLocation();
                Double[] coordinates = Geocoder.geocode(location);

                Set<Criteria> conditions = new HashSet<Criteria>();
                conditions.add(MongoDBController.createCriteria("name", MongoComparation.EQ, disease.getDiseaseKey().getName()));
                if(coordinates != null){
                    long timestamp = UtilsCommon.getTimestampFromDate(outputDateFormat, news.getDate());
                    if(!centerOperation.existsAtMaxDistance(Center.class, "location", coordinates, 500, conditions)) {
                        centerOperation.insert(new Center(disease.getDiseaseKey().getName(), location, timestamp, new Location(coordinates)));
                    }
                    heatpointOperation.insert(new HeatPoint(news.getWeight(),timestamp, disease.getDiseaseKey().getName(), location,
                            new Location(coordinates)));
                }
                ret.add(newsDiseaseTuple2);
                return ret;
            }
        };
    }

}
