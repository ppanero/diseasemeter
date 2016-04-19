package com.diseasemeter.data_colector.bbdd.mongodb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by Light on 14/04/16.
 */

public class MongoDBController {


    private static final ApplicationContext ctx = new GenericXmlApplicationContext("SpringConfig.xml");
    private static MongoOperations mongoOperation = (MongoOperations)ctx.getBean("mongoTemplate");

    public static MongoOperations getMongoOperations(){
        if(mongoOperation == null){
            mongoOperation = (MongoOperations)ctx.getBean("mongoTemplate");
        }
        return mongoOperation;
    }

    public static Criteria createCriteria(String attr, MongoComparation cmp, Object value){
        switch (cmp){
            case EQ:
                return Criteria.where(attr).is(value);

            case LT:
                return Criteria.where(attr).lt(value);

            case GT:
                return Criteria.where(attr).gt(value);

            case LTE:
                return Criteria.where(attr).lte(value);

            case GTE:
                return Criteria.where(attr).gte(value);

            case NE:
                return Criteria.where(attr).ne(value);

            default:
                return Criteria.where(attr).is(value);
        }

    }
}
