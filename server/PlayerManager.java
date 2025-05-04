import java.io.*;
import java.util.*;

public class PlayerManager {
    private final Map<String, Map<String, Integer>> players;

    public PlayerManager() {
        this.players = new HashMap<>();
        loadPlayers();
    }

    public void loadPlayers() {
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
        } catch (IOException e) {
            System.out.println("Error loading players: " + e.getMessage());
        }
        System.out.println("Players loaded: " + players);
    }

    public Map<String, Integer> getPlayerStats(String playerName) {
        return players.getOrDefault(playerName, new HashMap<>());
    }

    public List<String> getPlayerNames() {
        return new ArrayList<>(players.keySet());
    }
}