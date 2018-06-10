Yang Li 10427039

Contents of assignment package:

README.txt
Instruction of Assignment

postgresql-42.1.4.jar
JDBC Driver

Assignment.java
The java program of assignment, containing question 1 and question 2.

question1.sql 
The sql query sentence of question 1

question1.csv
The output of question 1. It can be opened by excel application

question2.sql
The sql query sentence of question 2

question2.csv
The output of question 2. It can be opened by excel application



1.Program

1.1 Firstly, to connect your own database, you should open Assignment.java and 
change the attributes url, user, password in class Assignment

1.2 For running java program by command line, you should first go into the Path of Assignment package, which contains all the files, by using

  	cd /(the path of Assignment Package in your computer)/Assignment

Then use following command line to compile 

	javac -cp postgresql-42.1.4.jar Assignment.java

Finally, use following command line to run program

	java -cp .:postgresql-42.1.4.jar Assignment

Both result of question1 and question2 will appear on terminal

1.3 For running java program in IDE, you should import postgresql-42.1.4.jar
in your project in advance. Then run Assignment class.


2.SQL

Import the question1.sql and question2.sql into pgAdmin and run them to get result separately.




