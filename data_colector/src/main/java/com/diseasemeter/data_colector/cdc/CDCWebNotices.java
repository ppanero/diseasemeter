package com.diseasemeter.data_colector.cdc;

import com.diseasemeter.data_colector.common.MACRO;
import com.diseasemeter.data_colector.common.UtilsCommon;
import com.diseasemeter.data_colector.common.UtilsFS;
import com.diseasemeter.data_colector.common.UtilsWeb;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    private static final String outputDateFormat = "dd/MM/yyyy HH:mm:ss";
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
        addOptions(options);
        CDCWebNotices cdcWebNotices = new CDCWebNotices();

        Map<String, String> parsedArgs = UtilsCommon.getArgs(options, args);

        String outVal = parsedArgs.get("o");
        if(outVal != null && !outVal.equals(MACRO.SPACE)){
            outDir = UtilsFS.preparePath(outVal, false);
            log.debug("Output directory set to: " + outDir);
        }

        String webUrl = parsedArgs.get("u");
        if(webUrl != null && !webUrl.equals(MACRO.SPACE) && UtilsWeb.isValidUrl(webUrl)){
            cdcWebNotices.setUrl(webUrl);
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
            Set<CDCAlert> alerts = new HashSet<CDCAlert>();
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

    private void saveAlerts(String outputDir, Set<CDCAlert> alerts) {
        for(CDCAlert a : alerts){
            System.out.println(a.toString());
        }
        System.out.println(alerts.size());
        // /UtilsFS.saveFile(outputDir,BASE_FILENAME , alerts);
    }

    private static Set<CDCAlert> processBlockList(Element list, String alertLevel) {

        Elements items = list.select(".list-block li");
        Iterator<Element> itemsItr = items.iterator();
        Set<CDCAlert> alertSet = new HashSet<CDCAlert>();
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
                    CDCAlert cdcAlert = processAlert(sDate, sAlert, alertLevel);
                    if(cdcAlert != null)
                        alertSet.add(cdcAlert);
                }
            } else {
                log.error("Error processing item. Date size is " + eDate.size() + " Alert size is " + alerts.size());
            }
        }
        return alertSet;
    }

    private static CDCAlert processAlert(String sDate, String sAlert, String alertLevel) {

        String cdcDisease = "", cdcDiseaseExtra = null, cdcPlace = "", cdcPlaceExtra = null, cdcDate = "";
        int cdcAlertLevel = getLevelFromString(alertLevel);
        //Convert date to dd/MM/yyyy
        cdcDate = UtilsCommon.formatDate(sDate, inputDateFromat, outputDateFormat);
        //Separate disease type from place split by "in"
        String[] parts = sAlert.split(DISEASE_PLACE_SEPARATOR);
        if(parts.length == 2){
            String sInBrackets = "";
            //Extract possible () words in the disease
            sInBrackets = UtilsCommon.getTextInBrackets(parts[0]);
            cdcDisease = parts[0].replace(sInBrackets, MACRO.EMPTY).trim();
            if(!sInBrackets.equals(MACRO.EMPTY)){
                cdcDiseaseExtra = sInBrackets.replace("(", MACRO.EMPTY).replace(")", MACRO.EMPTY);
            }
            //Extract possible () words in the place
            sInBrackets = UtilsCommon.getTextInBrackets(parts[1]);
            cdcPlace = parts[1].replace(sInBrackets, MACRO.EMPTY).trim();
            if(!sInBrackets.equals(MACRO.EMPTY)){
                cdcPlaceExtra = sInBrackets.replace("(", MACRO.EMPTY).replace(")", MACRO.EMPTY);
            }
        }
        else{
            log.error("More than two parts in alert: "+sAlert);
        }
        if(cdcDisease.equals("")){
            return null;
        }
        return new CDCAlert(cdcDisease,cdcDiseaseExtra,cdcPlace, cdcPlaceExtra, cdcDate,
                            cdcAlertLevel, CDCAlert.getWeightFromLevel(cdcAlertLevel));
    }

    private static int getLevelFromString(String level){
        Matcher matcher = Pattern.compile("\\d+").matcher(level);
        if(matcher.find()){
            return Integer.parseInt(matcher.group());
        }
        return 0;
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
