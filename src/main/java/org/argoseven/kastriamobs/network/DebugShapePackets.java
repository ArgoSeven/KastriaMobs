package org.argoseven.kastriamobs.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;

/**
 * Handles sending debug shape packets from server to client.
 * Packets are only sent when debug mode is enabled in config.
 */
public class DebugShapePackets {
    
    public static final Identifier DEBUG_BOX_PACKET = new Identifier(KastriaMobs.MOD_ID, "debug_box");
    public static final Identifier DEBUG_BEAM_PACKET = new Identifier(KastriaMobs.MOD_ID, "debug_beam");
    
    /**
     * Checks if debug mode is enabled in config.
     */
    public static boolean isDebugEnabled() {
        return Config.data != null && Config.data.debug != null && Config.data.debug.enabled;
    }
    
    /**
     * Sends a debug box to all players who can see the given position.
     * Only sends if debug mode is enabled in config.
     */
    public static void sendDebugBox(ServerWorld world, Box box, float red, float green, float blue, float alpha, long lifetimeTicks) {
        if (!isDebugEnabled()) {
            return;
        }
        
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(box.minX);
        buf.writeDouble(box.minY);
        buf.writeDouble(box.minZ);
        buf.writeDouble(box.maxX);
        buf.writeDouble(box.maxY);
        buf.writeDouble(box.maxZ);
        buf.writeFloat(red);
        buf.writeFloat(green);
        buf.writeFloat(blue);
        buf.writeFloat(alpha);
        buf.writeLong(lifetimeTicks);
        
        Vec3d center = box.getCenter();
        for (ServerPlayerEntity player : PlayerLookup.around(world, center, 64)) {
            ServerPlayNetworking.send(player, DEBUG_BOX_PACKET, buf);
        }
    }
    
    /**
     * Sends a debug box with default yellow color.
     * Only sends if debug mode is enabled in config.
     */
    public static void sendDebugBox(ServerWorld world, Box box, long lifetimeTicks) {
        sendDebugBox(world, box, 1.0f, 1.0f, 0.0f, 1.0f, lifetimeTicks);
    }
    
    /**
     * Sends a debug beam to all players who can see the given position.
     * Only sends if debug mode is enabled in config.
     */
    public static void sendDebugBeam(ServerWorld world, Vec3d start, Vec3d end, float red, float green, float blue, float alpha, long lifetimeTicks) {
        if (!isDebugEnabled()) {
            return;
        }
        
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(start.x);
        buf.writeDouble(start.y);
        buf.writeDouble(start.z);
        buf.writeDouble(end.x);
        buf.writeDouble(end.y);
        buf.writeDouble(end.z);
        buf.writeFloat(red);
        buf.writeFloat(green);
        buf.writeFloat(blue);
        buf.writeFloat(alpha);
        buf.writeLong(lifetimeTicks);
        
        Vec3d center = start.add(end).multiply(0.5);
        for (ServerPlayerEntity player : PlayerLookup.around(world, center, 64)) {
            ServerPlayNetworking.send(player, DEBUG_BEAM_PACKET, buf);
        }
    }
    
    /**
     * Sends a debug beam with default cyan color.
     * Only sends if debug mode is enabled in config.
     */
    public static void sendDebugBeam(ServerWorld world, Vec3d start, Vec3d end, long lifetimeTicks) {
        sendDebugBeam(world, start, end, 0.0f, 1.0f, 1.0f, 1.0f, lifetimeTicks);
    }
}
