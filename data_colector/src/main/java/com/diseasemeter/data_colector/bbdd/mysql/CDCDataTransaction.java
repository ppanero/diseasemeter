package com.diseasemeter.data_colector.bbdd.mysql;

import com.diseasemeter.data_colector.bbdd.resources.mysql.CDCData;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Created by Light on 17/04/16.
 */
public class CDCDataTransaction extends GeneralTransaction<CDCData>{

    private static final String[] KEY_COLUMN_NAMES = {"_name", "_location", "_date"};

    @Override
    public Object[] getKeyColumnNames() {
        return KEY_COLUMN_NAMES;
    }
}
