
import java.sql.*;
import java.util.Properties;

public class SQL {

    //TODO: fix handling of database
    //Handles database


    //views table 
    public static void viewTable(Connection con, String highscore_schema) throws SQLException {

        Statement stmt = null;
        String query = "select playerName, score, win, draw, loss " + "from " + highscore_schema;
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
        } finally {
            if (stmt != null) { stmt.close(); }
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

