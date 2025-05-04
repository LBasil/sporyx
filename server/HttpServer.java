import java.io.*;
import java.net.*;

public class HttpServer {
    private static final int PORT = 8080;
    private final PlayerManager playerManager;
    private final CombatManager combatManager;
    private final ExplorationManager explorationManager;

    public HttpServer() {
        this.playerManager = new PlayerManager();
        this.combatManager = new CombatManager(playerManager);
        this.explorationManager = new ExplorationManager();
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port " + PORT);
            System.exit(-1);
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String requestLine = in.readLine();
        System.out.println("Request: " + requestLine);

        String line;
        int contentLength = 0;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            System.out.println("Header: " + line);
            if (line.startsWith("Content-Length: ")) {
                contentLength = Integer.parseInt(line.substring("Content-Length: ".length()));
            }
        }

        StringBuilder body = new StringBuilder();
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            body.append(buffer);
            System.out.println("Body: " + body.toString());
        }

        String responseBody = handleRequest(requestLine, body.toString());
        System.out.println("Full response to send:\n" + responseBody);

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/plain");
        out.println("Content-Length: " + responseBody.length());
        out.println("Connection: close");
        out.println();
        out.println(responseBody);
        out.flush();
        System.out.println("Response sent: " + responseBody);

        clientSocket.close();
        System.out.println("Connection closed");
    }

    private String handleRequest(String requestLine, String body) {
        if (requestLine == null) {
            return "Unknown message";
        }
        if (requestLine.startsWith("GET /ping")) {
            return "pong";
        } else if (requestLine.startsWith("GET /worlds")) {
            return explorationManager.getWorldsList();
        } else if (requestLine.startsWith("POST /fight")) {
            return combatManager.simulateFight();
        } else if (requestLine.startsWith("POST /explore")) {
            return explorationManager.performExploration(body);
        }
        return "Unknown message";
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer();
        server.start();
    }
}