
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

public class SQL {

    private static final String HIGHSCORE_TABLE = "highscore";
    static Connection con;
    private String jdbcUrl = "jdbc:mysql://localhost:3306/chess?autoReconnect=true&useSSL=false";
    private String username = "root";
    private String password = "";
    static Statement stmt = null;


    //TODO: fix handling of database
    //Handles database

    public SQL(){
        try{
            con = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }




    //returns all scores
    public static void getAllScores() throws SQLException {
        String query = "select playerName, score, win, draw, loss " + "from " + HIGHSCORE_TABLE;
        runQuery(query);
    }

    public static void getScore(int scoreID) throws SQLException {
        String query = "select playerName, score, win, draw, loss " + "from " + HIGHSCORE_TABLE +" WHERE ID = " + scoreID;
        runQuery(query);
    }

    /**
     *
     * @param first_user first client
     * @param second_user second client
     * @param result result of the game
     *               0 - nobody wins, it's a draw
     *               1 -  the first user wins
     *               2 - the second user wins
     */
    public static void updateHighscore(int first_user, int second_user, int result){

    }

    private static void runQuery(String query){
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String playerName = rs.getString("playerName");
                int score = rs.getInt("score");
                float win = rs.getInt("win");
                int draw = rs.getInt("draw");
                int loss = rs.getInt("loss");
                System.out.println(playerName + "\t" + score + "\t" + win + "\t" + draw + "\t" + loss);
            }
        } catch (SQLException e ) {
            System.out.println(e);
        }
    }

  /*  public Connection getConnection() throws SQLException {

        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);

        if (this.dbms.equals("mysql")) {
            conn = DriverManager.getConnection(
                    "jdbc:" + this.dbms + "://" +
                            this.serverName +
                            ":" + this.portNumber + "/",
                    connectionProps);
        } else if (this.dbms.equals("derby")) {
            conn = DriverManager.getConnection(
                    "jdbc:" + this.dbms + ":" +
                            this.dbName +
                            ";create=true",
                    connectionProps);
        }
        System.out.println("Connected to database");
        return conn;
    }*/
}

