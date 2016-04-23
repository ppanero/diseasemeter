package com.diseasemeter.data_colector.bbdd.mysql;



import com.diseasemeter.data_colector.bbdd.resources.mysql.Disease;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by Light on 13/04/16.
 */
public class DiseaseTransaction extends GeneralTransaction<Disease>{

    //MySQL schema properties
    private static final String[] KEY_COLUMN_NAMES = {"diseaseKey"};
    //--
    private static final String SELECT_QUERY = "FROM Disease";
    private static final String WHERE_CLAUSE = " WHERE ";
    private static final String AND_CLAUSE = " AND ";
    private static final String NAME_PARAM = "location = :location";
    private static final String DATE_PARAM = "STR_TO_DATE(:date, '%d/%m/%Y') BETWEEN STR_TO_DATE(initial_date, '%d/%m/%Y') AND STR_TO_DATE(last_update, '%d/%m/%Y')";
    private static final String ORBER_BY_CLAUSE = " ORDER BY active DESC, level DESC";
    //String cheking
    private static final String DATE_REGEX = "^(0[1-9]|[12][0-9]|3[01])\\/(0[1-9]|1[012])\\/(19|20)\\d\\d$";
    private static final Pattern pattern = Pattern.compile(DATE_REGEX);


    @Override
    public Object[] getKeyColumnNames() {
        return KEY_COLUMN_NAMES;
    }

    /* Method to  READ all the employees */
    public static List<Disease> listDiseases(String zone, String date){
        Session session = MySQLController.getSessionFactory().openSession();
        Transaction tx = null;
        List<Disease> diseases = new ArrayList<Disease>();
        try{
            String queryString = SELECT_QUERY;
            boolean pName = false, pDate = false;
            if(zone != null && !zone.equals("")){
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
            queryString = queryString.concat(ORBER_BY_CLAUSE);
            Query query = session.createQuery(queryString);
            if(pName) query.setParameter("location",zone);
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


    public List<Disease> getAllByName(Disease disease) {
        Session session = MySQLController.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(disease.getClass());

        criteria.add(Restrictions.eq("diseaseKey._name", disease.getDiseaseKey().getName()));

        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            List<Disease> list = criteria.list();
            tx.commit();
            return list;
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            return new ArrayList<Disease>();
        }finally {
            session.close();
        }

    }

    private static boolean isDateFormatted(String date) {
        return pattern.matcher(date).find();
    }


}
