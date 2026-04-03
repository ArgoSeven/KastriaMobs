package org.argoseven.kastriamobs.client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.ColorHelper;
import org.argoseven.kastriamobs.client.KastriaColorHelper;

public class ClawSweep extends SpriteBillboardParticle {
    protected final SpriteProvider spriteProvider;
    protected int lifespan = 4;
    protected float baseScaleMultiplier = 1.0F;

    protected ClawSweep(ClientWorld world, double x, double y, double z, double unused, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.spriteProvider = spriteProvider;
        this.maxAge = this.lifespan;
        int[] c = KastriaColorHelper.fromHex("#492f61").toIntArray();
        this.setColor(c[0], c[1], c[2]);
        this.scale = this.baseScaleMultiplier - (float)unused * 0.5F;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteProvider);
        }
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getBrightness(float tint) {
        return this.brightness();
    }

    protected int brightness() {
        return 15728880;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new ClawSweep(clientWorld, d, e, f, g, this.spriteProvider);
        }
    }

}
