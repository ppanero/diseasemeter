package com.danalyzer;


import com.danalyzer.api.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by Light on 10/01/16.
 */
public class test {

    public static void main(String[] args){
        new Processor().sentimentAnalysis(2, new String[]{"Hace buen dia","Esto es malo malo y debería ser un -1",
                                        "Soy feliz esto es 1"});
    }
}
