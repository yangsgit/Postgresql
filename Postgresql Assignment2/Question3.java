
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 1 Firstly, to connect your own database, you should open .java file separately and
 change the attributes url, user, password in each class

 2 For running java program by command line, you should first go into the Path of this package, which contains all the files, by using :

 cd /(the path of Assignment Package in your computer)/YangLi_10427039

 Then use following command line to compile:

 javac -cp postgresql-42.1.4.jar Question3.java

 Finally, use following command line to run program

 java -cp .:postgresql-42.1.4.jar Question3

 Run three program using this way separately

 3 For running java program in IDE, you should import postgresql-42.1.4.jar in advance.
 Then import three .java file in your project and run them separately.
 */

/**
 * 2. Detail of program
 * Quesiton3.java contain one Main class to run program
 * Run program one time to read all data row by row and deal with it simultaneously
 * to ceate a  "base view", which is baseList in the program.
 *
 * Using HashSet to store the every BaseObject
 * Using HashMap to store every QueThrObject, so that we can check if there is already
 * exist in Map by O(1)
 *
 * after reading a row of data, caculate the quarter  for each sales
 * then store them in a baseSet as our base view. Meanwhile, we create the QueThrObject
 * store it in the QueThrMap.  Then we traverse QueThrMap, for each queThrMap instance
 * we find the object in baseSet when the quant is between queThrObject's average and minimum
 * quant
 */


/**
 * this class is to store the handled data after scanning database
 * @preCount and  @aftCount will be used to count how many sales  before and after this quarter
 * @minQuant  will be used to record the minimum quant
 * @sumQuant  and @count will be used to calculate the avgQuant
 */

class Question3 {
    class QueThrObject {
        String cust;
        String prod;
        int quarter;
        int count;
        float sumQuant;
        float avgQuant;
        float minQuant;
        int preCount;
        int aftCount;

        public QueThrObject(String cust, String prod, int quarter, float quant) {
            this.cust = cust;
            this.prod = prod;
            this.quarter = quarter;
            this.minQuant = quant;
            this.count = 1;
            this.avgQuant = quant;
            this.sumQuant = quant;

        }

        public void update(float quant) {
            this.sumQuant += quant;
            this.count++;
            this.avgQuant = this.sumQuant / this.count;
            if (quant < this.minQuant) {
                this.minQuant = quant;
            }
        }
    }

    class BaseObject {
        String cust;
        String prod;
        int quarter;
        float quant;

        public BaseObject(String cust, String prod, int quarter, float quant) {
            this.cust = cust;
            this.prod = prod;
            this.quarter = quarter;
            this.quant = quant;
        }
    }

    /**
     * url, user, password should be changed to connect your own Database
     */
    String url = "jdbc:postgresql://localhost:5432/postgres";
    String user = "postgres";
    String password = "9";

    HashSet<BaseObject> baseSet = new HashSet<>();

    HashMap<String, QueThrObject> queThrHash = new HashMap<>();

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

    // parse data and create BaseObject and QueThrObject
    public void dealWithData(ResultSet rs) throws java.sql.SQLException{
        while (rs.next()) {
            String cust = rs.getString("cust");
            String prod = rs.getString("prod");
            int month = rs.getInt("month");
            int quarter = (month - 1) / 3 + 1;
            int quant = rs.getInt("quant");


            BaseObject bo = new BaseObject(cust, prod, quarter, quant);
            baseSet.add(bo);
	    
            // if queThrHash does not contain matched object , create new one and add in
	    // else update it
            if (!queThrHash.containsKey(cust+prod+quarter)) {
                QueThrObject qto = new QueThrObject(cust, prod, quarter, quant);
                queThrHash.put(cust+prod+quarter, qto);
            }else {
                QueThrObject qto = queThrHash.get(cust+prod+quarter);
                qto.update(quant);
            }

        }

        // for each queThrObject find the baseObject whoes quant between avrage quant and minmum quant
        for (QueThrObject queThrObject : queThrHash.values()) {
            float avgQ = queThrObject.avgQuant;
            float minQ = queThrObject.minQuant;
            int quarter = queThrObject.quarter;
            for (BaseObject baseObject : baseSet ) {
                if (baseObject.cust.equals(queThrObject.cust) && baseObject.prod.equals(queThrObject.prod)) {
                    if (baseObject.quarter - quarter == 1 && baseObject.quant > minQ && baseObject.quant < avgQ){
                        queThrObject.aftCount++;
                    }
                    if (baseObject.quarter - quarter == -1 && baseObject.quant > minQ && baseObject.quant < avgQ) {
                        queThrObject.preCount++;
                    }
                }
            }
        }
        print();
    }

    // print queThrObject in queThrHash one by one
    public void print() {
        System.out.println("question 3 :");
        System.out.println("CUSTOMER" + "  " + "PRODUCT" + "  " + "QUARTER" + "  " + "BEFORE_TOT" + "  " + "AFTER_TOT");
        System.out.println("========" + "  " + "=======" + "  " + "=======" + "  " + "==========" + "  " + "=========");

        int count = 0;
        for (QueThrObject ob : queThrHash.values()) {
            if (ob.aftCount == 0 && ob.preCount == 0) continue;
            count++;
            System.out.println(String.format("%-10s", ob.cust) + String.format("%-9s", ob.prod) + String.format("%-9s", "Q"+ob.quarter)
                    + String.format("%10s", ob.preCount) + " " + String.format("%10s",ob.aftCount));
        }
        System.out.println("line : " +count);
    }

    public static void main(String args[]) {
        Question3 q3 = new Question3();
        q3.connect();
    }
}
