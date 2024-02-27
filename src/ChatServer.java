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
		int serverPort = (args.length > 0) ? Integer.parseInt(args[0]) : 5001;
		// start Server
		ServerSocket serverSocket = new ServerSocket(serverPort);
		System.out.println("Listening in Port " + serverPort);

		try {
			while (true) {
				// wait for new client connections
				Socket clientSocket = serverSocket.accept();
				System.out.println(
					"Client connected: " +
					clientSocket.getInetAddress().getHostName()
				);

				// Get clientSendStream to send to client (Server -> Client)
				PrintWriter clientSendStream = new PrintWriter(
					clientSocket.getOutputStream(),
					true
				);
				clientWriters.add(clientSendStream);

				// Create clientHandler that recieves Messages from client and handles them
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
				// get clientRecieve stream (Client -> Server)
				BufferedReader clientRecieve = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream())
				);
				String message;

				while ((message = clientRecieve.readLine()) != null) {
					// brodcast message to all clinets
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
