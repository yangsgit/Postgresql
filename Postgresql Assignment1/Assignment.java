import java.sql.*;
import java.util.ArrayList;


/**
 *
 * 1.Compile and Run
 *
 * 1.1 Firstly, to connect your own database, you should open Assignment.java and
 * change the attributes url, user, password in class Assignment
 *
 * 1.2 For running java program by command line, you should first go into the Path of Assignment package,
 * which contains all the files, by using:
 *
 *  cd /(the path of Assignment Package in your computer)/Assignment
 *
 *  Then use following command line to compile:
 *
 *  javac -cp postgresql-42.1.4.jar Assignment.java
 *
 *  Finally, use following command line to run program:
 *
 *  java -cp .:postgresql-42.1.4.jar Assignment
 *
 *  Both result of question1 and question2 will appear on terminal
 *
 *  1.3 For running java program in IDE, you should import postgresql-42.1.4.jar
 *  in your project in advance. Then run Assignment class.
 *
 * 2. Detail of program
 *
 * Assignment.java contain one Main class to run program and and two nested class
 * Run program one time to read all data row by row and deal with it simultaneously
 * for question1 and question2
 *
 * Using ArrayList to store two result tables
 * Unlike array, ArrayList is no need to initialize the capacity, which is unknown
 * Because there are only 500 rows data, every search operation in arraylist
 * can be considered to O(1). If the table is very big and input is unlimited. I will use
 * hashtable that search operation can be averagely O(1)
 *
 * For question 1, after reading a row of data, check if the MaxMinAvg instance with same
 * cust in maxMinAvgArrayList, if yes, copare the quant to the max_q and min_q, if quant greater
 * than max_q or less than min_q, update it. Meanwhile, update the total number and average quantity
 * If no, create a new MaxMinAvg instance add it in maxMinAvgArrayList.
 *
 * For question 2, after reading a row of data, find every cust and prod whoes month equals 1,2 or 3
 * with the instance who has same cust and prod in janFebMarArrayList. If the readed quant great than the
 * jan_max or less than feb_min and mar_min update the instance in janFebMarArrayList. If there is
 * no instance with same cust and prod in janFebMarArrayList, create one add in janFebMarArrayList
 */


/**
 * this class is to map output of question one, most attributes is uniform with result table columns
 * one instance represent one row
 */
class MaxMinAvg {
    String cust;
    int max_q;
    String maxProd;
    String maxDate;
    String maxSt;
    int min_q;
    String minProd;
    String minDate;
    String minSt;
    int avg_q;
    int count;
    int sumOfQuant;

    public MaxMinAvg (String cust, int max_q, String maxProd, String maxDate, String maxSt, int min_q, String minProd, String minDate, String minSt) {
        this.cust = cust;
        this.max_q = max_q;
        this.maxProd = maxProd;
        this.maxDate = maxDate;
        this.maxSt = maxSt;
        this.min_q = min_q;
        this.minProd = minProd;
        this.minDate = minDate;
        this.minSt = minSt;
        this.avg_q = 0;
        this.count = 1;
        this.sumOfQuant = max_q;
        this.avg_q = this.sumOfQuant / this.count;
    }
}

/**
 * this class is to map output of question two, every attributes is uniform with result table columns
 * one instance represent one row
 */
class JanFebMar {
    String cust;
    String prod;
    int janMax;
    String janMaxDate;
    int febMin;
    String febMinDate;
    int marMin;
    String marMinDate;

    public JanFebMar (String cust, String prod, int janMax, String janMaxDate, int febMin, String febMinDate, int marMin, String marMinDate ,int month) {
        this.cust = cust;
        this.prod = prod;
        if (month == 1) {
            this.janMax = janMax;
            this.janMaxDate = janMaxDate;
        } else if (month == 2) {
            this.febMin = febMin;
            this.febMinDate = febMinDate;
        } else {
            this.marMin = marMin;
            this.marMinDate = marMinDate;
        }
    }
}

/**
 * This class is main class， it has a coonect function to connect database
 * Connect function call a readAndDealWith() function after connect successfully to read and deal with data
 */
public class Assignment {
    ArrayList<MaxMinAvg> maxMinAvgArrayList = new ArrayList<>();
    ArrayList<JanFebMar> janFebMarArrayList = new ArrayList<>();

    /**
     * url, user, password should be changed to connect your own Database
     */
    String url = "jdbc:postgresql://localhost:5432/postgres";
    String user = "postgres";
    String password = "9";

    //connet and execute
    public void connect() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Successfully loaded the driver!");
        } catch (Exception e) {
            System.out.println("Failed to load the driver!");
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.\n\n");

        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
        }
        try {
             //After connecting the Database, read and deal with Data
            readAndDealWithData(conn);
        } catch (SQLException e) {
            System.out.println("Connection URL or username or password errors!");
            System.out.println();
            System.out.println();
            e.printStackTrace();
        }
    }

    /**
     * This function use a while loop read data from database row by row
     * Then deal with data by two function : executeQuestionOne ; executeQuestionTwo
     * Finally print the two outcome
     * @param Connection  use Connection instance to get Statement and then execute query
     * @throws SQLException  throw exception if catch
     */
    private void readAndDealWithData(Connection conn) throws java.sql.SQLException{
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");
        // only read and iterate one time for entire data
        while (rs.next()) {
            // parse data
            String cust = rs.getString("cust");
            String prod = rs.getString("prod");
            int day = rs.getInt("day");
            int month = rs.getInt("month");
            int year = rs.getInt("year");
            String date = constructDate(month, day, year);
            String st = rs.getString("state");
            int quant = rs.getInt("quant");

            // execute qustion 1 of the assignment
            executeQuestionOne(cust, prod, date, st, quant);

            // execute qustion 2 of the assignment
            executeQuestionTwo(cust, prod, date, st, quant, month, year);
        }

        // print table of question 1
        printMaxMinAvg();

        // print table of question 2
        printJanFebMar();
    }

    /**
     * execute qustion 1, deal with the data of one row, then store or update it in arraylist
     * @param cust  The customer of one row in Sales
     * @param prod  The production of one row in Sales
     * @param date  The date created by function constructDate(), using month, day, year of one row in Sales
     * @param st    The state of one row in Sales
     * @param quant The quantity of one row in Sales
     * @throws java.sql.SQLException
     */
    private void executeQuestionOne(String cust, String prod, String date, String st, int quant) throws java.sql.SQLException{
            // looking for max_q and min_q
            boolean hasCust = false;
            for(MaxMinAvg item: maxMinAvgArrayList) {
                // if the cust has been in arraylist, check if it is the max_q or min_q
                if (item.cust.equals(cust)){
                    hasCust = true;
                    // update
                    if (quant > item.max_q) {
                        item.max_q = quant;
                        item.maxProd = prod;
                        item.maxDate = date;
                        item.maxSt = st;
                    }
                    if (quant < item.min_q){
                        item.min_q = quant;
                        item.minProd = prod;
                        item.minDate = date;
                        item.minSt = st;
                    }
                    item.count++;  // update total number
                    item.sumOfQuant += quant; // update sum of quantity
                    item.avg_q = item.sumOfQuant / item.count;  // update the average quantity
                }
            }

            // if the cust is not in arraylist, add it in.
            if (!hasCust){
                MaxMinAvg newCust = new MaxMinAvg(cust, quant, prod, date, st, quant, prod, date, st);
                maxMinAvgArrayList.add(newCust);
            }
    }

    // print table of question 1
    private void printMaxMinAvg() {
        System.out.println("question 1 :");
        System.out.println("CUSTOMER" + "  " + "MAX_Q" + "  " + "PRODUCT" + "  " + "DATE" + "        " + "ST" + "  " + "MIN_Q" + "  " + "PRODUCT" + "  " + "DATE" + "        " + "ST" + "  " + "AVG_Q" );
        System.out.println("========" + "  " + "=====" + "  " + "=======" + "  " + "==========" + "  " + "==" + "  " + "=====" + "  " + "=======" + "  " + "==========" + "  " + "==" + "  " + "=====");
        for(MaxMinAvg item: maxMinAvgArrayList) {
            String row = String.format("%-10s", item.cust) + String.format("%5s  ",item.max_q) + String.format("%-9s", item.maxProd)
                    + item.maxDate+ "  " + item.maxSt + "  " + String.format("%5s  ",item.min_q) + String.format("%-9s", item.minProd) + item.minDate + "  " + item.minSt + "  " + String.format("%5s",item.avg_q);
            System.out.println(row);
        }

        System.out.println();
    }

    // modify the format of date
    private String constructDate(int month, int day, int year) {
        String strDay;
        String strMonth;

        // if the day or month is 1 digit, converte it to 2 digits
        if ((day / 10) == 0) {
           strDay  = "0" + day;
        }else {
            strDay = "" + day;
        }
        if ((month / 10) == 0) {
            strMonth = "0" + month;
        }else {
            strMonth = "" + month;
        }
        // compose the date
        String date = strMonth + "/" + strDay + "/" + year;
        return date;
    }

    // execute qustion 2
    private void executeQuestionTwo(String cust, String prod, String date, String st, int quant, int month, int year) throws java.sql.SQLException{
        // looking for max_q and min_q
        boolean hasCust = false;
        if (month == 1 || month == 2 || month == 3) {  // judge if it needs consider
            if (month == 1 && (year < 2000 || year > 2005)) {
                return;
            }
            for (JanFebMar item : janFebMarArrayList) {
                // if the cust has been in arraylist, check if it is the max_q or min_q
                if (item.cust.equals(cust) && item.prod.equals(prod)) {
                        hasCust = true;
                        // update
                        if (month == 1) {
                            if (quant > item.janMax) {
                                item.janMax = quant;
                                item.janMaxDate = date;
                            }
                            break;
                        } else if (month == 2) {
                            if (item.febMin == 0) {
                                item.febMin = quant;
                                item.febMinDate = date;
                            }
                            if (quant < item.febMin) {
                                item.febMin = quant;
                                item.febMinDate = date;
                            }
                            break;
                        } else {
                            if (item.marMin == 0) {
                                item.marMin = quant;
                                item.marMinDate = date;
                            }
                            if (quant < item.febMin) {
                                item.marMin = quant;
                                item.marMinDate = date;
                            }
                            break;
                        }
                }
            }

            // if the cust is not in arraylist, add it in.
            if (!hasCust) {
                JanFebMar janFebMar = new JanFebMar(cust, prod, quant, date, quant, date, quant, date, month);
                janFebMarArrayList.add(janFebMar);
            }
        }
    }

    // print table of question 2
    private void printJanFebMar() {
        System.out.println("question 2 :");
        System.out.println("CUSTOMER" + "  " + "PRODUCT" + "  " + "JAN_MAX" + "  " + "DATE" + "        " + "FEB_MIN" + "  " + "DATE" + "        " + "MAR_MIN" + "  " + "DATE" + "        ");
        System.out.println("========" + "  " + "=======" + "  " + "=======" + "  " + "==========" + "  " + "=======" + "  " + "==========" + "  " + "=======" + "  " + "==========" + "  ");

        // iterate the janFebMarArrayList print every item
        for(JanFebMar item: janFebMarArrayList) {
            String row = String.format("%-10s", item.cust) + String.format("%-9s", item.prod)
                    + String.format("%7s", item.janMax) + "  " + String.format("%-10s",item.janMaxDate) + "  "
                    + String.format("%7s", item.febMin) + "  " + String.format("%-10s",item.febMinDate) + "  "
                    + String.format("%7s", item.marMin) + "  " + String.format("%-10s",item.marMinDate);
            System.out.println(row);
        }
    }


    // main function
    public static void main(String[] args){
        Assignment assignment = new Assignment();
        assignment.connect();
    }
}
