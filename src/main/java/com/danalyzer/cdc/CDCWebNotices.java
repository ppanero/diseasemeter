package com.danalyzer.cdc;

import com.danalyzer.common.MACRO;
import com.danalyzer.common.UtilsCommon;
import com.danalyzer.common.UtilsFS;
import com.danalyzer.common.UtilsWeb;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * It the alerts from the CDC web page (wwwnc.cdc.gov/travel/notices).
 * It parses them and divides them into alert level, date (in yyyy-MM-dd format), disease name,
 * disease extra information (for example the chemical formula), placement of the outbrak, and
 * extra infor about the placement (for example a city inside a country).
 *
 * It will receive the temporal data output directory and the CDC's web page url (if it is not properly formatted
 * the program will use the one mentioned above as default).
 *
 * example:
 *
 * -o data/twitter_users/ -m local[2] -u http://wwwnc.cdc.gov/travel/notices
 *
 */
public class CDCWebNotices {

    private static final String inputDateFromat = "MMMMM dd, yyyy";
    private static final String outputDateFormat = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_URL = "http://wwwnc.cdc.gov/travel/notices";
    private static final String READMORE_CLASS = "readmore";
    private static final String DISEASE_PLACE_SEPARATOR = " in ";
    private static final String BASE_FILENAME = "cdc_notices";
    private static Logger log = Logger.getLogger(CDCWebNotices.class);
    private String url;
    private String outputDir;

    public void setUrl(String url) {
        this.url = url;
    }

    public static void main(String[] args){
        String outDir = "";
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
                    outDir = UtilsFS.preparePath(outVal, false);
                    log.debug("Output directory set to: " + outDir);
                }
            }
            if(cmd.hasOption("u")) {
                String webUrl = cmd.getOptionValue("u");
                if(webUrl != null && UtilsWeb.isValidUrl(webUrl)){
                    cdcWebNotices.setUrl(webUrl);
                }
            }
        }

        cdcWebNotices.checkUrl();
        cdcWebNotices.setOutputDir(outDir);
        cdcWebNotices.run();
        log.debug("Program running correctly but not exiting. Code (0).");
    }

    private void setOutputDir(String dir) {
        this.outputDir = dir;
    }

    private void run() {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements items = doc.select("#contentArea .row");
            Iterator<Element> rowsItr = items.iterator();
            Set<String> alerts = new HashSet<String>();
            while(rowsItr.hasNext()){
                Element row = rowsItr.next();
                String id = row.id();
                if(!id.equals(MACRO.EMPTY)){
                    alerts.addAll(processBlockList(row, row.select("h3").text()));
                }
            }
            saveAlerts(outputDir, alerts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAlerts(String outputDir, Set<String> alerts) {
        UtilsFS.saveFile(outputDir,BASE_FILENAME , alerts);
    }

    private static Set<String> processBlockList(Element list, String alertLevel) {

        Elements items = list.select(".list-block li");
        Iterator<Element> itemsItr = items.iterator();
        Set<String> alertSet = new HashSet<String>();
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
                    alertSet.add(processAlert(sDate, sAlert, alertLevel));
                }
            } else {
                log.error("Error processing item. Date size is " + eDate.size() + " Alert size is " + alerts.size());
            }
        }
        return alertSet;
    }

    private static String processAlert(String sDate, String sAlert, String alertLevel) {
        String processedString = alertLevel;
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
        }
        else{
            log.error("More than two parts in alert: "+sAlert);
        }
        return processedString;
    }

    private void checkUrl() {
        if(!UtilsWeb.isValidUrl(this.url))
            this.url = DEFAULT_URL;
    }

    private static void addOptions(Options options) {
        options.addOption("o", true, "output directory");
        options.addOption("u", true, "url of CDC notices");
    }

}
