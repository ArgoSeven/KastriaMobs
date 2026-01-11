package org.argoseven.kastriamobs;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.argoseven.kastriamobs.KastriaMobs.LOGGER;
import static org.argoseven.kastriamobs.KastriaMobs.configPath;

public class Config {
    
    private static final String CONFIG_RESOURCE_PATH = "config.toml";
    
    public static Config data;
    public Double version;
    public DebugConfig debug;
    public BastionConfig bastion;
    public BlindwrathConfig blindwrath;
    public HollowseerConfig hollowseer;
    public PlaguebruteConfig plaguebrute;
    public ReaverConfig reaver;
    public RedBloodMageConfig red_blood_mage;
    public StalkerConfig stalker;
    public BardConfig bard;

    public static class DebugConfig {
        public boolean enabled = false;
    }

    public static class EntityStatsConfig {
        public double max_health;
        public double movement_speed;
        public double attack_damage;
        public double follow_range;
        public double armor;
        public double armor_toughness;
        public double knockback_resistance;
        public double alert_range;
    }

    public static class SonicAttackConfig {
        public int max_cooldown;
        public float aggro_range;
        public float attack_range;
        public float damage;
        public float vertical_force_multiplier;
        public float horizontal_force_multiplier;
    }

    public static class FangAttackConfig {
        public float aggro_range;
        public float attack_range;
        public int max_cooldown;
        public int number_of_fangs;
        public int number_of_circles;
        public float radius_step;
    }

    public static class BloodBeamConfig {
        public int max_cooldown;
        public float aggro_range;
        public float attack_range;
        public float damage;
        public float attraction_strength;
    }

    public static class CursedBulletConfig {
        public int max_cooldown;
        public float min_shooting_distance;
        public String status_effect;
        public int effect_duration;
        public int effect_amplifier;
    }

    public static class MeleeEffectConfig {
        public String status_effect;
        public int effect_duration;
        public int effect_amplifier;
    }

    public static class BastionConfig extends EntityStatsConfig {
        public MeleeEffectConfig melee_effect;
    }

    public static class StalkerConfig extends EntityStatsConfig {
        public MeleeEffectConfig melee_effect;
    }

    public static class BlindwrathConfig extends EntityStatsConfig {
        public SonicAttackConfig sonicbeam;
    }

    public static class PlaguebruteConfig extends EntityStatsConfig {
        public SonicAttackConfig sonicboom;
        public MeleeEffectConfig melee_effect;
    }

    public static class HollowseerConfig extends EntityStatsConfig {
        public FangAttackConfig evoker_fang_beam;
        public CursedBulletConfig cursed_bullet;
    }

    public static class ReaverConfig extends EntityStatsConfig {
        public FangAttackConfig evoker_fang_circle;
        public MeleeEffectConfig melee_effect;
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
        createModFolder(configPath);
        Toml externalConfigToml = readVersionFromFile(configPath);
        Toml internalConfigToml = readTomlFromResource(KastriaMobs.class.getClassLoader().getResourceAsStream(CONFIG_RESOURCE_PATH));

        if (externalConfigToml == null){
            configEnsurer(configPath);
            externalConfigToml = internalConfigToml;
        }

        LOGGER.info("Reading KastriaMobs config...");
        double internalVersion = internalConfigToml.getDouble("version", -1D);
        double externalVersion = externalConfigToml.getDouble("version", -1D);

        if (internalVersion > externalVersion){
            LOGGER.warn("Config version mismatch: external={} internal={} copying the default one", externalVersion, internalVersion);
            Path oldPath = configPath.resolveSibling(String.format("oldconfig-version%.1f.old", externalVersion).replace(",","_"));
            try { Files.move(configPath, oldPath, StandardCopyOption.REPLACE_EXISTING); } catch (IOException e) { LOGGER.error("Failed to rename config file", e); }
            configEnsurer(configPath);
            externalConfigToml = internalConfigToml;
        }
        data = externalConfigToml.to(Config.class);
    }

    private static void createModFolder(Path config) {
        Path parent = config.getParent();
        if (Files.notExists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create mod folder", e);
            }
        }
    }

    private static Toml readVersionFromFile(Path path) {
        if (!Files.exists(path)) return null;
        return new Toml().read(path.toFile());
    }

    private static Toml readTomlFromResource(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalStateException("Internal config resource missing try to download again the mod!");
        }

        try (InputStream is = inputStream) {
            return new Toml().read(is);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read internal config resource", e);
        }
    }


    private static Path configEnsurer(Path path) {
        if (Files.exists(path)) {
            KastriaMobs.LOGGER.info("Config loaded successfully");
            return path;
        }
        try (InputStream is =  KastriaMobs.class.getClassLoader().getResourceAsStream(CONFIG_RESOURCE_PATH)) {
            if (is == null) {
                KastriaMobs.LOGGER.error("Default config not found in resources: {}", CONFIG_RESOURCE_PATH);
                return path;
            }
            Files.copy(is, path);
            KastriaMobs.LOGGER.info("Default config copied to: {}", path);
        } catch (IOException e) {
            KastriaMobs.LOGGER.error("Failed to copy default config", e);
        }
        return path;
    }
}
