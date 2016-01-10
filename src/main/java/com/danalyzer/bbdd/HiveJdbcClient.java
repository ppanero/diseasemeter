package com.danalyzer.bbdd;

import com.danalyzer.common.MACRO;
import com.danalyzer.common.UtilsCommon;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Light on 02/01/16.
 */
public class HiveJdbcClient {

    protected static Logger log = Logger.getLogger(HiveJdbcClient.class);
    protected static Properties properties;
    protected Connection conn;
    protected boolean connected;

    public HiveJdbcClient(String filename){
        properties = UtilsCommon.readProperties(filename);
        connected = false;
    }

    public Connection getConnection(){
        if(connected) {
            return conn;
        }
        else{
            log.error("Unable to connect to database server with the specified properties. Retrieving null connection.");
            return null;
        }
    }

    public boolean connect(){
        if(!isConnected()) {
            if (checkClass()) {
                String ip = properties.getProperty(MACRO.HIVE_IP);
                String port = properties.getProperty(MACRO.HIVE_PORT);
                String user = properties.getProperty(MACRO.HIVE_USER);
                String database = properties.getProperty(MACRO.HIVE_DATABASE);
                try {
                    conn = DriverManager.getConnection("jdbc:hive2://" + ip + ":" + port + "/" + database, user, "");
                    connected = true;
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    log.error("Could not create connection.\n" + e.getMessage());
                    connected = false;
                    return false;
                }
            } else {
                connected = false;
                return false;
            }
        }
        return true;
    }

    public boolean disconnect(){
        try {
            if(!conn.isClosed()) {
                conn.close();
                connected = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Could not close jdbc connection.\n" + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean isConnected(){
        return connected;
    }

    private static boolean checkClass(){
        try {
            Class.forName(properties.getProperty(MACRO.HIVE_DRIVER));
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error("JDBC driver could not be found.\n" + e.getMessage());
            return false;
        }
    }

}
