package com.diseasemeter.data_colector.monkey_learn;

import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by Light on 17/03/16.
 */
public class Processor {

    private static final String LANGUANGE_DETECTION_ID = "cl_oJNMkt2V";
    private static final String ENGLISH_SENTIMENT_ID = "cl_qkjxv9Ly";
    private static final String SPANISH_SENTIMENT_ID = "cl_u9PRHNzf";
    private static final String ENGLISH_ENTITY_EXTRACTOR = "ex_isnnZRbS";
    private static final String SPANISH_ENTITY_EXTRACTOR = "ex_Kc8uzhSi";
    private static final String SPANISH_LANG = "Spanish-es";
    private static final String ENGLISH_LANG = "English-en";
    private static final String POSITIVE_SENTIMENT = "positive";
    private static final String NEGATIVE_SENTIMENT = "negative";
    private static final String NEUTRAL_SENTIMENT = "neutral";
    private static final String ENTITY_TAG = "tag";
    private static final String ENTITY_LOCATION_ENG = "LOCATION";
    private static final String ENTITY_LOCATION_ESP = "LUG";
    private static MonkeyLearn ml;

    public static void Processor(){ //Singleton pattern
        if(ml == null && Config.config()) {
            ml = new MonkeyLearn(Config.getApiKey());
        }
    }

    public static List<Integer> detectLanguage(String[] textList){
        MonkeyLearnResponse res = null;
        double engProb = 0, espProb = 0;
        try {
            res = ml.classifiers.classify(LANGUANGE_DETECTION_ID, textList);
        } catch (MonkeyLearnException e) {
            e.printStackTrace();
        }
        List<Integer> ret = new ArrayList<Integer>();
        Iterator phrasesIt = res.arrayResult.iterator();
        while(phrasesIt.hasNext()){
            JSONArray array = (JSONArray) phrasesIt.next();
            Iterator it = array.iterator();
            while(it.hasNext()){
                JSONObject obj = (JSONObject) it.next();
                if(obj.get("label").equals(SPANISH_LANG)){
                    espProb = (Double)obj.get("probability");
                }
                else if(obj.get("label").equals(ENGLISH_LANG)){
                    engProb = (Double)obj.get("probability");
                }
            }
            Integer result = (engProb >= espProb)? 1:2;
            ret.add(result);
        }
        return  ret;//1 English, 2 Spanish
    }

    public static List<Integer> sentimentAnalysis(int language, String[] textList){
        MonkeyLearnResponse res = null;
        double max = -1;
        int result = 0;
        try {
            if(language > 1) //Menaing 2 = Spanish
                res = ml.classifiers.classify(SPANISH_SENTIMENT_ID, textList);
            else
                res = ml.classifiers.classify(ENGLISH_SENTIMENT_ID, textList);
        } catch (MonkeyLearnException e) {
            e.printStackTrace();
        }
        List<Integer> ret = new ArrayList<Integer>();
        Iterator phrasesIt = res.arrayResult.iterator();
        while(phrasesIt.hasNext()){
            JSONArray array = (JSONArray) phrasesIt.next();
            Iterator it = array.iterator();
            max = -1;
            result = 0;
            while(it.hasNext()){
                JSONObject obj = (JSONObject) it.next();
                if(obj.get("label").equals(POSITIVE_SENTIMENT) && (max < (Double)obj.get("probability"))){
                        result = 1;
                }
                else if(obj.get("label").equals(NEGATIVE_SENTIMENT) && (max < (Double)obj.get("probability"))){
                        result = -1;
                }
                else if(obj.get("label").equals(NEUTRAL_SENTIMENT) && max < (Double)obj.get("probability")){
                        result = 0;
                }
            }
            ret.add(result); //1 positive, 0 neutral, -1 negative
        }
        return ret;
    }

    public static Set<String> locationExtractor(int language, String[] textList){
        MonkeyLearnResponse res = null;
        Set<String> result = new HashSet<String>();
        String entity_location = ENTITY_LOCATION_ENG;
        try {
            if(language > 1) { //Menaing 2 = Spanish
                res = ml.extractors.extract(SPANISH_ENTITY_EXTRACTOR, textList);
                entity_location = ENTITY_LOCATION_ESP;
            }
            else
                res = ml.extractors.extract(ENGLISH_ENTITY_EXTRACTOR, textList);
        } catch (MonkeyLearnException e) {
            e.printStackTrace();
        }
        Iterator phrasesIt = res.arrayResult.iterator();
        while(phrasesIt.hasNext()){
            JSONArray array = (JSONArray) phrasesIt.next();
            Iterator it = array.iterator();
            while(it.hasNext()){
                JSONObject obj = (JSONObject) it.next();
                if(obj.get(ENTITY_TAG).toString().toUpperCase().equals(entity_location)){
                    result.add(obj.get(ENTITY_TAG).toString());
                }
            }
        }
        return result;
    }
}
