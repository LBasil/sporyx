import java.io.*;
import java.net.*;
import java.util.Random;

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

                    // Lire les en-têtes
                    String line;
                    int contentLength = 0;
                    while ((line = in.readLine()) != null && !line.isEmpty()) {
                        System.out.println("Header: " + line);
                        if (line.startsWith("Content-Length: ")) {
                            contentLength = Integer.parseInt(line.substring("Content-Length: ".length()));
                        }
                    }

                    // Lire le corps de la requête (si POST)
                    StringBuilder body = new StringBuilder();
                    if (contentLength > 0) {
                        char[] buffer = new char[contentLength];
                        in.read(buffer, 0, contentLength);
                        body.append(buffer);
                        System.out.println("Body: " + body.toString());
                    }

                    // Préparer la réponse
                    String responseBody = "Unknown message";
                    if (requestLine != null) {
                        // Gérer GET /ping
                        if (requestLine.startsWith("GET /ping")) {
                            responseBody = "pong";
                        }
                        // Gérer POST /fight
                        else if (requestLine.startsWith("POST /fight")) {
                            // Simuler un combat
                            responseBody = simulateFight(body.toString());
                        }
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

    private static String simulateFight(String requestBody) {
        try {
            // Extraire les stats du joueur
            String playerJson = extractJsonObject(requestBody, "\"player\":");
            int playerHp = extractValue(playerJson, "\"hp\":", ",");
            int playerAttack = extractValue(playerJson, "\"attack\":", "}");

            // Générer un adversaire (un autre joueur) avec des stats similaires mais légèrement aléatoires
            Random rand = new Random();
            int opponentHp = playerHp + rand.nextInt(5) - 2; // HP entre playerHp-2 et playerHp+2
            int opponentAttack = playerAttack + rand.nextInt(3) - 1; // Attaque entre playerAttack-1 et playerAttack+1
            if (opponentHp < 1) opponentHp = 1; // Éviter HP négatif
            if (opponentAttack < 1) opponentAttack = 1; // Éviter attaque négative

            System.out.println("Player: HP=" + playerHp + ", Attack=" + playerAttack);
            System.out.println("Opponent: HP=" + opponentHp + ", Attack=" + opponentAttack);

            // Simuler le combat avec aléatoire
            while (playerHp > 0 && opponentHp > 0) {
                // Attaque du joueur
                if (rand.nextFloat() > 0.3) { // 70% de chance de toucher
                    int damage = playerAttack + rand.nextInt(3) - 1; // Dégâts entre attack-1 et attack+1
                    if (damage < 1) damage = 1;
                    opponentHp -= damage;
                    System.out.println("Player hits Opponent for " + damage + " damage. Opponent HP: " + opponentHp);
                } else {
                    System.out.println("Player misses!");
                }

                if (opponentHp <= 0) break;

                // Attaque de l'adversaire
                if (rand.nextFloat() > 0.3) { // 70% de chance de toucher
                    int damage = opponentAttack + rand.nextInt(3) - 1; // Dégâts entre attack-1 et attack+1
                    if (damage < 1) damage = 1;
                    playerHp -= damage;
                    System.out.println("Opponent hits Player for " + damage + " damage. Player HP: " + playerHp);
                } else {
                    System.out.println("Opponent misses!");
                }
            }

            if (playerHp > 0) {
                return "Vous avez gagné !";
            } else {
                return "Vous avez perdu !";
            }
        } catch (Exception e) {
            return "Erreur lors du combat: " + e.getMessage();
        }
    }

    private static String extractJsonObject(String json, String key) {
        int startIndex = json.indexOf(key) + key.length();
        int braceCount = 0;
        int i = startIndex;
        boolean insideObject = false;

        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '{') {
                braceCount++;
                insideObject = true;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0 && insideObject) {
                    return json.substring(startIndex, i + 1);
                }
            }
            i++;
        }
        throw new IllegalArgumentException("Could not find closing brace for " + key);
    }

    private static int extractValue(String json, String key, String endDelimiter) {
        int startIndex = json.indexOf(key) + key.length();
        int endIndex = json.indexOf(endDelimiter, startIndex);
        if (endIndex == -1) {
            endIndex = json.length();
        }
        String value = json.substring(startIndex, endIndex).trim();
        // Supprimer les caractères non numériques (ex. : "8}" -> "8")
        value = value.replaceAll("[^0-9]", "");
        return Integer.parseInt(value);
    }
}