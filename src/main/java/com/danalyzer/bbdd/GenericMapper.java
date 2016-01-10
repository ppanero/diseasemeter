package com.danalyzer.bbdd;

import com.danalyzer.common.MACRO;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Light on 02/01/16.
 */
public abstract class GenericMapper <T>{

    protected HiveJdbcClient jdbcClient;

    protected abstract String getTableName();
    protected abstract String[] getColumnNames();
    protected abstract String[] getSerializedObjectAsString(T object) ;

    public GenericMapper(HiveJdbcClient jdbcClient){
        this.jdbcClient = jdbcClient;
    }

    public void insert(T object){
        Statement stmt = null;
        try {
            stmt = jdbcClient.getConnection().createStatement();
            String tableName = getTableName();
            String[] columnNames = getColumnNames();
            String[] serObj = getSerializedObjectAsString(object);
            stmt.execute("INSERT INTO TABLE " + tableName + " ("+String.join(MACRO.COMMA, columnNames)+") " +
                    "VALUES ("+String.join(MACRO.COMMA, serObj)+")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
