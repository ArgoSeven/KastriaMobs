package org.argoseven.kastriamobs.client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.argoseven.kastriamobs.client.KastriaColorHelper;
import org.argoseven.kastriamobs.client.KastriaMobsClient;
import org.jetbrains.annotations.Nullable;

public class Notes extends AnimatedParticle {
    int[] color = KastriaColorHelper.randomColor().toIntArray();
    protected Notes(ClientWorld clientWorld, double d, double e, double f,double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, spriteProvider, 0.0125F);
        this.velocityMultiplier = 0.96F;
        this.gravityStrength = -0.1F;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;

        this.setColor(color[0], color[1], color[2]);
        this.scale *= 0.75F;
        this.maxAge = 60 + this.random.nextInt(5);
        this.collidesWithWorld = false;
        this.setSpriteForAge(spriteProvider);
    }


    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public @Nullable Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new Notes(world, x,  y,  z, velocityX,velocityY,velocityZ, this.spriteProvider);
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}
