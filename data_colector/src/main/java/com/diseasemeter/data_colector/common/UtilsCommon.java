package com.diseasemeter.data_colector.common;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Light on 13/12/15.
 */
public class UtilsCommon {

    private static Logger log = Logger.getLogger(UtilsCommon.class);
    private static final String IN_BRACKETS_REGEX_ = "\\(.*\\)";
    private static final Pattern pattern = Pattern.compile(IN_BRACKETS_REGEX_);

    public static String formatDate(String date, String inputFormat, String outputFormat){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat insdf = new SimpleDateFormat(inputFormat), outsdf = new SimpleDateFormat(outputFormat);
        try {
            cal.setTime(insdf.parse(date));
            return outsdf.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "1970-01-01 01:00:00"; //Unix init time
        }
    }

    public static long getTimestampFromDate(String format, String date){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
        try{
            cal.setTime(dateFormatter.parse(date));
            return cal.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
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

    public static String getTextInBrackets(String text){
        String bText = "";
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            bText = matcher.group();

        }
        return bText;
    }

    public static Map<String, String> getArgs(Options options, String args[]){
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        }catch (org.apache.commons.cli.ParseException e) {
            log.error("Exit program with code (-2). Error parsing options");
            System.exit(-2);
        }
        Map<String, String> ret = new HashMap<String, String>();
        if(cmd != null){
            Iterator<Option> it = options.getOptions().iterator();
            while(it.hasNext()){
                Option opt = it.next();
                if(cmd.hasOption(opt.getOpt())){
                    String auxVal = cmd.getOptionValue(opt.getOpt());
                    if(auxVal != null) ret.put(opt.getOpt(), auxVal);
                }
            }
        }
        return ret;
    }

}
