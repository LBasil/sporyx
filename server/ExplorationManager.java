import java.util.*;

public class ExplorationManager {
    private static final Map<String, String> WORLDS = new HashMap<>();

    public ExplorationManager() {
        initializeWorlds();
    }

    private void initializeWorlds() {
        WORLDS.put("Forêt Mycélique", "A lush forest filled with giant mushrooms and floating spores.");
        WORLDS.put("Caverne Sporique", "A dark cave glowing with luminescent sporadic crystals.");
        System.out.println("Worlds initialized: " + WORLDS);
    }

    public String performExploration(String requestBody) {
        String worldName = parseWorldName(requestBody);
        if (!WORLDS.containsKey(worldName)) {
            return "Unknown world.";
        }

        try {
            Thread.sleep(10000); // Wait 10 seconds
            StringBuilder response = new StringBuilder();
            response.append("Exploration of ").append(worldName).append(" completed!\n");
            response.append(WORLDS.get(worldName)).append("\n");
            Random random = new Random();
            if (random.nextBoolean()) {
                response.append("You found 5 spores!");
            } else {
                response.append("You encountered an enemy but fled.");
            }
            return response.toString();
        } catch (InterruptedException e) {
            return "Exploration interrupted.";
        }
    }

    private String parseWorldName(String body) {
        String worldName = body;
        if (body.contains("world")) {
            int start = body.indexOf(":") + 2;
            int end = body.lastIndexOf("\"");
            worldName = body.substring(start, end);
        }
        return worldName;
    }
}