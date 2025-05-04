import java.util.*;

public class CombatManager {
    private final PlayerManager playerManager;
    private static final int MAX_CONSECUTIVE_DODGES = 5;

    public CombatManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public String simulateFight() {
        Random random = new Random();
        List<String> playerNames = playerManager.getPlayerNames();
        if (playerNames.size() < 2) {
            return "Not enough players in database.";
        }

        String player1Name = playerNames.get(random.nextInt(playerNames.size()));
        String player2Name;
        do {
            player2Name = playerNames.get(random.nextInt(playerNames.size()));
        } while (player2Name.equals(player1Name));

        Map<String, Integer> player1Stats = playerManager.getPlayerStats(player1Name);
        Map<String, Integer> player2Stats = playerManager.getPlayerStats(player2Name);

        int p1Health = player1Stats.getOrDefault("pv", 10);
        int p1Attack = player1Stats.getOrDefault("attaque", 3);
        int p1Defense = player1Stats.getOrDefault("defense", 0);
        int p1Dodge = player1Stats.getOrDefault("fuite", 0);
        int p1CritChance = player1Stats.getOrDefault("critique", 0);
        int p1Aggressiveness = player1Stats.getOrDefault("agressivite", 0);

        int p2Health = player2Stats.getOrDefault("pv", 10);
        int p2Attack = player2Stats.getOrDefault("attaque", 3);
        int p2Defense = player2Stats.getOrDefault("defense", 0);
        int p2Dodge = player2Stats.getOrDefault("fuite", 0);
        int p2CritChance = player2Stats.getOrDefault("critique", 0);
        int p2Aggressiveness = player2Stats.getOrDefault("agressivite", 0);

        StringBuilder log = new StringBuilder("Fight started!\n");
        log.append(player1Name).append(": HP=").append(p1Health).append(", Attack=").append(p1Attack).append("\n");
        log.append(player2Name).append(": HP=").append(p2Health).append(", Attack=").append(p2Attack).append("\n");

        int p1ConsecutiveDodges = 0;
        int p2ConsecutiveDodges = 0;

        while (p1Health > 0 && p2Health > 0) {
            int p1DodgeChance = 10 + p1Dodge - p1Aggressiveness / 2;
            if (p1DodgeChance < 0) p1DodgeChance = 0;
            if (p1DodgeChance > 90) p1DodgeChance = 90;

            int p2DodgeChance = 10 + p2Dodge - p2Aggressiveness / 2;
            if (p2DodgeChance < 0) p2DodgeChance = 0;
            if (p2DodgeChance > 90) p2DodgeChance = 90;

            if (random.nextInt(100) < p2DodgeChance && p2ConsecutiveDodges < MAX_CONSECUTIVE_DODGES) {
                p2ConsecutiveDodges++;
                p1ConsecutiveDodges = 0;
                log.append(player2Name).append(" dodges ").append(player1Name).append("'s attack!\n");
            } else {
                p2ConsecutiveDodges = 0;
                int damage = calculateDamage(p1Attack, p2Defense, p1CritChance, random);
                p2Health -= damage;
                log.append(player1Name).append(" inflicts ").append(damage).append(" damage to ").append(player2Name)
                   .append(" (HP: ").append(p2Health).append(").\n");
            }

            if (p2Health <= 0) break;

            if (random.nextInt(100) < p1DodgeChance && p1ConsecutiveDodges < MAX_CONSECUTIVE_DODGES) {
                p1ConsecutiveDodges++;
                p2ConsecutiveDodges = 0;
                log.append(player1Name).append(" dodges ").append(player2Name).append("'s attack!\n");
            } else {
                p1ConsecutiveDodges = 0;
                int damage = calculateDamage(p2Attack, p1Defense, p2CritChance, random);
                p1Health -= damage;
                log.append(player2Name).append(" inflicts ").append(damage).append(" damage to ").append(player1Name)
                   .append(" (HP: ").append(p1Health).append(").\n");
            }
        }

        if (p1Health > 0) {
            log.append(player1Name).append(" has won!");
        } else {
            log.append(player2Name).append(" has won!");
        }
        return log.toString();
    }

    private int calculateDamage(int attack, int defense, int critChance, Random random) {
        int baseDamage = attack - defense / 2;
        if (baseDamage < 1) baseDamage = 1;
        if (random.nextInt(100) < critChance) {
            baseDamage *= 2;
            System.out.println("Critical hit!");
        }
        return baseDamage;
    }
}