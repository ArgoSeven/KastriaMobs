package org.argoseven.kastriamobs;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.argoseven.kastriamobs.KastriaMobs.configPath;

public class Config {
    
    private static final String CONFIG_RESOURCE_PATH = "config.toml";
    
    public static Config data;
    public Double version;
    public BastionConfig bastion;
    public BlindwrathConfig blindwrath;
    public HollowseerConfig hollowseer;
    public PlaguebruteConfig plaguebrute;
    public ReaverConfig reaver;
    public RedBloodMageConfig red_blood_mage;
    public StalkerConfig stalker;
    public BardConfig bard;

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
        public String status_effect;
        public int effect_duration;
        public int effect_amplifier;
    }

    public static class BastionConfig extends EntityStatsConfig {
    }

    public static class StalkerConfig extends EntityStatsConfig {
    }

    public static class BlindwrathConfig extends EntityStatsConfig {
        public SonicAttackConfig sonicbeam;
    }

    public static class PlaguebruteConfig extends EntityStatsConfig {
        public SonicAttackConfig sonicboom;
    }

    public static class HollowseerConfig extends EntityStatsConfig {
        public FangAttackConfig evoker_fang_beam;
        public CursedBulletConfig cursed_bullet;
    }

    public static class ReaverConfig extends EntityStatsConfig {
        public FangAttackConfig evoker_fang_circle;
    }

    public static class RedBloodMageConfig extends EntityStatsConfig {
        public BloodBeamConfig blood_beam;
    }

    public static class BardConfig extends EntityStatsConfig {
        public FangAttackConfig evoker_fang_beam;
        public FangAttackConfig evoker_fang_circle;
        public CursedBulletConfig cursed_bullet;
        public BloodBeamConfig blood_beam;
        public SonicAttackConfig sonicboom;
        public SonicAttackConfig sonicbeam;
    }

    public static void init() {
        Path path = ensureConfigExists(configPath);
        data = new Toml().read(new File(path.toUri())).to(Config.class);
    }

    private static Path ensureConfigExists(Path path) {
        if (Files.exists(path)) {
            KastriaMobs.LOGGER.info("Config loaded successfully");
            return path;
        }

        try (InputStream inputStream = KastriaMobs.class.getClassLoader().getResourceAsStream(CONFIG_RESOURCE_PATH)) {
            if (inputStream == null) {
                KastriaMobs.LOGGER.error("Default config not found in resources: {}", CONFIG_RESOURCE_PATH);
                return path;
            }
            Files.copy(inputStream, path);
            KastriaMobs.LOGGER.info("Default config copied to: {}", path);
        } catch (IOException e) {
            KastriaMobs.LOGGER.error("Failed to copy default config", e);
        }

        return path;
    }
}
