package org.argoseven.kastriamobs.client.debug;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores and renders debug shapes when F3+B hitboxes are enabled.
 * Shapes are automatically removed after their lifetime expires.
 */
public class DebugShapeRenderer {
    
    private static final List<DebugShape> shapes = new ArrayList<>();
    
    public static void addBox(Box box, float red, float green, float blue, float alpha, long lifetimeTicks) {
        shapes.add(new DebugBox(box, red, green, blue, alpha, lifetimeTicks));
    }
    
    public static void addBox(Box box, long lifetimeTicks) {
        addBox(box, 1.0f, 1.0f, 0.0f, 1.0f, lifetimeTicks);
    }
    
    public static void addBeam(Vec3d start, Vec3d end, float red, float green, float blue, float alpha, long lifetimeTicks) {
        shapes.add(new DebugBeam(start, end, red, green, blue, alpha, lifetimeTicks));
    }
    
    public static void addBeam(Vec3d start, Vec3d end, long lifetimeTicks) {
        addBeam(start, end, 0.0f, 1.0f, 1.0f, 1.0f, lifetimeTicks);
    }
    
    public static void render(MatrixStack matrices, VertexConsumer vertices, double cameraX, double cameraY, double cameraZ) {
        Iterator<DebugShape> iterator = shapes.iterator();
        while (iterator.hasNext()) {
            DebugShape shape = iterator.next();
            if (shape.isExpired()) {
                iterator.remove();
            } else {
                shape.render(matrices, vertices, cameraX, cameraY, cameraZ);
            }
        }
    }
    
    public static void tick() {
        shapes.forEach(DebugShape::tick);
    }
    
    public static void clear() {
        shapes.clear();
    }
    
    private static abstract class DebugShape {
        protected final float red, green, blue, alpha;
        protected long remainingTicks;
        
        protected DebugShape(float red, float green, float blue, float alpha, long lifetimeTicks) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
            this.remainingTicks = lifetimeTicks;
        }
        
        public void tick() {
            remainingTicks--;
        }
        
        public boolean isExpired() {
            return remainingTicks <= 0;
        }
        
        public abstract void render(MatrixStack matrices, VertexConsumer vertices, double cameraX, double cameraY, double cameraZ);
    }
    
    private static class DebugBox extends DebugShape {
        private final Box box;
        
        public DebugBox(Box box, float red, float green, float blue, float alpha, long lifetimeTicks) {
            super(red, green, blue, alpha, lifetimeTicks);
            this.box = box;
        }
        
        @Override
        public void render(MatrixStack matrices, VertexConsumer vertices, double cameraX, double cameraY, double cameraZ) {
            WorldRenderer.drawBox(
                    matrices, vertices,
                    box.minX - cameraX, box.minY - cameraY, box.minZ - cameraZ,
                    box.maxX - cameraX, box.maxY - cameraY, box.maxZ - cameraZ,
                    red, green, blue, alpha
            );
        }
    }
    
    private static class DebugBeam extends DebugShape {
        private final Vec3d start;
        private final Vec3d end;
        
        public DebugBeam(Vec3d start, Vec3d end, float red, float green, float blue, float alpha, long lifetimeTicks) {
            super(red, green, blue, alpha, lifetimeTicks);
            this.start = start;
            this.end = end;
        }
        
        @Override
        public void render(MatrixStack matrices, VertexConsumer vertices, double cameraX, double cameraY, double cameraZ) {
            // Draw a thin box along the beam direction
            Vec3d direction = end.subtract(start).normalize();
            double beamWidth = 0.1;
            
            // Create perpendicular vectors for the beam width
            Vec3d perpX = direction.crossProduct(new Vec3d(0, 1, 0)).normalize().multiply(beamWidth);
            Vec3d perpY = direction.crossProduct(perpX).normalize().multiply(beamWidth);
            
            if (perpX.lengthSquared() < 0.001) {
                perpX = new Vec3d(beamWidth, 0, 0);
                perpY = new Vec3d(0, 0, beamWidth);
            }
            
            Box beamBox = new Box(
                    start.x - beamWidth, start.y - beamWidth, start.z - beamWidth,
                    end.x + beamWidth, end.y + beamWidth, end.z + beamWidth
            );
            
            WorldRenderer.drawBox(
                    matrices, vertices,
                    beamBox.minX - cameraX, beamBox.minY - cameraY, beamBox.minZ - cameraZ,
                    beamBox.maxX - cameraX, beamBox.maxY - cameraY, beamBox.maxZ - cameraZ,
                    red, green, blue, alpha
            );
        }
    }
}
