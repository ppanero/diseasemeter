package com.diseasemeter.data_colector.bbdd.mysql;

import com.diseasemeter.data_colector.bbdd.resources.mysql.News;

/**
 * Created by Light on 23/04/16.
 */
public class NewsTransaction extends GeneralTransaction<News> {

    private static final String[] KEY_COLUMN_NAMES = {"newsKey"};

    @Override
    public Object[] getKeyColumnNames() {
            return KEY_COLUMN_NAMES;
        }
}
