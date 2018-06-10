
import java.sql.*;
import java.util.ArrayList;

/**
 1 Firstly, to connect your own database, you should open .java file separately and
 change the attributes url, user, password in each class

 2 For running java program by command line, you should first go into the Path of this package, which contains all the files, by using :

 cd /(the path of Assignment Package in your computer)/YangLi_10427039

 Then use following command line to compile  :

 javac -cp postgresql-42.1.4.jar Question2.java

 Finally, use following command line to run program

 java -cp .:postgresql-42.1.4.jar Question2

 Run three program using this way separately

 3 For running java program in IDE, you should import postgresql-42.1.4.jar in advance.
 Then import three .java file in your project and run them separately.
 */

/**
 * 2. Detail of program
 * Quesiton2.java contain one Main class to run program
 * Run program one time to read all data row by row and deal with it simultaneously
 * to ceate a  "base view", which is baseList in the program.
 *
 * Using ArrayList baseList to store a base view for later deal with
 * Unlike array, ArrayList is no need to initialize the capacity, which is unknown
 * Because there are only 500 rows data, every search operation in arraylist
 * can be considered to O(1). If the table is very big and input is unlimited. I will use
 * hashtable that search operation can be averagely O(1)
 *
 * after reading a row of data, caculate the sum and count for each cust and prod
 * then store them in a baseList as our base view. Then we traverse this base view
 * to calculate the before avg and after avg
 */


/**
 * this class is to store the handled data after scanning database
 * @beforeQuant and  @beforeCount will be used to calculate before avg
 * @afterQuant and @afterCount will be used to calculate the after avg
 */

class QueTwoObject {
    String cust;
    String prod;
    int quarter;
    int quant;
    int count;
    int beforeQuant = 0;
    int beforeCount = 0;
    int afterQuant = 0;
    int afterCount = 0;

    public QueTwoObject(String cust, String prod, int quant, int quarter) {
        this.cust = cust;
        this.prod = prod;
        this.quarter = quarter;
        this.quant = quant;
        this.count++;
    }

    public void update(int quant) {
        this.quant += quant;
        this.count++;
    }
}



public class Question2 {
    /**
     * url, user, password should be changed to connect your own Database
     */
    String url = "jdbc:postgresql://localhost:5432/postgres";
    String user = "postgres";
    String password = "9";

    ArrayList<QueTwoObject> baseList = new ArrayList<>();

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
    public void dealWithData (ResultSet rs) throws java.sql.SQLException {
        // parse data
        while (rs.next()) {
            String cust = rs.getString("cust");
            String prod = rs.getString("prod");
            int month = rs.getInt("month");
            int quarter = (month - 1) / 3 + 1;
            int quant = rs.getInt("quant");

            // for each cust and prod, check if this row is in baseList
            if (baseList.isEmpty()) {
                QueTwoObject ob = new QueTwoObject(cust, prod, quant, quarter);
                baseList.add(ob);
            } else {
                // if exist, update
                boolean contains = false;
                for (QueTwoObject ob : baseList) {
                    if (ob.cust.equals(cust) && ob.prod.equals(prod) && ob.quarter == quarter) {
                        ob.update(quant);
                        contains = true;
                        break;
                    }
                }
                // if does not exist, create a new one and add it in list
                if (!contains) {
                    QueTwoObject queTwoObject = new QueTwoObject(cust, prod, quant, quarter);
                    baseList.add(queTwoObject);
                }
            }
        }

        // for each queTwoObject, match it with another queTwoObject in list
        for (QueTwoObject queTwoObject : baseList) {
            for (QueTwoObject bo : baseList) {
                if (queTwoObject.cust.equals(bo.cust) && queTwoObject.prod.equals(bo.prod)) {
                    // traverse the baseList to calculate the before_avg
                    if (queTwoObject.quarter - bo.quarter == 1) {
                        queTwoObject.beforeQuant += bo.quant;
                        queTwoObject.beforeCount += bo.count;
                    }
                    // traverse the baseList to calculate the after_avg
                    if (queTwoObject.quarter - bo.quarter == -1) {
                        queTwoObject.afterQuant += bo.quant;
                        queTwoObject.afterCount += bo.count;
                    }
                }
            }
        }
        print();
    }

    // calculate the avg value then print them line by line
    public void print() {
        System.out.println("question 2 :");
        System.out.println("CUSTOMER" + "  " + "PRODUCT" + "  " + "QUARTER" + "  " + "BEFORE_AVG" + "  " + "AFTER_AVG");
        System.out.println("========" + "  " + "=======" + "  " + "=======" + "  " + "==========" + "  " + "=========");

        for (QueTwoObject ob : baseList) {
            String beforeAvg = ob.beforeCount == 0 ? String.format("%8s","NULL"): String.format("%8s", ob.beforeQuant/ob.beforeCount);
            String afterAvg = ob.afterCount == 0 ? String.format("%9s","NULL") : String.format("%9s", ob.afterQuant/ob.afterCount);

            System.out.println(String.format("%-10s", ob.cust) + String.format("%-9s", ob.prod) + String.format("%-9s", "Q"+ob.quarter)+"  "
                    + beforeAvg + "  "
                    + afterAvg);
        }
        System.out.println("line : " +baseList.size());
    }

    public static void main(String args[]) {
        Question2 q2 = new Question2();
        q2.connect();
    }
}
