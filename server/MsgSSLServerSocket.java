package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.net.ssl.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MsgSSLServerSocket {
	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */

	 // JDBC driver name and database URL 
	 static final String JDBC_DRIVER = "org.h2.Driver";   
	 static final String DB_URL = "jdbc:h2:~/serverDB";
	 
	 //  Database credentials 
	 static final String USER = "sa"; 
	 static final String PASS = ""; 

	public static void main(String[] args) throws IOException, InterruptedException, SQLException, ClassNotFoundException{

		Connection conn = null; 
      	Statement stmt = null; 

		String correcUsername = "practica";
		String correctPassword = "cf22a8a09367f9802e640e691a7a756087bb16e9344f20c38e6fd8ebcc5ec335";

		try {
			SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(3343);

			Class.forName(JDBC_DRIVER);
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);

			System.out.println("Creating table in given database if not exists...");
			stmt = conn.createStatement();
			String sql =  "CREATE TABLE IF NOT EXISTS MESSAGES " +
							"(id INTEGER not NULL, " +
							" message VARCHAR(255), " + 
							" username VARCHAR(255), " +
							" PRIMARY KEY ( id ))";
			stmt.executeUpdate(sql);

			System.out.println("Created table in given database...");
		
			// wait for client connection and check login information
			System.err.println("Waiting for connection...");

			while (true) {
			SSLSocket socket = (SSLSocket) serverSocket.accept();
				// open BufferedReader for reading data from client
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String username = input.readLine();
				String password = input.readLine();
				String message = input.readLine();
				
				// open PrintWriter for writing data to client
				PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
				if (username.equals(correcUsername) && password.equals(correctPassword)) {
					// save to database
					sql = "INSERT INTO MESSAGES (message, username) VALUES ('" + message + "', '" + username + "')";
					stmt.executeUpdate(sql);
					output.println("Welcome to the Server. Your message has been saved.");
					
				} else {
					output.println("Incorrect credentials.");
				}

				output.close();
				input.close();
				//socket.close();
			}
		
		}catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
