import java.io.*;
import java.net.*;
import java.util.Scanner;

class ChatClient {

	public static void main(String[] args) throws IOException {
		String serverName = (args.length > 0) ? args[0] : "localhost";

		System.out.println(
			"Öffne Verbindung zu " + serverName + " auf Port 5001."
		);
		Socket connection = new Socket(serverName, 5001);

		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter your username: ");
		String username = scanner.nextLine();

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
						ChatMessage msg = ChatMessage.fromString(message);
						System.out.println(msg.displayString());
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
						ChatMessage msg = new ChatMessage(message, username);
						output.println(msg.toString());
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
