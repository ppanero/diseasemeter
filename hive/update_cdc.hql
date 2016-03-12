INSERT INTO cdc_notices SELECT cdc_periodic.* FROM cdc_periodic, cdc_notices WHERE cdc_periodic.alert_date != cdc_notices.alert_date OR cdc_periodic.disease != cdc_notices.disease OR cdc_periodic.area != cdc_notices.area;


SELECT * FROM cdc_periodic WHERE NOT EXISTS (SELECT alert_date, area, disease FROM cdc_notices WHERE cdc_periodic.alert_date = cdc_notices.alert_date OR cdc_periodic.disease = cdc_notices.disease OR cdc_periodic.area = cdc_notices.area);


SELECT cdc_periodic.* FROM cdc_periodic, cdc_notices WHERE cdc_periodic.alert_date != cdc_notices.alert_date OR cdc_periodic.disease != cdc_notices.disease OR cdc_periodic.area != cdc_notices.area GROUP BY cdc_periodic.alert_date, cdc_periodic.disease, cdc_periodic.area;;

//try group by titulo, o intereccion

