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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

		String correcUsername = "practica";
		String correctPassword = "cf22a8a09367f9802e640e691a7a756087bb16e9344f20c38e6fd8ebcc5ec335";

		try {
			SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(3343);
			SSLParameters sslParameters = new SSLParameters();
			String[] enabledCipherSuites = {"TLS_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"};
            sslParameters.setCipherSuites(enabledCipherSuites);
			serverSocket.setSSLParameters(sslParameters);

			Class.forName(JDBC_DRIVER);
			System.out.println("Connecting to database...");
			final Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

			System.out.println("Creating tables in given database...");
			Statement stmt = conn.createStatement();

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
					"INSERT INTO USERS (username, password) VALUES ('"+correcUsername+"', '"+correctPassword+"')");
			System.out.println("Created tables in given database...");

			// wait for client connection and check login information
			System.err.println("Waiting for connection...");
			ExecutorService threadPool = Executors.newFixedThreadPool(400);
			while (true) {
				try {
					final SSLSocket socket = (SSLSocket) serverSocket.accept();
					threadPool.execute(() -> {
						try {
							// open BufferedReader for reading data from client
							BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							String username = input.readLine();
							String password = input.readLine();
							String message = input.readLine();

							Statement threadStatement = conn.createStatement();

							// open PrintWriter for writing data to client
							PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
							if (threadStatement.executeQuery("SELECT username FROM USERS WHERE username = '"
									+ String.valueOf(username) + "' AND password = '" + String.valueOf(password) + "'")
									.next()) {
								String sql = "INSERT INTO MESSAGES (message, username) VALUES ('" + message + "', '"
										+ username + "')";
								threadStatement.executeUpdate(sql);
								output.println("Welcome to the Server. Your message has been saved.");
								System.out.println("Message saved from " + username + ": " + message);

								// close everything
								if (input != null)
									input.close();
								if (output != null)
									output.close();
								if (socket != null)
									socket.close();
							} else {
								output.println("Incorrect credentials.");
								System.out.println("Incorrect credentials from " + username);
							}

						} catch (IOException | SQLException e) {
							System.err.println("Error: " + e.getMessage());
						}
					});
				} catch (IOException e) {
					System.err.println("Error: " + e.getMessage());
				}
			}
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
