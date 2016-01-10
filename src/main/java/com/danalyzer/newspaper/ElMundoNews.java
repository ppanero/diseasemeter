package com.danalyzer.newspaper;

/**
 * Created by Light on 14/12/15.
 */
public class ElMundoNews extends News{

    private String topic;
    private String antetitulo;
    private String entradilla;

    public ElMundoNews(String topic, String title, String antetitulo, String entradilla, String content, String url) {
        super(url, title, content);
        this.topic = topic;
        this.antetitulo = antetitulo;
        this.entradilla = entradilla;
    }

    public String getTopic() {
        return topic;
    }

    public String getAntetitulo() {
        return antetitulo;
    }

    public String getEntradilla() {
        return entradilla;
    }
}
