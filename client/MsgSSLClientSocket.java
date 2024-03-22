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

public class MsgSSLClientSocket {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		try {
			
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 3343);
			//SSLSocket socket = (SSLSocket) factory.createSocket("192.168.100.30", 3343);
			
			// create BufferedReader for reading server response
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// create PrintWriter for sending login to server
			PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			// prompt user for user name
			String user = JOptionPane.showInputDialog(null, "Enter a user:");
			
			JPasswordField passwordField = new JPasswordField();
        
			// Creamos un panel para el cuadro de diálogo
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Cambiamos el LayoutManager a BoxLayout

			JLabel label = new JLabel("Enter a password:");
			panel.add(label);

			panel.add(passwordField);
			
			JOptionPane.showConfirmDialog(null, panel, "Password Input", JOptionPane.OK_CANCEL_OPTION);
			String message = JOptionPane.showInputDialog(null, "Enter a message:");
			
			// Convertimos el valor del arreglo de caracteres a String
			String pass = new String(passwordField.getPassword());

			// Convert the password to a SHA-256 hash
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

			
			JOptionPane.showMessageDialog(null, response);

			
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
