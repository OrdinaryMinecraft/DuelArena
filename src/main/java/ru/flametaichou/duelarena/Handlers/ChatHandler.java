package ru.flametaichou.duelarena.Handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import ru.flametaichou.duelarena.DuelArena;

public class ChatHandler {

    @SubscribeEvent
    public void onChatEvent(ServerChatEvent event) {
        if (DuelArena.arena.isDuelTime() && event.message.startsWith("/")) {
            if (event.username.equals(DuelArena.arena.getPlayer1Name()) || event.username.equals(DuelArena.arena.getPlayer2Name())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            if (DuelArena.arena.isDuelTime()) {
                EntityPlayer player = (EntityPlayer) event.entityLiving;
                if (player.getDisplayName().equals(DuelArena.arena.getPlayer1Name())) {
                    DuelArena.arena.finishDuel(2, "win");
                }
                if (player.getDisplayName().equals(DuelArena.arena.getPlayer2Name())) {
                    DuelArena.arena.finishDuel(1, "win");
                }
            }
        }

    }

}
