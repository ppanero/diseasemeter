package com.danalyzer.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Light on 13/12/15.
 */
public class UtilsCommon {

    private static final String IN_BRACKETS_REGEX_ = "\\(\\w*\\)";
    private static final Pattern pattern = Pattern.compile(IN_BRACKETS_REGEX_);

    public static String formatDate(String date, String inputFormat, String outputFormat){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat insdf = new SimpleDateFormat(inputFormat), outsdf = new SimpleDateFormat(outputFormat);
        try {
            cal.setTime(insdf.parse(date));
            return outsdf.format(cal.getTime()).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return "1970-01-01 01:00:00"; //Unix init time
        }
    }

    public static Properties readProperties(String file) {
        Properties props = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            props.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return props;
    }

    //Main for tests
    public static void main (String[] args){
        //formatDate test
        //System.out.println(formatDate("Tue Dec 15 20:36:14 CET 2015","EEE MMM dd HH:mm:ss z yyyy", "YYYY-MM-DD HH:MM:SS"));
    }

    public static String getTextInBrackets(String text){
        String bText = "";
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            bText = matcher.group();

        }
        return bText;
    }
}
