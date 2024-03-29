package com.diseasemeter.data_colector.newspaper;

import com.diseasemeter.data_colector.bbdd.resources.mysql.News;
import com.diseasemeter.data_colector.bbdd.resources.mysql.NewsKey;
import com.diseasemeter.data_colector.common.MACRO;
import com.diseasemeter.data_colector.common.UtilsCommon;
import com.diseasemeter.data_colector.common.UtilsWeb;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

/**
 * Created by Light on 16/12/15.
 */
public class ElPublicoNewspaper extends Newspaper {


    private static Logger log = Logger.getLogger(ElPublicoNewspaper.class);
    private static final String SOURCE = "elpublico";
    private static final String BASE_URL = "http://www.publico.es";

    public ElPublicoNewspaper(String url) {
        super(url, SOURCE);
    }

    @Override
    public List<News> getWebConent() {
        List<News> news = new ArrayList<News>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements newsElems = doc.select(".content .article .news-title");
            for(Element singleNew : newsElems) {
                String strTitle = singleNew.text();
                String link = BASE_URL.concat(singleNew.attr("href"));
                //if(strTopic.equals(MACRO.EMPTY)) strTopic = MACRO.MISSING_VALUE; //Not needed in this case
                if(strTitle.equals(MACRO.EMPTY)) strTitle = MACRO.MISSING_VALUE;
                if(!UtilsWeb.isValidUrl(link)) link = MACRO.MISSING_VALUE;

                news.add(new News(new NewsKey(strTitle, null), link, null, SOURCE));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return news;
    }


    //Main for testing
    public static void main(String[] args){
        //Read input arguments
        if (args.length != 2) {
            System.out.printf("Usage: ElPublicoNewspaper -u <url> \n");
            log.error("Exit program with code (-1). Insufficient calling arguments");
            System.exit(-1);
        }
        // create Options object
        Options options = new Options();
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        addOptions(options);
        Newspaper newspaper = new ElPublicoNewspaper("http://www.publico.es/sociedad/sanidad");

        Map<String, String> parsedArgs = UtilsCommon.getArgs(options, args);

        String webUrl = parsedArgs.get("u");
        if(webUrl != null && !webUrl.equals(MACRO.SPACE) && UtilsWeb.isValidUrl(webUrl)){
            newspaper.setUrl(webUrl);
        }
        newspaper.saveNews(newspaper.getWebConent());
    }

    private static void addOptions(Options options) {
        options.addOption("u", true, "url of El Publico newspaper health section");
    }
}
