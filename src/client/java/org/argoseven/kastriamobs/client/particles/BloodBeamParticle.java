//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.argoseven.kastriamobs.client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class BloodBeamParticle extends ExplosionLargeParticle {
    protected BloodBeamParticle(ClientWorld clientWorld, double d, double e, double f, double g, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, g, spriteProvider);
        Random r = new Random();
        this.maxAge = r.nextInt(5,16);
        this.scale = r.nextFloat(0.2F,0.4F);
        this.setSpriteForAge(spriteProvider);
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new BloodBeamParticle(clientWorld, d, e, f, g, this.spriteProvider);
        }
    }
}
