package org.argoseven.kastriamobs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.util.Map;

public class Config {
    private int version;
    private Map<String, Entity> entities;

    public int getVersion() { return version; }
    public Map<String, Entity> getEntities() { return entities; }

    public static class Entity {
        private double generic_max_health;
        private double generic_movement_speed;
        private double generic_attack_damage;
        private double generic_follow_range;
        private double generic_armor;
        private double generic_armor_toughness;
        private double generic_knockback_resistance;
        private Map<String, SpecialAttack> special_attack;

        public double getGeneric_max_health() { return generic_max_health; }
        public double getGeneric_movement_speed() { return generic_movement_speed; }
        public double getGeneric_attack_damage() { return generic_attack_damage; }
        public double getGeneric_follow_range() { return generic_follow_range; }
        public double getGeneric_armor() { return generic_armor; }
        public double getGeneric_armor_toughness() { return generic_armor_toughness; }
        public double getGeneric_knockback_resistance() { return generic_knockback_resistance; }
        public Map<String, SpecialAttack> getSpecial_attack() { return special_attack; }
    }

    // --- Nested special attack class ---
    public static class SpecialAttack {
        private Map<String, Object> properties;
        public Map<String, Object> getProperties() { return properties; }
    }

    // --- Loader method ---
    public static Config load(String path) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileReader reader = new FileReader(path)) {
            return gson.fromJson(reader, Config.class);
        }
    }

    // --- Example usage ---
    public static void main(String[] args) throws Exception {
        Config config = Config.load("config/entities.json");

        Entity bastion = config.getEntities().get("bastion");
        System.out.println("Bastion health: " + bastion.getGeneric_max_health());

        Entity blindwrath = config.getEntities().get("blindwrath");
        SpecialAttack sonicbeam = blindwrath.getSpecial_attack().get("sonicbeam");
        System.out.println("Blindwrath sonicbeam damage: " + sonicbeam.getProperties().get("damage"));
    }
}
