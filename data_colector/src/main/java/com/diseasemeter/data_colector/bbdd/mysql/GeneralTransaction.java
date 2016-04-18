package com.diseasemeter.data_colector.bbdd.mysql;


import com.diseasemeter.data_colector.bbdd.resources.mysql.GeneralResource;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;


/**
 * Created by Light on 17/04/16.
 */
public abstract class GeneralTransaction<K extends GeneralResource> {


    public abstract Object[] getKeyColumnNames();

    public boolean insert(K data){
        Session session = MySQLController.getSessionFactory().openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            session.save(data);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            return false;
        }finally {
            session.close();
        }
        return true;
    }

    public boolean exists(K data){
        Session session = MySQLController.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(data.getClass());

        Object[] keyValues = data.getKeyValues();
        Object[] keyColumnNames = getKeyColumnNames();
        //Assuming the two previous Object[] have the same length

        for(int i = 0; i < keyValues.length; ++i){
            criteria.add(Restrictions.eq(keyColumnNames[i].toString(), keyValues[i]));
        }

        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            List<K> list = criteria.list();
            tx.commit();
            return(list.size() > 0);
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            return false;
        }finally {
            session.close();
        }
    }

    public boolean update(K data){
        Session session = MySQLController.getSessionFactory().openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            session.update(data);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            return false;
        }finally {
            session.close();
        }
        return true;
    }

    public boolean delete(K data){
        Session session = MySQLController.getSessionFactory().openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            session.delete(data);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            return false;
        }finally {
            session.close();
        }
        return true;
    }

    public K findByKey(K data){
        Session session = MySQLController.getSessionFactory().openSession();
        Transaction tx = null;
        K object = null;
        try{
            tx = session.beginTransaction();
            object = (K) session.load(data.getClass(), data.getKey());
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            return null;
        }finally {
            session.close();
        }
        return object;
    }

    public void shutdown(){
        MySQLController.shutdown();
    }

}
