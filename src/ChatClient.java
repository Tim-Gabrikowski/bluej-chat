import java.io.*;
import java.net.*;
import java.util.Scanner;

class ChatClient {

	public static void main(String[] args) throws IOException {
		// define Hostname
		String serverName = (args.length > 0) ? args[0] : "localhost";

		// Define Input Scanner
		Scanner scanner = new Scanner(System.in);

		// Username Input
		System.out.print("Enter your username: ");
		String username = scanner.nextLine();

		// Connect to Server
		System.out.println("Connect to " + serverName + ":5001");
		Socket connection = new Socket(serverName, 5001);

		try {
			// socketSend Stream (Client -> Server)
			PrintWriter socketSend = new PrintWriter(
				connection.getOutputStream(),
				true
			);
			// socketRecieve (Server -> Client)
			BufferedReader socketRecieve = new BufferedReader(
				new InputStreamReader(connection.getInputStream())
			);
			// messageInput (stdIn -> Software)
			BufferedReader messageInput = new BufferedReader(
				new InputStreamReader(System.in)
			);

			System.out.println(
				"Connected to " + connection.getInetAddress().getHostName()
			);

			// Thread for receiving messages from the server
			Thread receiveThread = new Thread(() -> {
				try {
					String message;
					while ((message = socketRecieve.readLine()) != null) {
						ChatMessage msg = ChatMessage.fromString(message);
						System.out.println(msg.displayString());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			// Thread for sending messages to the server
			Thread sendThread = new Thread(() -> {
				try {
					String message;
					while ((message = messageInput.readLine()) != null) {
						ChatMessage msg = new ChatMessage(message, username);
						socketSend.println(msg.toString());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			// start Threads to Send and recieve
			receiveThread.start();
			sendThread.start();

			// Wait for both threads to finish (aka leave chat / disconnect)
			receiveThread.join();
			sendThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Connection ended");
			scanner.close();
			connection.close();
		}
	}
}
