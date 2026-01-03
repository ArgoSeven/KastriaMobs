package org.argoseven.kastriamobs.command;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.RegistryKastriaEntity;

public class KastriaReload {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        LiteralCommandNode<ServerCommandSource> kastriaCommand = dispatcher.register(
                CommandManager.literal("kastria")
                        .then(CommandManager.literal("reload")
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    Config.init();
                                    RegistryKastriaEntity.registerEntityAttributes();
                                    source.sendFeedback(Text.of("Reloading KastriaMobs config..."), true);
                                    KastriaMobs.LOGGER.info("Config reloaded via command by {}", source.getName());
                                    source.sendFeedback(Text.of("DONE"), true);
                                    return 1;
                                })
                        )
        );
    }
}
