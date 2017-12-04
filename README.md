# juniorDataEngineerTask

1.
Stack architecture:
- Storage: HDFS
- Data processor: Hive on Spark/Hive on Tez
- Application: Python
- Data Frame: Pandas

Since we want to generate blog traffic stats per day we should fetch the data once per day. Ideally at midnight (server time) but since we can't account on the server being available we should implement a retry logic which would retry to submit the job at the predefined frequency until succeeding. Once we have retrieved the .csv file it should be read into a Pandas date frame
once it can be inspected structure and data wise. If structure and data correspond with the format we are expecting the .csv file should be stored in HDFS. If not, depending on the business logic, we should either contact the data provider if data is missing, is corrupted or if any additional columns have appeared. Or delete the excess columns and reorder the requested ones if need be and said data frame should be exported into a new .csv file which will be stored in HDFS. Once the traffic stats are requested for a given day we would select two .csv files using their upload timestamps. If, for instance, the requested stats are for date 10.10. we should fetch the files received at ~midnight of 11.10. and 10.10. and calculating deltas on those two files will give us the usable values we want. Two files in question should be imported into Hive ORC and using HQL query to submit an MR job to Hadoop we can calculate the desired delta values which will then be displayed to the user. This would be my solution if my assumption that the user wants to retrieve stats exclusively for one or more days is correct. The other scenario in which the dataset should present stats per day for all days at once the design changes as follows: Once inspected and corrected the file should be stored into HDFS but the Hive ORC should be generated immediately at this point, one for each file. Then the MR jobs will be submitted on the whole dataset.


Reasoning for using HDFS: Since we are receiving lifetime aggregated data all of the previously received files should be stored in the data warehouse. Since we won't be updating any of those files and all of them are expected to be used in the future HDFSs write once/read multiple times principle is ideal for this scenario.

Reasoning for using Hive on Hadoop: Reason number of one is Hives abstract resemblance with traditional SQL oriented RDMBSs which makes it suitable for this case when we are working with structured data. Additionally, this is a classic data warehousing environment in which Hive has already proved it's worth. We aren't going the process data streams in real time but retroactively analyze previously stored data which is a job MapReduce processor is very good at.

Reasoning for using Python Pandas: Because we cannot rely on the consistency of the file structure and the integrity of data we are receiving we need a way of inspecting the received file on the application level before storing the file. Pandas provides proven methods of parsing and manipulating .csv files using data frames. Since I'm not very familiar with Python other alternatives would be to either manually parse and inspect the file in Java (which, at this moment I'm most familiar with) or implementing a stack which uses Spark data frames on top of Java. However, during my research in the past days I've come to a conclusion that Python Pandas provides the best solution for this scenario.


2a.
SELECT departmentFamily AS "Department Family", SUM(apAmount) AS "Total Expenditure"
FROM combExpenditureOverThreshold
GROUP BY departmentFamily;

2b.
SELECT departmentFamily AS "Department Family", expenseType AS "Expense Type", SUM(apAmount) AS "Total Expenditure"
FROM combExpenditureOverThreshold
GROUP BY departmentFamily, expenseType;

3.
DB: PostgreSQL
Application language: Java
Build tool: Ant
DB initialization script is located in juniorDataEngineerTask/piratedbInit.sql

Solution consists of using the PostgreSQLs table function crosstab() to pivot the table and display the result in the requested way. I wrote a primitive Java application which parses the file, dynamically generates all the queries, imports the data into DB 
and runs the query:
SELECT * FROM crosstab('SELECT expenseType, expenseArea, SUM(apAmount) ' ||
                       'FROM combExpenditureOverThreshold ' ||
                       'GROUP BY expenseType, expenseArea ' ||
                       'ORDER BY 1', 'SELECT DISTINCT expenseArea FROM combExpenditureOverThreshold') 
                       AS ct(listOfAllExpenseAreas)
 Dataset is formatted and displayed in the standard output stream.
