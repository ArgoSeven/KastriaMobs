//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.argoseven.kastriamobs.client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class BloodOrbParticle extends SpriteBillboardParticle {

    BloodOrbParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f);
        this.setBoundingBoxSpacing(0.02F, 0.02F);
        this.scale *= this.random.nextFloat() * 0.6F + 0.2F;
        this.velocityX = g * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
        this.velocityY = h * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
        this.velocityZ = i * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
        this.maxAge = (int)((double)8.0F / (Math.random() * 0.8 + 0.2));
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            BloodOrbParticle bloodOrbParticle = new BloodOrbParticle(clientWorld, d, e, f, g, h, i);
            bloodOrbParticle.setSprite(this.spriteProvider);
            return bloodOrbParticle;
        }
    }
}
