package com.danalyzer.cdc;

import com.danalyzer.common.MACRO;
import com.danalyzer.common.UtilsCommon;
import com.danalyzer.common.UtilsFS;
import com.danalyzer.common.WebUtils;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;


/**
 * Created by Light on 10/01/16.
 */
public class CDCWebNotices {

    private static final String inputDateFromat = "MMMMM dd, yyyy";
    private static final String outputDateFormat = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_URL = "http://wwwnc.cdc.gov/travel/notices";
    private static final String READMORE_CLASS = "readmore";
    private static final String DISEASE_PLACE_SEPARATOR = " in ";
    private static Logger log = Logger.getLogger(CDCWebNotices.class);
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public static void main(String[] args){
        String outputDir = "";
        //Read input arguments
        if (args.length != 4) {
            System.out.printf("Usage: CDCWebNotices -o <output dir> -u <url> \n");
            log.error("Exit program with code (-1). Insufficient calling arguments");
            System.exit(-1);
        }
        // create Options object
        Options options = new Options();
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        addOptions(options);
        CDCWebNotices cdcWebNotices = new CDCWebNotices();
        try {
            cmd = parser.parse( options, args);
        }catch (ParseException e) {
            log.error("Exit program with code (-2). Error parsing options");
            System.exit(-2);
        }
        if(cmd != null){
            if(cmd.hasOption("o")) {
                String outVal = cmd.getOptionValue("o");
                if(outVal != null){
                    outputDir = UtilsFS.preparePath(outVal, false);
                    log.debug("Output directory set to: " + outputDir);
                }
            }
            if(cmd.hasOption("u")) {
                String webUrl = cmd.getOptionValue("u");
                if(webUrl != null && WebUtils.isValidUrl(webUrl)){
                    cdcWebNotices.setUrl(webUrl);
                }
            }
        }

        cdcWebNotices.checkUrl();
        cdcWebNotices.run();
        log.debug("Program running correctly but not exiting. Code (0).");
    }

    private void run() {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements items = doc.select("#contentArea .row");
            Iterator<Element> rowsItr = items.iterator();

            while(rowsItr.hasNext()){
                Element row = rowsItr.next();
                String id = row.id();
                if(!id.equals(MACRO.EMPTY)){
                    processBlockList(row, row.select("h3").text());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processBlockList(Element list, String alertLevel) {

        Elements items = list.select(".list-block li");
        Iterator<Element> itemsItr = items.iterator();
        while(itemsItr.hasNext()) {
            Element item = itemsItr.next();
            Elements eDate = item.select(".date");
            Elements alerts = item.select("a");
            if (eDate.size() == 1 && alerts.size() == 2) {
                boolean correct = true;
                String sDate = eDate.get(0).text();
                String sAlert = "";
                if (!alerts.get(0).hasClass(READMORE_CLASS)) {
                    sAlert = alerts.get(0).text();
                } else if (!alerts.get(1).hasClass(READMORE_CLASS)) {
                    sAlert = alerts.get(1).text();
                } else {
                    correct = false;
                    log.error("Error processing item. No alert information available");
                }
                if (correct) {
                    processAlert(sDate, sAlert, alertLevel);
                }
            } else {
                log.error("Error processing item. Date size is " + eDate.size() + " Alert size is " + alerts.size());
            }
        }
    }

    private static void processAlert(String sDate, String sAlert, String alertLevel) {
        String processedString = alertLevel.concat(MACRO.FILE_SEPARATOR);
        //Convert date to YYYY-MM-DD
        processedString = processedString.concat(MACRO.FILE_SEPARATOR).concat(
                UtilsCommon.formatDate(sDate, inputDateFromat, outputDateFormat)
        );
        //Separate disease type from place split by "in"
        String[] parts = sAlert.split(DISEASE_PLACE_SEPARATOR);
        if(parts.length == 2){
            String sInBrackets = "";
            //Extract possible () words in the disease
            sInBrackets = UtilsCommon.getTextInBrackets(parts[0]);
            processedString = processedString.concat(MACRO.FILE_SEPARATOR).concat(
                    parts[0].replace(sInBrackets, MACRO.EMPTY).trim()
            );
            if(!sInBrackets.equals(MACRO.EMPTY)){
                processedString = processedString.concat(MACRO.FILE_SEPARATOR).concat(
                        sInBrackets.replace("(", MACRO.EMPTY).replace(")", MACRO.EMPTY)
                );
            }
            else{
                processedString = processedString.concat(MACRO.FILE_SEPARATOR).concat(MACRO.MISSING_VALUE);
            }
            //Extract possible () words in the place
            sInBrackets = UtilsCommon.getTextInBrackets(parts[1]);
            processedString = processedString.concat(MACRO.FILE_SEPARATOR).concat(
                    parts[1].replace(sInBrackets, MACRO.EMPTY).trim()
            );
            if(!sInBrackets.equals(MACRO.EMPTY)){
                processedString = processedString.concat(MACRO.FILE_SEPARATOR).concat(
                        sInBrackets.replace("(", MACRO.EMPTY).replace(")", MACRO.EMPTY)
                );
            }
            else{
                processedString = processedString.concat(MACRO.FILE_SEPARATOR).concat(MACRO.MISSING_VALUE);
            }
            System.out.println(processedString);
        }
        else{
            log.error("More than two parts in alert: "+sAlert);
        }
    }

    private void checkUrl() {
        if(!WebUtils.isValidUrl(this.url))
            this.url = DEFAULT_URL;
    }

    private static void addOptions(Options options) {
        options.addOption("o", true, "output directory");
        options.addOption("u", true, "url of CDC notices");
    }

}
