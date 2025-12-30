package org.argoseven.kastriamobs.entity;

import org.argoseven.kastriamobs.Config;

/**
 * Interface for entities that provide attack configuration.
 * Entities implement the specific config getters for attacks they support.
 */
public interface ConfigProvider {

    interface SonicBeamProvider {
        Config.SonicAttackConfig getSonicBeamConfig();
    }

    interface SonicBoomProvider {
        Config.SonicAttackConfig getSonicBoomConfig();
    }

    interface BloodBeamProvider {
        Config.BloodBeamConfig getBloodBeamConfig();
    }

    interface LineFangsProvider {
        Config.FangAttackConfig getLineFangsConfig();
    }

    interface CircleFangsProvider {
        Config.FangAttackConfig getCircleFangsConfig();
    }

    interface CursedBulletProvider {
        Config.CursedBulletConfig getCursedBulletConfig();
    }
}
