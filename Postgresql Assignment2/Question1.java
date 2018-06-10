
/**
   1 Firstly, to connect your own database, you should open .java file separately and
        change the attributes url, user, password in each class

   2 For running java program by command line, you should first go into the Path of this package, which contains all the files, by using :

        cd /(the path of Assignment Package in your computer)/YangLi_10427039

        Then use following command line to compile  :

        javac -cp postgresql-42.1.4.jar Question1.java

        Finally, use following command line to run program

        java -cp .:postgresql-42.1.4.jar Question1

        Run three program using this way separately

     3 For running java program in IDE, you should import postgresql-42.1.4.jar in advance.
        Then import three .java file in your project and run them separately.
*/

/** 2. Detail of program
 * Quesiton1.java contain one Main class to run program
 * Run program one time to read all data row by row and deal with it simultaneously
 * to ceate a  "base view",  which is baseList in the program.
 *
 * Using ArrayList baseList to store a base view for later deal with
 * Unlike array, ArrayList is no need to initialize the capacity, which is unknown
 * Because there are only 500 rows data, every search operation in arraylist
 * can be considered to O(1). If the table is very big and input is unlimited. I will use
 * hashtable that search operation can be averagely O(1)
 *
 * after reading a row of data, caculate the sum and count for each cust and prod
 * then store them in a baseList as our base view. Then we traverse this base view
 * to calculate the other_prod_avg and other_cust_avg
 */

import java.sql.*;
import java.util.ArrayList;

/**
 * this class is to store the handled data after scanning database
 * @otherProdQuant and  @otherProdCount will be used to calculate the other prod avg
 * @otherCustQuant and @otherCustCount will be used to calculate the other cust avg
 */
class QuesOneObject {
    String cust;
    String prod;
    int quantSum = 0;
    int count;
    int otherProdQuant = 0;
    int otherProdCount = 0;
    int otherCustQuant = 0;
    int otherCustCount = 0;

    public QuesOneObject(String cust, String prod, int quant) {
        this.cust = cust;
        this.prod = prod;
        this.quantSum = quant;
        this.count++;
    }

    public void update(int newQuant) {
        this.quantSum += newQuant;
        this.count++;
    }
}

public class Question1 {
    /**
     * url, user, password should be changed to connect your own Database
     */
    String url = "jdbc:postgresql://localhost:5432/postgres";
    String user = "postgres";
    String password = "9";

    ArrayList<QuesOneObject> baseList = new ArrayList<>();

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
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM sales");
            dealWithData(rs);
        } catch (SQLException e) {
            System.out.println("Connection URL or username or password errors!");
            System.out.println();
            System.out.println();
            e.printStackTrace();
        }
    }

    // read data from database row by row then parse data to object
    public void dealWithData (ResultSet rs) throws java.sql.SQLException{
        // parse data
        while (rs.next()) {
            String cust = rs.getString("cust");
            String prod = rs.getString("prod");
            int quant = rs.getInt("quant");

            // for each cust and prod, check if this row is in baseList
            if (baseList.isEmpty()) {
                QuesOneObject quesOneObject = new QuesOneObject(cust, prod, quant);
                baseList.add(quesOneObject);
            } else {
                boolean contains = false;
                for (QuesOneObject bv: baseList) {
                    // if exist, update
                    if (bv.cust.equals(cust) && bv.prod.equals(prod))
                    {
                        bv.update(quant);
                        contains = true;
                        break;
                    }
                }
                // if does not exist, create a new one and add it in list
                if (!contains) {
                    QuesOneObject quesOneObject = new QuesOneObject(cust, prod, quant);
                    baseList.add(quesOneObject);
                }
            }
        }

        // for each queTwoObject, match it with another queTwoObject in list
        for (QuesOneObject quesOneObject : baseList) {
            // traverse the baseList to calculate the other_prod_avg
            for (QuesOneObject ob: baseList) {
                if (ob.cust.equals(quesOneObject.cust) && !ob.prod.equals(quesOneObject.prod)){
                        quesOneObject.otherProdQuant += ob.quantSum;
                        quesOneObject.otherProdCount += ob.count;
                }
            }
            // traverse the baseList to calculate the other_cust_avg
            for (QuesOneObject ob: baseList) {
                if (ob.prod.equals(quesOneObject.prod) && !ob.cust.equals(quesOneObject.cust)) {
                        quesOneObject.otherCustQuant += ob.quantSum;
                        quesOneObject.otherCustCount += ob.count;
                }
            }
        }
        print();
    }

    // print the baseList row by row, meanwhile, calculate the avg, other prod avg and other cust avg
    public void print() {
        System.out.println("question 1 :");
        System.out.println("CUSTOMER" + "  " + "PRODUCT" + "  " + "THE_AVG" + "  " + "OTHER_PROD_AVG" + "  " + "OTHER_CUST_AVG");
        System.out.println("========" + "  " + "=======" + "  " + "=======" + "  " + "==============" + "  " + "==============");
        for (QuesOneObject ob : baseList) {
            System.out.println(String.format("%-10s", ob.cust) + String.format("%-9s", ob.prod) + String.format("%7s", ob.quantSum/ob.count)+"  "
                                + String.format("%14s", ob.otherProdQuant/ob.otherProdCount) + "  "
                                + String.format("%14s", ob.otherCustQuant/ob.otherCustCount));
        }
        System.out.println("line : " +baseList.size());
    }

    public static void main(String args[]) {
        Question1 q1 = new Question1();
        q1.connect();
    }
}
