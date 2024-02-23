import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/*
 * Server that stores a List of all the connected Clients and forwards messages between them
 */
public class ChatServer {

	private static List<PrintWriter> clientWriters = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(5001);
		System.out.println("Warte auf Verbindung auf Port 5001.");

		try {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println(
					"Verbunden mit " +
					clientSocket.getInetAddress().getHostName() +
					"."
				);

				PrintWriter writer = new PrintWriter(
					clientSocket.getOutputStream(),
					true
				);
				clientWriters.add(writer);

				Thread clientHandler = new Thread(
					new ClientHandler(clientSocket)
				);
				clientHandler.start();
			}
		} finally {
			serverSocket.close();
		}
	}

	private static class ClientHandler implements Runnable {

		private Socket clientSocket;

		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			try {
				BufferedReader reader = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream())
				);
				String message;

				while ((message = reader.readLine()) != null) {
					System.out.println(message);
					broadcast(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void broadcast(String message) {
			for (PrintWriter writer : clientWriters) {
				writer.println(message);
			}
		}
	}
}
