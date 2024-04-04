package client;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.annotation.processing.SupportedSourceVersion;
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

		loadEnvVariables();
		String SERVER_URL = System.getProperty("SERVER_URL");
		Integer SERVER_PORT = Integer.valueOf(System.getProperty("SERVER_PORT"));

		try {
			
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) factory.createSocket(SERVER_URL, SERVER_PORT);
			
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

			String user = JOptionPane.showInputDialog(null, "Enter a user:");
			
			JPasswordField passwordField = new JPasswordField();

			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			JLabel label = new JLabel("Enter a password:");
			panel.add(label);

			panel.add(passwordField);
			
			JOptionPane.showConfirmDialog(null, panel, "Password Input", JOptionPane.OK_CANCEL_OPTION);
			String message = JOptionPane.showInputDialog(null, "Enter a message:");

			String pass = new String(passwordField.getPassword());

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
	
	private static void loadEnvVariables() {
        try {
            File file = new File(System.getProperty("user.dir")+"\\client"+"\\"+".properties");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    System.setProperty(key, value);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
