package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.net.ssl.*;

public class MsgSSLServerSocket {
	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		
		String correcUsername = "practica";
		String correctPassword = "cf22a8a09367f9802e640e691a7a756087bb16e9344f20c38e6fd8ebcc5ec335";
		try {
			SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(3343);
		
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
