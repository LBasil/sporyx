import java.util.*;

public class ExplorationManager {
    private static final Map<String, World> WORLDS = new HashMap<>();

    public ExplorationManager() {
        initializeWorlds();
    }

    private void initializeWorlds() {
        WORLDS.put("Mycelic Forest", new World("Mycelic Forest", 
            "A lush forest filled with giant mushrooms and floating spores.", 
            List.of("Giant Mushroom", "Floating Spore"), 
            Map.of("Spores", 5, "Mushroom Cap", 2)));
        WORLDS.put("Sporic Cavern", new World("Sporic Cavern", 
            "A dark cave glowing with luminescent sporic crystals.", 
            List.of("Sporic Crystal", "Cave Bat"), 
            Map.of("Crystals", 3, "Bat Wing", 1)));
        WORLDS.put("Fungal Abyss", new World("Fungal Abyss", 
            "A deep chasm with bioluminescent fungi and toxic spores.", 
            List.of("Toxic Spore", "Glow Fungus"), 
            Map.of("Toxic Spores", 4, "Glow Essence", 2)));
        System.out.println("Worlds initialized: " + WORLDS.keySet());
    }

    public String getWorldsList() {
        StringBuilder response = new StringBuilder();
        response.append("Available worlds:\n");
        for (String worldName : WORLDS.keySet()) {
            response.append(worldName).append("\n");
        }
        return response.toString();
    }

    public String performExploration(String requestBody) {
        String worldName = parseWorldName(requestBody);
        World world = WORLDS.get(worldName);
        if (world == null) {
            return "Unknown world.";
        }

        try {
            Thread.sleep(10000); // Wait 10 seconds
            StringBuilder response = new StringBuilder();
            response.append("Exploration of ").append(worldName).append(" completed!\n");
            response.append(world.getDescription()).append("\n");
            Random random = new Random();
            if (random.nextBoolean()) {
                Map.Entry<String, Integer> loot = world.getPossibleLoot().entrySet().iterator().next();
                response.append("You found ").append(loot.getValue()).append(" ").append(loot.getKey()).append("!");
            } else {
                response.append("You encountered a ").append(world.getPossibleEncounters().get(random.nextInt(world.getPossibleEncounters().size()))).append(" but fled.");
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

class World {
    private final String name;
    private final String description;
    private final List<String> possibleEncounters;
    private final Map<String, Integer> possibleLoot;

    public World(String name, String description, List<String> possibleEncounters, Map<String, Integer> possibleLoot) {
        this.name = name;
        this.description = description;
        this.possibleEncounters = possibleEncounters;
        this.possibleLoot = possibleLoot;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getPossibleEncounters() {
        return possibleEncounters;
    }

    public Map<String, Integer> getPossibleLoot() {
        return possibleLoot;
    }
}