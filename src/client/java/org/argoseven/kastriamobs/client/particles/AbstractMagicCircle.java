package org.argoseven.kastriamobs.client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.function.Consumer;

public abstract class AbstractMagicCircle extends ExplosionLargeParticle {
    private static final Vec3f unknown = (Vec3f) Util.make(new Vec3f(0.5F, 0.5F, 0.5F), Vec3f::normalize);
    protected AbstractMagicCircle(ClientWorld clientWorld, double d, double e, double f, double g, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, g, spriteProvider);
        this.maxAge = 2 * 11;
        this.scale = 2.0F;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public int getBrightness(float tint) {
        return 240;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
       // this.alpha = 1.0F - MathHelper.clamp((this.age + tickDelta) / this.maxAge, 0.0F, 1.0F);
        this.builder(vertexConsumer, camera, tickDelta, quaternion -> {
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(0.0F));
            quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion((float) -1.5708F));
        });
        this.builder(vertexConsumer, camera, tickDelta, quaternion -> {
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion((float) -Math.PI));
            quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(1.5708F));
        });
    }

    private void builder(VertexConsumer vertexConsumer, Camera camera, float tickDelta, Consumer<Quaternion> rotator) {
        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
        Quaternion quaternion = new Quaternion(unknown, 0.0F, true);
        rotator.accept(quaternion);
        unknown.rotate(quaternion);
        Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, 1.0F, 0.0F), new Vec3f(1.0F, 1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F)};
        float i = this.getSize(tickDelta);

        for (int j = 0; j < 4; j++) {
            Vec3f vec3f = vec3fs[j];
            vec3f.rotate(quaternion);
            vec3f.scale(i);
            vec3f.add(f, g, h);
        }

        int j = this.getBrightness(tickDelta);
        this.vertex(vertexConsumer, vec3fs[0], this.getMaxU(), this.getMaxV(), j);
        this.vertex(vertexConsumer, vec3fs[1], this.getMaxU(), this.getMinV(), j);
        this.vertex(vertexConsumer, vec3fs[2], this.getMinU(), this.getMinV(), j);
        this.vertex(vertexConsumer, vec3fs[3], this.getMinU(), this.getMaxV(), j);
    }

    private void vertex(VertexConsumer vertexConsumer, Vec3f pos, float u, float v, int light) {
        vertexConsumer.vertex(pos.getX(), pos.getY(), pos.getZ()).texture(u, v).color(this.red, this.green, this.blue, this.alpha).light(light).next();
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
