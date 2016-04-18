package com.diseasemeter.data_colector.bbdd.mysql;

import com.diseasemeter.data_colector.bbdd.resources.mysql.CDCData;

/**
 * Created by Light on 17/04/16.
 */
public class CDCDataTransaction extends GeneralTransaction<CDCData>{

    private static final String[] KEY_COLUMN_NAMES = {"cdcDataKey"};

    @Override
    public Object[] getKeyColumnNames() {
        return KEY_COLUMN_NAMES;
    }
}
