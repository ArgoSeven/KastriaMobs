package org.argoseven.kastriamobs;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.argoseven.kastriamobs.KastriaMobs.configPath;

public class Config {
    public static Config data;
    public Double version;
    public Bastion bastion;
    public Blindwrath blindwrath;
    public Hollowseer hollowseer;
    public Plaguebrute plaguebrute;
    public Reaver reaver;
    public RedBloodMage red_blood_mage;
    public Stalker stalker;

    public static class EntityStatsConfig {
        public double generic_max_health;
        public double generic_movement_speed;
        public double generic_attack_damage;
        public double generic_follow_range;
        public double generic_armor;
        public double generic_armor_toughness;
        public double generic_knockback_resistance;
    }

    public static class SonicAttackConfig {
        public int max_cooldown;
        public float max_range;
        public float damage;
        public float vertical_knock_constant;
        public float horizontal_knock_constant;
    }

    public static class FangAttackConfig {
        public float range_of_activation;
        public int max_cooldown;
        public int number_of_fangs;
        public int number_of_circles;
        public float radius_step;
    }

    public static class BloodBeamConfig {
        public int max_cooldown;
        public float max_range;
        public float damage;
        public float attraction_strength;
    }

    public static class CursedBulletConfig {
        public int max_cooldown;
        public float range_of_activation;
        public float max_range_of_attack;
    }

    public static class Bastion extends EntityStatsConfig {
    }

    public static class Stalker extends EntityStatsConfig {
    }

    public static class Blindwrath extends EntityStatsConfig {
        public SonicAttackConfig sonicbeam;
    }

    public static class Plaguebrute extends EntityStatsConfig {
        public SonicAttackConfig sonicboom;
    }

    public static class Hollowseer extends EntityStatsConfig {
        public FangAttackConfig evoker_fang_beam;
        public CursedBulletConfig cursed_bullet;
    }

    public static class Reaver extends EntityStatsConfig {
        public FangAttackConfig evoker_fang_circle;
    }

    public static class RedBloodMage extends EntityStatsConfig {
        public BloodBeamConfig blood_beam;
    }

    public static Path checkConfig(Path path) {
        if (!Files.exists(path)) {
            try (InputStream inputStream = KastriaMobs.class.getClassLoader().getResourceAsStream("config.toml")) {
                if (inputStream == null) {
                    KastriaMobs.LOGGER.error("File not found in resources: ");
                }
                assert inputStream != null;
                Files.copy(inputStream, path);
                KastriaMobs.LOGGER.info("File copied successfully");
                return path;
            } catch (IOException e) {
                KastriaMobs.LOGGER.error("Error occurred: {}", e.getMessage());
            }
        }
        KastriaMobs.LOGGER.info("Config loaded successfully");
        return path;
    }

    public static void init(){
        data = new Toml().read(new File(checkConfig(configPath).toUri())).to(Config.class);
    }
}
