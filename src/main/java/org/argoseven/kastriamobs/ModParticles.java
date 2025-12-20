package org.argoseven.kastriamobs;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModParticles {
    public static final DefaultParticleType NOTES_PARTICLE = registerParticle("notes", FabricParticleTypes.simple());
    public static final DefaultParticleType BLOOD_BEAM_PARTICLE = registerParticle("blood_beam", FabricParticleTypes.simple());


    private static DefaultParticleType registerParticle(String name, DefaultParticleType particleType) {
        return Registry.register(Registry.PARTICLE_TYPE, Identifier.of(KastriaMobs.MOD_ID, name), particleType);
    }

    public static void registerParticles() {
        KastriaMobs.LOGGER.info("Registering Particles");
    }
}