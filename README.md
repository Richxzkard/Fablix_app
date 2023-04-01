- # General
    - #### Team#: 58
    
    - #### Names: Huaiwu Li; Zikang Xiong
    
    - #### Project 5 Video Demo Link:
        https://youtu.be/52pxB0CsC2s

    - #### Instruction of deployment:
         Ensure the Environment is set up:
            > Java11
            > mysql8
            > Tomcat9

         After setting up the environment, load the repository using command below;
          git clone https://github.com/uci-jherold2-teaching/cs122b-fall-team-58.git

         Login to the mysql to create and populate the movie database, head to the repository directory and use following commands;
            <!-- create the datababase moviedb -->
            mysql -u mytestuser-p < create_table.sql

            <!-- populate the database -->
            mysql -u mytestuser -p --database=moviedb < movie-data.sql

        Head to the repository directory, and depoly the Application using commands;
            <!-- create the war file -->
            mvn package

            <!-- copy the newly built war file -->
            cp ./target/*.war /var/lib/tomcat9/webapps/

            <!-- show tomcat web apps, it should now have the new war file -->
            ls -lah /var/lib/tomcat9/webapps/

         The application is successfully deployed. Open the browser and go to the tomcat manager page, the application deployed should be shown named "cs122b-fall-team-58". Click on the application and the movie list page will be shown.

    - #### Collaborations and Work Distribution:

        Huaiwu Li:
        > Set up AWS environment;
        > Created the movielist page and finished jumping functionalities;
        > Implemented the login & session features;
        > Implemented the shopping cart features;
        > Implemented the additional functionalities;
        > Implemented front-end asthetics;
        > Implemented Recaptcha, domain registration, Https registration, data encryption, dashboard page and stored procedures;

        Zikang Xiong:
        > Set up mysql database;
        > Populated the mysql database;
        > Implemented the main page features;
        > Implemented the payment page and place order features;
        > Implemented README file and video submission;
        > Implemented XML parsing features and talked to database using script;

- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    
      The filename/path of all code/configuration files are listed below.
      
      [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/WebContent/META-INF/context.xml](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/WebContent/META-INF/context.xml)
      
      [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/CartServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/CartServlet.java)
      
      [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/DashboardServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/DashboardServlet.java)
      
      [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/FullTextSearchServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/FullTextSearchServlet.java)
      
      [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/LoginServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/LoginServlet.java)
      
      [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/MoviesServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/MoviesServlet.java)
      
      [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/PayServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/PayServlet.java)
      
      [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/SearchServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/SearchServlet.java)
      
      [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/SingleMovieServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/SingleMovieServlet.java)
      
      [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/SingleStarServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/SingleStarServlet.java)
      
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    
     When we use connection pooling, we get a connection from dataSource and let resource manager close the connection after usage. So when we need to use 
     again, we just need to use the previous connection. If we never use the connection for datasource, so we save time by resuing the datasource.
     
    - #### Explain how Connection Pooling works with two backend SQL.
    
     If we want to use two backend SQL, we need to declare two resource in [context.xml](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/WebContent/META-INF/context.xml), we need to specify different url and different names. Then we need to declare two sql resource-ref in [web.xml](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/WebContent/WEB-INF/web.xml),then we need to configure init function in different servlets to use different datasource with different url.

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
    


    
    
        # master

        [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/CartServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/CartServlet.java)

        [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/DashboardServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/DashboardServlet.java)

        [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/FullTextSearchServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/FullTextSearchServlet.java)

        [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/LoginServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/LoginServlet.java)

        [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/MoviesServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/MoviesServlet.java)


        # slave

        [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/PayServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/PayServlet.java)

        [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/SearchServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/SearchServlet.java)

        [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/SingleMovieServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/SingleMovieServlet.java)

        [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/SingleStarServlet.java](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/src/SingleStarServlet.java)

         # configure

        [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/WebContent/META-INF/context.xml](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/WebContent/META-INF/context.xml)

        [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/WebContent/WEB-INF/web.xml](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/WebContent/WEB-INF/web.xml)
    
    - #### How read/write requests were routed to Master/Slave SQL?
       
       Servlets which have the write requests will be lead to master sql, and servlets which have read requests will be lead to master sql or slave sql.
       To making sure load is balanced, all servlets are distributed to master sql and slave sql with close numbers 5 and 4.

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
    
        [https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/log_processing.py](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/log_processing.py)
        
        The script process each line of log file, and calculate TS and TJ. And turn TS and TJ to seconds, finally calculate the average of TS and TJ and print them out.

- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![1.png](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/img/1.png)   | 168                         | 38.757                                  | 38.134                        | Average query time is 168 which is the least           |
| Case 2: HTTP/10 threads                        | ![2.png](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/img/2.png)   | 283                         | 51.772                                  | 51.426                        | Average query time is 283 which is greater than 1 thread time, since the server becomes busy.           |
| Case 3: HTTPS/10 threads                       | ![3.png](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/img/3.png)   | 443                         | 45.057                                  | 44.617                        | Average query time is 443 which is greater than 10 thread http time after adding security restriction.           |
| Case 4: HTTP/10 threads/No connection pooling  | ![4.png](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/img/4.png)   | 643                         | 89.973                                  | 89.647                        | Average query time is 643 which is the greatest in this column, since the pooling is retrieved.           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![5.png](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/img/5.png)   | 164                         | 40.147                                  | 39.296                        | Average query time is nearly equal to the single-instance version, since there's only one thread active. Load balancer isn't effective at this time.           |
| Case 2: HTTP/10 threads                        | ![6.png](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/img/6.png)   | 171                         | 48.732                                  | 48.335                        | Average query time is higher than the 1 thread test, but still less than the single-instance 10 thread version. Load balancer has taken effort.           |
| Case 3: HTTP/10 threads/No connection pooling  | ![7.png](https://github.com/uci-jherold2-teaching/cs122b-fall-team-58/blob/main/img/7.png)   | 209                         | 66.225                                  | 65.875                        | Average query is the greatest among this column, the reason is similar to above that pooling is disabled.           |
