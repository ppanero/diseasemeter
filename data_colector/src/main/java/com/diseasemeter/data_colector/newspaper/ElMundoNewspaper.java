package com.diseasemeter.data_colector.newspaper;

import com.diseasemeter.data_colector.bbdd.resources.mysql.News;
import com.diseasemeter.data_colector.bbdd.resources.mysql.NewsKey;
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
import java.util.*;

/**
 * Created by Light on 16/12/15.
 */
public class ElMundoNewspaper extends Newspaper {


    private static Logger log = Logger.getLogger(ElMundoNewspaper.class);
    private static final String SOURCE = "elmundo";

    public ElMundoNewspaper(String url) {
        super(url, SOURCE);
    }

    @Override
    public List<News> getWebConent() {
        List<News> news = new ArrayList<News>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements newsElems = doc.select("article");
            for(Element singleNew : newsElems) {
                if (singleNew.className().contains("noticia")) {
                    Elements topic = singleNew.select("header").select("a");
                    String strTopic = null;
                    String strTitle = MACRO.MISSING_VALUE;
                    String link = MACRO.MISSING_VALUE;
                    if (topic.size() > 1) {
                        strTopic = topic.get(0).getElementsByIndexEquals(0).text();
                        strTitle = topic.get(1).getElementsByIndexEquals(0).text();
                        link = topic.get(1).attr("href");
                    } else if (topic.size() == 1) {
                        strTitle = topic.get(0).getElementsByIndexEquals(0).text();
                        link = topic.get(0).attr("href");
                    }
                    news.add(new News(new NewsKey(strTitle, null), link, strTopic, SOURCE));
                }
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
            System.out.printf("Usage: ElMundoNewspaper -u <url> \n");
            log.error("Exit program with code (-1). Insufficient calling arguments");
            System.exit(-1);
        }
        // create Options object
        Options options = new Options();
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        addOptions(options);
        Newspaper newspaper = new ElMundoNewspaper("http://www.elmundo.es/salud.html");
        Map<String, String> parsedArgs = UtilsCommon.getArgs(options, args);

        String webUrl = parsedArgs.get("u");
        if(webUrl != null && !webUrl.equals(MACRO.SPACE) && UtilsWeb.isValidUrl(webUrl)){
            newspaper.setUrl(webUrl);
        }
        newspaper.saveNews(newspaper.getWebConent());
    }

    private static void addOptions(Options options) {
        options.addOption("u", true, "url of El Mundo newspaper health section");
    }
}
