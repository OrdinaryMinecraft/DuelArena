package ru.flametaichou.duelarena.Handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import ru.flametaichou.duelarena.DuelArena;
import ru.flametaichou.duelarena.Util.ConfigHelper;

public class ServerEventHandler {

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world.getWorldTime() % 50 == 0) {
            if (DuelArena.arena.isBusy() && MinecraftServer.getServer().getTickCounter() - DuelArena.arena.getRequestTime() > ConfigHelper.timeout * 20) {
                DuelArena.arena.requestDenied();
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
}
