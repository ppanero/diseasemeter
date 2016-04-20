package com.diseasemeter.data_colector.newspaper;

import com.diseasemeter.data_colector.common.MACRO;
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
import java.util.List;
import java.util.Set;

/**
 * Created by Light on 16/12/15.
 */
public class ElMundoNewspaper extends Newspaper {


    private static Logger log = Logger.getLogger(ElMundoNewspaper.class);
    private static final String BASE_FILENAME = "elmundo";

    public ElMundoNewspaper(String url) {
        super(url);
    }

    @Override
    public List<News> getWebConent() {
        Set<String> news = new HashSet<String>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements newsElems = doc.select("article");
            for(Element singleNew : newsElems) {
                if (singleNew.className().contains("noticia")) {
                    Elements topic = singleNew.select("header").select("a");
                    String strTopic = MACRO.MISSING_VALUE;
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
                    news.add(strTitle.concat(MACRO.FILE_SEPARATOR)
                            .concat(strTopic).concat(MACRO.FILE_SEPARATOR)
                            .concat(link));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //Main for testing
    public static void main(String[] args){
        String outDir = "";
        //Read input arguments
        if (args.length != 4) {
            System.out.printf("Usage: ElMundoNewspaper -o <output dir> -u <url> \n");
            log.error("Exit program with code (-1). Insufficient calling arguments");
            System.exit(-1);
        }
        // create Options object
        Options options = new Options();
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        addOptions(options);
        Newspaper newspaper = new ElMundoNewspaper("http://www.elmundo.es/salud.html");
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
                    newspaper.setUrl(webUrl);
                }
            }
        }

        //newspaper.saveNews(newspaper.getWebConent());
    }

    private static void addOptions(Options options) {
        options.addOption("o", true, "output directory");
        options.addOption("u", true, "url of El Mundo newspaper health section");
    }
}
