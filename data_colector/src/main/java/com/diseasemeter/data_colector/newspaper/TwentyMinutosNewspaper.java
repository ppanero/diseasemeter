package com.diseasemeter.data_colector.newspaper;

import com.diseasemeter.data_colector.common.MACRO;
import com.diseasemeter.data_colector.common.UtilsCommon;
import com.diseasemeter.data_colector.common.UtilsFS;
import com.diseasemeter.data_colector.common.UtilsWeb;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Light on 16/12/15.
 */
public class TwentyMinutosNewspaper extends Newspaper {


    private static Logger log = Logger.getLogger(TwentyMinutosNewspaper.class);
    private static final String BASE_FILENAME = "20minutos";

    public TwentyMinutosNewspaper(String url) {
        super(url);
    }

    @Override
    public Set<String> getWebConent() {
        Set<String> news = new HashSet<String>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements newsElems = doc.select("#content .title");
            for(Element singleNew : newsElems) {
                String strTopic = MACRO.MISSING_VALUE;
                String strTitle = singleNew.attr("title");
                String link = singleNew.attr("href");
                //if(strTopic.equals(MACRO.EMPTY)) strTopic = MACRO.MISSING_VALUE; //Not needed in this case
                if(strTitle.equals(MACRO.EMPTY)) strTitle = MACRO.MISSING_VALUE;
                if(!UtilsWeb.isValidUrl(link)) link = MACRO.MISSING_VALUE;

                news.add(strTitle.concat(MACRO.FILE_SEPARATOR)
                        .concat(strTopic).concat(MACRO.FILE_SEPARATOR)
                        .concat(link));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return news;
    }


    //Main for testing
    public static void main(String[] args){
        String outDir = "";
        //Read input arguments
        if (args.length != 4) {
            System.out.printf("Usage: TwentyMinutosNewspaper -o <output dir> -u <url> \n");
            log.error("Exit program with code (-1). Insufficient calling arguments");
            System.exit(-1);
        }
        // create Options object
        Options options = new Options();
        addOptions(options);
        Newspaper newspaper = new TwentyMinutosNewspaper("http://www.20minutos.es/salud/");

        Map<String, String> parsedArgs = UtilsCommon.getArgs(options, args);

        String outVal = parsedArgs.get("o");
        if(outVal != null && !outVal.equals(MACRO.SPACE)){
            outDir = UtilsFS.preparePath(outVal, false);
            log.debug("Output directory set to: " + outDir);
        }

        String webUrl = parsedArgs.get("u");
        if(webUrl != null && !webUrl.equals(MACRO.SPACE) && UtilsWeb.isValidUrl(webUrl)){
            newspaper.setUrl(webUrl);
        }

        newspaper.saveNews(newspaper.getWebConent(), outDir , BASE_FILENAME);
    }

    private static void addOptions(Options options) {
        options.addOption("o", true, "output directory");
        options.addOption("u", true, "url of 20 minutos newspaper health section");
    }
}
