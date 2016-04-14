package com.diseasemeter.restful_api.bbdd;

import com.diseasemeter.restful_api.resources.Disease;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Light on 13/04/16.
 */
public class DiseaseTransaction {

    private static final String SELECT_QUERY = "FROM Disease";
    private static final String WHERE_CLAUSE = " WHERE ";
    private static final String AND_CLAUSE = " AND ";
    private static final String NAME_PARAM = "name = :name";
    private static final String DATE_PARAM = "STR_TO_DATE(:date, '%d/%m/%Y') BETWEEN STR_TO_DATE(initial_date, '%d/%m/%Y') AND STR_TO_DATE(last_update, '%d/%m/%Y')";
    //String cheking
    private static final String DATE_REGEX = "^(0[1-9]|[12][0-9]|3[01])\\/(0[1-9]|1[012])\\/(19|20)\\d\\d$";
    private static final Pattern pattern = Pattern.compile(DATE_REGEX);

    /* Method to  READ all the employees */
    public static List<Disease> listDiseases(String name, String date){
        Session session = MySQLController.getSessionFactory().openSession();
        Transaction tx = null;
        List<Disease> diseases = new ArrayList<>();
        try{
            String queryString = SELECT_QUERY;
            boolean pName = false, pDate = false;
            if(name != null && !name.equals("")){
                pName = true;
                queryString = queryString.concat(WHERE_CLAUSE).concat(NAME_PARAM);
            }
            if(date != null && isDateFormatted(date)){
                if(pName){
                    queryString = queryString.concat(AND_CLAUSE);
                }
                else{
                    queryString = queryString.concat(WHERE_CLAUSE);
                }
                pDate = true;
                queryString = queryString.concat(DATE_PARAM);
            }

            Query query = session.createQuery(queryString);
            if(pName) query.setParameter("name",name);
            if(pDate) query.setParameter("date", date);

            tx = session.beginTransaction();
            diseases = query.list();
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return diseases;
    }

    private static boolean isDateFormatted(String date) {
        return pattern.matcher(date).find();
    }
}
