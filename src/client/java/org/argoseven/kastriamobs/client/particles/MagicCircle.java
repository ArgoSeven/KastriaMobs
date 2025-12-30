package org.argoseven.kastriamobs.client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.argoseven.kastriamobs.client.KastriaColorHelper;

public class MagicCircle extends AbstractMagicCircle{
    protected MagicCircle(ClientWorld clientWorld, double d, double e, double f, double g, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, g, spriteProvider);
        float[] color = KastriaColorHelper.fromHex("#492f61").toFloatColor();
        this.setColor(color[0], color[1], color[2]);
        this.scale  = 2F;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new MagicCircle(clientWorld, d, e, f, g, this.spriteProvider);
        }
    }

}
