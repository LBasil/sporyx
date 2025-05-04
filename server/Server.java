import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static Map<String, Map<String, Integer>> players = new HashMap<>();
    private static Map<String, String> worlds = new HashMap<>();

    public static void main(String[] args) throws IOException {
        loadPlayers();
        initializeWorlds();
        int port = 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

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

                    String responseBody = "Unknown message";
                    if (requestLine != null) {
                        if (requestLine.startsWith("GET /ping")) {
                            responseBody = "pong";
                        } else if (requestLine.startsWith("POST /fight")) {
                            responseBody = simulateFight();
                        } else if (requestLine.startsWith("POST /explore")) {
                            responseBody = performExploration(body.toString());
                        }
                    }

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
                } catch (IOException e) {
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port " + port);
            System.exit(-1);
        }
    }

    private static void loadPlayers() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("players.txt"))) {
            String line;
            String currentPlayer = null;
            Map<String, Integer> stats = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.endsWith(":")) {
                    currentPlayer = line.substring(0, line.length() - 1);
                    stats = new HashMap<>();
                    players.put(currentPlayer, stats);
                } else if (currentPlayer != null && line.contains(":")) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        stats.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                    }
                }
            }
        }
        System.out.println("Players loaded: " + players);
    }

    private static void initializeWorlds() {
        worlds.put("Forêt Mycélique", "Une forêt luxuriante remplie de champignons géants et de spores flottantes.");
        worlds.put("Caverne Sporique", "Une grotte sombre où brillent des cristaux sporiques luminescents.");
        System.out.println("Worlds initialized: " + worlds);
    }

    private static String performExploration(String requestBody) {
        String worldName = parseWorldName(requestBody);
        if (!worlds.containsKey(worldName)) {
            return "Monde inconnu.";
        }

        try {
            Thread.sleep(10000); // Attendre 10 secondes
            StringBuilder response = new StringBuilder();
            response.append("Exploration de ").append(worldName).append(" terminée !\n");
            response.append(worlds.get(worldName)).append("\n");
            Random rand = new Random();
            if (rand.nextBoolean()) {
                response.append("Vous avez trouvé 5 spores !");
            } else {
                response.append("Vous avez rencontré un ennemi mais vous avez fui.");
            }
            return response.toString();
        } catch (InterruptedException e) {
            return "Exploration interrompue.";
        }
    }

    private static String parseWorldName(String body) {
        String worldName = body;
        if (body.contains("world")) {
            int start = body.indexOf(":") + 2;
            int end = body.lastIndexOf("\"");
            worldName = body.substring(start, end);
        }
        return worldName;
    }

    private static String simulateFight() {
        Random rand = new Random();
        List<String> playerNames = new ArrayList<>(players.keySet());
        if (playerNames.size() < 2) return "Not enough players in database.";

        String player1Name = playerNames.get(rand.nextInt(playerNames.size()));
        String player2Name;
        do {
            player2Name = playerNames.get(rand.nextInt(playerNames.size()));
        } while (player2Name.equals(player1Name));

        Map<String, Integer> player1Stats = players.get(player1Name);
        Map<String, Integer> player2Stats = players.get(player2Name);

        int p1Pv = player1Stats.get("pv");
        int p1Attaque = player1Stats.get("attaque");
        int p1Defense = player1Stats.get("defense");
        int p1Fuite = player1Stats.get("fuite");
        int p1Critique = player1Stats.get("critique");
        int p1Agressivite = player1Stats.get("agressivite");

        int p2Pv = player2Stats.get("pv");
        int p2Attaque = player2Stats.get("attaque");
        int p2Defense = player2Stats.get("defense");
        int p2Fuite = player2Stats.get("fuite");
        int p2Critique = player2Stats.get("critique");
        int p2Agressivite = player2Stats.get("agressivite");

        StringBuilder log = new StringBuilder("Début du combat !\n");
        log.append(player1Name).append(": HP=").append(p1Pv).append(", Attaque=").append(p1Attaque).append("\n");
        log.append(player2Name).append(": HP=").append(p2Pv).append(", Attaque=").append(p2Attaque).append("\n");

        int p1ConsecutiveDodges = 0;
        int p2ConsecutiveDodges = 0;
        int maxConsecutiveDodges = 5;

        while (p1Pv > 0 && p2Pv > 0) {
            int p1DodgeChance = 10 + p1Fuite - p1Agressivite / 2;
            if (p1DodgeChance < 0) p1DodgeChance = 0;
            if (p1DodgeChance > 90) p1DodgeChance = 90;

            int p2DodgeChance = 10 + p2Fuite - p2Agressivite / 2;
            if (p2DodgeChance < 0) p2DodgeChance = 0;
            if (p2DodgeChance > 90) p2DodgeChance = 90;

            if (rand.nextInt(100) < p2DodgeChance && p2ConsecutiveDodges < maxConsecutiveDodges) {
                p2ConsecutiveDodges++;
                p1ConsecutiveDodges = 0;
                log.append(player2Name).append(" esquive l'attaque de ").append(player1Name).append(" !\n");
            } else {
                p2ConsecutiveDodges = 0;
                int damage = calculateDamage(p1Attaque, p2Defense, p1Critique, rand);
                p2Pv -= damage;
                log.append(player1Name).append(" inflige ").append(damage).append(" dégâts à ").append(player2Name)
                   .append(" (HP: ").append(p2Pv).append(").\n");
            }

            if (p2Pv <= 0) break;

            if (rand.nextInt(100) < p1DodgeChance && p1ConsecutiveDodges < maxConsecutiveDodges) {
                p1ConsecutiveDodges++;
                p2ConsecutiveDodges = 0;
                log.append(player1Name).append(" esquive l'attaque de ").append(player2Name).append(" !\n");
            } else {
                p1ConsecutiveDodges = 0;
                int damage = calculateDamage(p2Attaque, p1Defense, p2Critique, rand);
                p1Pv -= damage;
                log.append(player2Name).append(" inflige ").append(damage).append(" dégâts à ").append(player1Name)
                   .append(" (HP: ").append(p1Pv).append(").\n");
            }
        }

        if (p1Pv > 0) {
            log.append(player1Name).append(" a gagné !");
        } else {
            log.append(player2Name).append(" a gagné !");
        }
        return log.toString();
    }

    private static int calculateDamage(int attaque, int defense, int critique, Random rand) {
        int baseDamage = attaque - defense / 2;
        if (baseDamage < 1) baseDamage = 1;
        if (rand.nextInt(100) < critique) {
            baseDamage *= 2;
            System.out.println("Coup critique !");
        }
        return baseDamage;
    }
}