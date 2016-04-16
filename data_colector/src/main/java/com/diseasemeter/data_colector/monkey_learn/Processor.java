package com.diseasemeter.data_colector.monkey_learn;

import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Light on 17/03/16.
 */
public class Processor {

    private static final String LANGUANGE_DETECTION_ID = "cl_oJNMkt2V";
    private static final String KEYWORD_EXTRACTION_ID = "";
    private static final String ENGLISH_SENTIMENT_ID = "cl_qkjxv9Ly";
    private static final String SPANISH_SENTIMENT_ID = "cl_u9PRHNzf";
    private static final String SPANISH_LANG = "Spanish-es";
    private static final String ENGLISH_LANG = "English-en";
    private static final String POSITIVE_SENTIMENT = "positive";
    private static final String NEGATIVE_SENTIMENT = "negative";
    private static final String NEUTRAL_SENTIMENT = "neutral";
    private static MonkeyLearn ml;

    public Processor(){ //Singleton pattern
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
            if(language > 1) //Menaing 1 = Spanish
                res = ml.classifiers.classify(ENGLISH_SENTIMENT_ID, textList);
            else
                res = ml.classifiers.classify(SPANISH_SENTIMENT_ID, textList);
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

    /**
     * Returns a disease weight according to the level value (1,2,3 for cdc
     * or -1 (negative), 0 (neutral), 1 (positive) for newspaper and twitter data)
     * and its source
     *
     * @param source - cdc 1, newspaper 2, twitter 3
     * @param level - disease level or sentiment
     * @return disease weight
     */
    public static int getWeight(int source, int level){
        switch (source){
            case 1: //cdc
                if(level == 1) return 100;
                else if (level == 2) return 250;
                else if (level == 3) return 500;
                else return 0;

            case 2: //newspaper
                if(level == 0) return 20; //nuetral
                else if (level == 1) return -40; //positive
                else if (level == -1) return 50; //negative
                else return 0;

            case 3: //twitter
                if(level == 0) return 10; //nuetral
                else if (level == 1) return -20; //positive
                else if (level == -1) return 30; //negative
                else return 0;

            default:
                return 0;
        }
    }
}
