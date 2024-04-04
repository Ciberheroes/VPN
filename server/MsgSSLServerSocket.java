package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
	static final String DB_URL = "jdbc:h2:tcp://localhost/~/serverDB";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	public static void main(String[] args)
			throws IOException, InterruptedException, SQLException, ClassNotFoundException {

		Connection conn = null;
		Statement stmt = null;

		String correcUsername = "practica";
		String correctPassword = "cf22a8a09367f9802e640e691a7a756087bb16e9344f20c38e6fd8ebcc5ec335";

		try {
			SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(3343);

			Class.forName(JDBC_DRIVER);
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			System.out.println("Creating tables in given database...");
			stmt = conn.createStatement();

			String dropMessagesTable = "DROP TABLE IF EXISTS MESSAGES";
			stmt.executeUpdate(dropMessagesTable);

			String createMessagesTable = "CREATE TABLE MESSAGES " +
					"(id INTEGER AUTO_INCREMENT, " +
					" username VARCHAR(255), " +
					" message VARCHAR(255), " +
					" PRIMARY KEY ( id ))";
			stmt.executeUpdate(createMessagesTable);

			String dropUsersTable = "DROP TABLE IF EXISTS USERS";
			stmt.executeUpdate(dropUsersTable);

			String createUsersTable = "CREATE TABLE USERS " +
					"(username VARCHAR(255), " +
					" password VARCHAR(255), " +
					" PRIMARY KEY ( username ))";
			stmt.executeUpdate(createUsersTable);

			for (Integer i = 0; i < 300; i++) {
				String pass = "";
				try {
					byte[] hashBytes = MessageDigest.getInstance("SHA-256").digest((String.valueOf(i)).getBytes());
					StringBuilder hashBuilder = new StringBuilder();
					for (byte b : hashBytes) {
						hashBuilder.append(String.format("%02x", b));
					}
					pass = hashBuilder.toString();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				stmt.executeUpdate("INSERT INTO USERS (username, password) VALUES ('" + i + "', '" + pass + "')");
			}
			stmt.executeUpdate(
					"INSERT INTO USERS (username, password) VALUES ('practica', 'cf22a8a09367f9802e640e691a7a756087bb16e9344f20c38e6fd8ebcc5ec335')");
			System.out.println("Created tables in given database...");

			// wait for client connection and check login information
			System.err.println("Waiting for connection...");

			while (true) {
				try {
					final SSLSocket socket = (SSLSocket) serverSocket.accept();
					new Thread(() -> {
						try {
							// open BufferedReader for reading data from client
							BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							String username = input.readLine();
							String password = input.readLine();
							String message = input.readLine();

							// open PrintWriter for writing data to client
							PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
							if (stmt.executeQuery("SELECT username FROM USERS WHERE username = '"
									+ String.valueOf(username) + "' AND password = '" + String.valueOf(password) + "'")
									.next()) {
								String sql = "INSERT INTO MESSAGES (message, username) VALUES ('" + message + "', '"
										+ username + "')";
								stmt.executeUpdate(sql);
								output.println("Welcome to the Server. Your message has been saved.");
							} else {
								output.println("Incorrect credentials.");
							}

						} catch (IOException | SQLException e) {
							System.err.println("Error: " + e.getMessage());

						}
					}).start();
				} catch (IOException e) {
					System.err.println("Error: " + e.getMessage());
				}
			}
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
			output.close();
			input.close();
			socket.close();
		}
	}
}
