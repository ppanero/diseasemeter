package com.danalyzer.newspaper;

import com.danalyzer.common.MACRO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Light on 16/12/15.
 */
public class ElMundoNewspaper extends Newspaper {

    public ElMundoNewspaper(String url) {
        super(url);
    }

    @Override
    public List<News> getWebConent() {
        List<News> news = new ArrayList<News>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements newsElems = doc.select("article");
            System.out.println(newsElems.size());
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
                    System.out.println(strTopic);
                    System.out.println(strTitle);
                    System.out.println(link);
                    news.add(new ElMundoNews(strTopic, strTitle, MACRO.MISSING_VALUE,MACRO.MISSING_VALUE
                            ,MACRO.MISSING_VALUE , link));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return news;
    }


    //Main for testing
    public static void main(String[] args){
        new ElMundoNewspaper("http://www.elmundo.es/salud.html").getWebConent();
    }
}
