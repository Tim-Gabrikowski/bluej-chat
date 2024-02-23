import java.io.*;
import java.net.*;

class ChatClient {

	public static void main(String[] args) throws IOException {
		String serverName = (args.length > 0) ? args[0] : "localhost";

		System.out.println(
			"Öffne Verbindung zu " + serverName + " auf Port 5001."
		);
		Socket connection = new Socket(serverName, 5001);

		try {
			PrintWriter output = new PrintWriter(
				connection.getOutputStream(),
				true
			);
			BufferedReader input = new BufferedReader(
				new InputStreamReader(connection.getInputStream())
			);
			BufferedReader stdIn = new BufferedReader(
				new InputStreamReader(System.in)
			);

			System.out.println(
				"Verbunden mit " +
				connection.getInetAddress().getHostName() +
				"."
			);

			// Thread for receiving messages from the server
			Thread receiveThread = new Thread(() -> {
				try {
					String message;
					while ((message = input.readLine()) != null) {
						System.out.println(message);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			receiveThread.start();

			// Thread for sending messages to the server
			Thread sendThread = new Thread(() -> {
				try {
					String message;
					while ((message = stdIn.readLine()) != null) {
						output.println("Client: " + message);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			sendThread.start();

			// Wait for both threads to finish
			receiveThread.join();
			sendThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Verbindung beendet.");
			connection.close();
		}
	}
}
