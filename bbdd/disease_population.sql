"""
+---------------+-------------+------+-----+---------+-------+
| Field         | Type        | Null | Key | Default | Extra |
+---------------+-------------+------+-----+---------+-------+
| _name         | varchar(45) | NO   | PRI | NULL    |       |
| _location     | varchar(45) | NO   | PRI | NULL    |       |
| level         | int(11)     | NO   |     | NULL    |       |
| twitter_count | int(11)     | NO   |     | NULL    |       |
| news_count    | int(11)     | NO   |     | NULL    |       |
| cdc_count     | int(11)     | NO   |     | NULL    |       |
| last_update   | varchar(10) | NO   |     | NULL    |       |
| initial_date  | varchar(10) | NO   |     | NULL    |       |
| active        | tinyint(1)  | NO   |     | NULL    |       |
+---------------+-------------+------+-----+---------+-------+
"""

INSERT INTO disease (name, location, level, twitter_count, news_count, cdc_count, last_update, initial_date, active) VALUES 
                        ("Ebola", "Sierra Leona", 3, 237834, 20, 3,"27/02/2016","10/05/2015", 1), 
                        ("Zika", "Paraguay", 3, 137834, 10, 3, "05/03/2016", "12/03/2015",1),
                        ("Dengue", "Argentina", 2, 7834, 10, 2, "10/01/2016","10/06/2015", 1),
                        ("Malaria", "Brasil", 1, 834, 5, 1, "20/07/2015","27/03/2015",0);