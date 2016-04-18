package com.diseasemeter.data_colector.bbdd.mysql;


import com.diseasemeter.data_colector.bbdd.resources.mysql.CDCData;
import com.diseasemeter.data_colector.bbdd.resources.mysql.Disease;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Created by Light on 13/04/16.
 */
public class MySQLController {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if(sessionFactory == null){
            try{
                sessionFactory = new Configuration().
                                    configure().
                                    addAnnotatedClass(Disease.class).
                                    addAnnotatedClass(CDCData.class).
                                    buildSessionFactory();
            }catch (Throwable ex) {
                System.err.println("Failed to create sessionFactory object." + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
        return sessionFactory;
    }

    public static void shutdown(){
        if(!sessionFactory.isClosed())
            sessionFactory.close();
    }

}
