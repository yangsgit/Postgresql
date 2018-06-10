Yang Li 10427039

1.Contents of assignment package:

README.txt
Instruction of Assignment

postgresql-42.1.4.jar
JDBC Driver

Question1.java    
The java program of assignment for question 1.

Question2.java
The java program of assignment for question 2.

Question3.java
The java program of assignment for question 3.


question1.sql 
The sql query sentence of question 1
 
question1.csv
The output of question 1. It can be opened by excel application

question2.sql
The sql query sentence of question 2

question2.csv
The output of question 2. It can be opened by excel application

question3.sql
The sql query sentence of question 2

question3.csv
The output of question 2. It can be opened by excel application

2.Program

2.1 Firstly, to connect your own database, you should open .java file separately and 
change the attributes url, user, password in each class

2.2 For running java program by command line, you should first go into the Path of this package, which contains all the files, by using :

  	cd /(the path of Assignment Package in your computer)/YangLi_10427039

Then use following command line to compile  :

	javac -cp postgresql-42.1.4.jar Question1.java

Finally, use following command line to run program

	java -cp .:postgresql-42.1.4.jar Question1

Run three program using this way separately (change the "Question1" to "Question2/3) 

2.3 For running java program in IDE, you should import postgresql-42.1.4.jar in advance.
Then import three .java file in your project and run them separately. 


3.SQL

Import the question1.sql, question2.sql and question3.sql into pgAdmin and run them to get result separately.




