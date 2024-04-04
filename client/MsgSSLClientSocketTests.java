package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.net.ssl.*;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.Duration;

public class MsgSSLClientSocketTests {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		int numOperaciones = 2;
		Thread[] hilos = new Thread[numOperaciones];

		LocalDateTime startTime = LocalDateTime.now();
		
		for (int i = 0; i < numOperaciones; i++) {
			final String index = String.valueOf(i);
			Runnable operacion = new Runnable() {
				@Override
				public void run() {
					sendMessage(index,index,"Mensaje de prueba "+index);
					System.out.println("Operación " + index + " ejecutándose...");
				}
			};
			hilos[i] = new Thread(operacion);
			hilos[i].start();
		}

		for (Thread hilo : hilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
		System.out.println("El proceso ha terminado en " + Duration.between(startTime, LocalDateTime.now()) + " milisegundos.");
	}

	public static void sendMessage(String user, String pass, String message){
		try {
			
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 3343);
			//SSLSocket socket = (SSLSocket) factory.createSocket("192.168.100.30", 3343);

			// create BufferedReader for reading server response
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// create PrintWriter for sending login to server
			PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			// prompt user for user name
			
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hashBytes = digest.digest(pass.getBytes());
				
				StringBuilder hashBuilder = new StringBuilder();
				for (byte b : hashBytes) {
					hashBuilder.append(String.format("%02x", b));
				}
				pass = hashBuilder.toString();
			} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
			}
				
			output.println(user);
			output.println(pass);
			output.println(message);
			output.flush();

			
			String response = input.readLine();

			System.out.println(response);
				
			output.close();
			input.close();
			socket.close();
		} 


		catch (IOException ioException) {
			ioException.printStackTrace();
		}
		
		finally {
			System.exit(0);
		}
	}
}
