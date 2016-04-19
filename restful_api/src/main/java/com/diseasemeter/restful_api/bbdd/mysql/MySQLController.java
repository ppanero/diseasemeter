package com.diseasemeter.restful_api.bbdd.mysql;


import com.diseasemeter.restful_api.resources.disease.Disease;
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
