import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        int port = 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Lire la requête HTTP
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String requestLine = in.readLine();
                    System.out.println("Request: " + requestLine);

                    // Lire le reste de la requête (pour vider le buffer)
                    String line;
                    while ((line = in.readLine()) != null && !line.isEmpty()) {
                        System.out.println("Header: " + line);
                    }

                    // Vérifier si la requête est un GET /ping
                    String responseBody = "Unknown message";
                    if (requestLine != null && requestLine.startsWith("GET /ping")) {
                        responseBody = "pong";
                    }

                    // Envoyer une réponse HTTP
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/plain");
                    out.println("Content-Length: " + responseBody.length());
                    out.println("Connection: close");
                    out.println();
                    out.println(responseBody);
                    out.flush();
                    System.out.println("Response sent: " + responseBody);

                    // Fermer explicitement le socket
                    clientSocket.close();
                    System.out.println("Connection closed");

                } catch (IOException e) {
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port " + port);
            System.exit(-1);
        }
    }
}