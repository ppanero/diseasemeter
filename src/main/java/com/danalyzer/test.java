package com.danalyzer;


import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by Light on 10/01/16.
 */
public class test {

    public static void main(String[] args){
        String[] parts = new String[]{"a","b","c"};
        System.out.println(StringUtils.join(parts,","));
    }
}
