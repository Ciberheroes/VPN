package client;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.net.ssl.*;
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

		loadEnvVariables();
		Integer numOperaciones = Integer.valueOf(System.getProperty("NUM_OPERACIONES"));

		Thread[] hilos = new Thread[numOperaciones];
		
		LocalDateTime startTime = LocalDateTime.now();
		int satisfactoryPetitions = 0;

		for (int i = 0; i < numOperaciones; i++) {
			final String index = String.valueOf(i);
			Runnable operacion = new Runnable() {
				@Override
				public void run() {
					sendMessage(index,index,"Mensaje de prueba "+index);
				}
			};
			hilos[i] = new Thread(operacion);
			hilos[i].start();
		}

		for (Thread hilo : hilos) {
            try {
                hilo.join();
				satisfactoryPetitions++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
		System.out.println("Tiempo total: "+Duration.between(startTime, LocalDateTime.now()).toMillis()+" ms");
		System.out.println("Peticiones satisfactorias: "+satisfactoryPetitions);
	}

	public static void sendMessage(String user, String pass, String message){
		try {
			
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) factory.createSocket(System.getProperty("SERVER_URL"), Integer.valueOf(System.getProperty("SERVER_PORT")));

			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			
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
			
		} 


		catch (IOException ioException) {
			sendMessage(user,pass, message);
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
