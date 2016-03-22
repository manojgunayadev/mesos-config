import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.ibatis.common.jdbc.ScriptRunner;

public class JDBCTest {

	public static void main(String[] args) {


		Connection conn = null;
		try {
			String aSQLScriptFilePath = "path/to/sql/script.sql";
			
			// Create MySql Connection
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/mysql", "root", "password");
			Statement stmt = null;

			try {
				// Initialize object for ScripRunner
				ScriptRunner sr = new ScriptRunner(con, false, false);

				// Give the input file to Reader
				Reader reader = new BufferedReader(
	                               new FileReader(aSQLScriptFilePath));

				// Exctute script
				sr.runScript(reader);

			} catch (SQLException ex) {
		    // handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}catch(Exception ex){
			
		}


	}

}
