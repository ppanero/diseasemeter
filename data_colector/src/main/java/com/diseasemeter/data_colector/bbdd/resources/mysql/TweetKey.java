package com.diseasemeter.data_colector.bbdd.resources.mysql;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by Light on 24/04/16.
 */
public class TweetKey implements Serializable {

    private String _content;
    private String _user_name;
    private String _disease;

    public TweetKey() {
    }

    public TweetKey(String _content, String _user_name, String _disease) {
        this._content = _content;
        this._user_name = _user_name;
        this._disease = _disease;
    }

    public String getContent() {
        return _content;
    }

    public void setContent(String _content) {
        this._content = _content;
    }

    public String getUserName() {
        return _user_name;
    }

    public void setUserName(String _user_name) {
        this._user_name = _user_name;
    }

    public String getDisease() {
        return _disease;
    }

    public void setDisease(String _disease) {
        this._disease = _disease;
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof TweetKey) ) return false;

        final TweetKey oKey = (TweetKey) other;

        if ( !oKey.getContent().equals( getContent() ) ) return false;
        if ( !oKey.getUserName().equals( getUserName() ) ) return false;
        if ( !oKey.getDisease().equals( getDisease() ) ) return false;

        return true;
    }

    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(_content);
        hcb.append(_user_name);
        hcb.append(_disease);

        return hcb.toHashCode();
    }
}
