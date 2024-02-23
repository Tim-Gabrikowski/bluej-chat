import java.net.*;
import java.io.*;

/**
 * Die Klasse ChatServer realisiert den Server f?r einen einfachen
 * Chat. Der Server horcht auf Port 5000 auf eine Verbindung von 
 * einem ChatClient. Wird eine Verbindung ge?ffnet, dann liest er 
 * wechselweise Eingaben vom Socket und gibt sie auf der Konsole aus
 * und wartet dann auf eine Eingabe von der Konsole und schickt sie
 * an den Client.
 * 
 * @author   Dr. Michael Janneck, Torsten Otto
 * @version  2023-06-28
 */
class ChatServer {

    public static void main(String[] args) throws IOException {

        /*  Zun?chst wird ein ServerSocket auf Port 5001 erzeugt,
         *  der auf eine eingehende Verbindung wartet.
         */
        ServerSocket serverSocket = new ServerSocket(5001);
        System.out.println("Warte auf Verbindung auf Port 5001.");
        
        /*  Falls eine Verbindung zustande kommt, wird ein Socket
         *  erzeugt, ?ber den die Kommunikation mit dem Client
         *  abgewickelt wird.
         */
        Socket verbindung = serverSocket.accept();
        serverSocket.close();

        /*  Hier werden nun Stream-Objekte f?r die Ein- und Ausgabe
         *  erzeugt und zwei String-Objekte deklariert, die die Ein-
         *  und Ausgabezeilen zwischenspeichern.
         */
        PrintWriter ausgang = new PrintWriter(verbindung.getOutputStream(), true);
        BufferedReader eingang = new BufferedReader(
                new InputStreamReader(verbindung.getInputStream()));
        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
        String nachricht;
        System.out.println("Verbunden mit " + verbindung.getInetAddress().getHostName() + ".");

        /*  Als erstes wird dem Client mitgeteilt, dass er nun verbunden
         *  ist. Danach wird auf eine Nachricht vom Client gewartet und
         *  diese auf der Konsole ausgegeben, woraufhin eine Eingabe von
         *  der Konsole gelesen wird, die an den Client geschickt wird.
         *  Mit ^D (Unix) bzw. ^Z (Windows) wird die Verbindung beendet.
         */
        while (true) {
 
            /*  Lesen vom Socket und Schreiben auf die Konsole */
            if ((nachricht = eingang.readLine()) == null) {
                break;
            } else {
                System.out.println(nachricht);
            }

            /*  Lesen von der Konsole und Schreiben auf den Socket */
            System.out.print("Server: ");
            if ((nachricht = stdIn.readLine()) == null) {
                break;
            } else {
                ausgang.println("Server: " + nachricht);
            }
        }
        System.out.println("Verbindung beendet.");        
        
        /*  Zum Schluss werden alle ge?ffneten Streams und Sockets wieder
         *  in umgekehrter Reihenfolge geschlossen.
         */
        stdIn.close();
        ausgang.close();
        eingang.close();
        verbindung.close();
    }
}