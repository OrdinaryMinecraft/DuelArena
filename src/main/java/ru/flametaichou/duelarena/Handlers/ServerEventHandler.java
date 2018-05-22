package ru.flametaichou.duelarena.Handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import ru.flametaichou.duelarena.DuelArena;
import ru.flametaichou.duelarena.Util.ConfigHelper;

public class ServerEventHandler {

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world.getWorldTime() % 50 == 0) {
            if (DuelArena.arena.isBusy() && !DuelArena.arena.isDuelTime()) {
                if (DuelArena.arena.getFinishTime() == 0) {
                    if (MinecraftServer.getServer().getTickCounter() - DuelArena.arena.getRequestTime() > ConfigHelper.timeout * 20) {
                        DuelArena.arena.requestDenied();
                    }
                }

                if (DuelArena.arena.getFinishTime() != 0) {
                    if (MinecraftServer.getServer().getTickCounter() - DuelArena.arena.getFinishTime() > 10 * 20) {
                        DuelArena.arena.duelEnd();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {

    }

    @SubscribeEvent
    public void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (DuelArena.arena.isDuelTime()) {
            EntityPlayer player = event.player;
            if (player.getDisplayName().equals(DuelArena.arena.getPlayer1Name())) {
                DuelArena.arena.finishDuel(2, "logout");
            }
            if (player.getDisplayName().equals(DuelArena.arena.getPlayer2Name())) {
                DuelArena.arena.finishDuel(1, "logout");
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (DuelArena.arena.playersOnArena != null && !DuelArena.arena.playersOnArena.isEmpty()) {
            DuelArena.arena.teleportPlayersFromArena();
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(TickEvent.PlayerTickEvent event) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (event.phase == TickEvent.Phase.START) {
            EntityPlayer player = event.player;
            if (player.worldObj.getWorldTime() % 50 == 0) {
                if (DuelArena.arena.isDuelTime()) {
                    if (player.getDisplayName().equals(DuelArena.arena.getPlayer1Name())) {
                        if (DuelArena.arena.pointIsOutsideTheArena((int)player.posX, (int)player.posZ)) {
                            DuelArena.arena.teleportPlayer1ToArena();
                            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("duelarena.return", DuelArena.arena.getPlayer1Name()));
                        }
                    } else if (player.getDisplayName().equals(DuelArena.arena.getPlayer2Name())) {
                        if (DuelArena.arena.pointIsOutsideTheArena((int)player.posX, (int)player.posZ)) {
                            DuelArena.arena.teleportPlayer2ToArena();
                            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("duelarena.return", DuelArena.arena.getPlayer2Name()));
                        }
                    }
                }
            }
        }
    }
}
