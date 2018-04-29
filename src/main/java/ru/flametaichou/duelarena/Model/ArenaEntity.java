package ru.flametaichou.duelarena.Model;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import ru.flametaichou.duelarena.DuelArena;
import ru.flametaichou.duelarena.Util.ConfigHelper;

import java.util.*;

public class ArenaEntity {

    private boolean isBusy;
    private boolean duelTime;
    private long requestTime;
    private EntityPlayerMP player1;
    private EntityPlayerMP player2;
    private Random randomGenerator = new Random();
    public Map<String, Coordinates> playersOnArena;
    private World arenaWorld = DimensionManager.getWorld(Integer.parseInt(ConfigHelper.arena_world));

    public void sendRequest(EntityPlayerMP player1, EntityPlayerMP player2) {
        this.setBusy(true);
        this.player1 = player1;
        this.player2 = player2;
        this.requestTime = MinecraftServer.getServer().getTickCounter();
        MinecraftServer.getServer().addChatMessage(new ChatComponentTranslation("duelarena.request.sent", player1.getDisplayName(), player2.getDisplayName()));
    }

    public void requestDenied() {
        MinecraftServer.getServer().addChatMessage(new ChatComponentTranslation("duelarena.request.denied", player2.getDisplayName(), player1.getDisplayName()));
        clearArena();
        DuelArena.database.updatePlayerPoints(getPlayer2Name(), -10);
    }

    public void requestAccepted() {
        MinecraftServer.getServer().addChatMessage(new ChatComponentTranslation("duelarena.request.accepted", player2.getDisplayName(), player1.getDisplayName()));
        MinecraftServer.getServer().addChatMessage(new ChatComponentTranslation("duelarena.request.accepted2"));

        this.setDuelTime(true);
        List<ArenaPoint> arenaPointList = DuelArena.database.fetchAllArenaPoints();
        if (arenaPointList.size() < 2) {
            clearArena();
        } else {
            int index = randomGenerator.nextInt(arenaPointList.size());
            ArenaPoint arenaPoint = arenaPointList.get(index);
            savePlayerCoordinates(player1);
            player1.setWorld(arenaWorld);
            player1.setPositionAndUpdate(arenaPoint.x, arenaPoint.y, arenaPoint.z);
            arenaPointList.remove(index);
            index = randomGenerator.nextInt(arenaPointList.size());
            arenaPoint = arenaPointList.get(index);
            savePlayerCoordinates(player2);
            player2.setWorld(arenaWorld);
            player2.setPositionAndUpdate(arenaPoint.x, arenaPoint.y, arenaPoint.z);
        }
    }

    public void finishDuel(int playerWin, String reason) {
        EntityPlayerMP winPlayer;
        EntityPlayerMP loosePlayer;
        if (playerWin == 1) {
            winPlayer = player1;
             loosePlayer = player2;
        } else {
            winPlayer = player2;
            loosePlayer = player1;
        }
        MinecraftServer.getServer().addChatMessage(new ChatComponentTranslation("duelarena.win", winPlayer.getDisplayName()));
        DuelArena.database.updatePlayerPoints(winPlayer.getDisplayName(), 25);
        if (reason.equals("win")) {
            MinecraftServer.getServer().addChatMessage(new ChatComponentTranslation("duelarena.loose", loosePlayer.getDisplayName()));
            DuelArena.database.updatePlayerPoints(loosePlayer.getDisplayName(), 5);
        } else if (reason.equals("logout")) {
            MinecraftServer.getServer().addChatMessage(new ChatComponentTranslation("duelarena.logout", loosePlayer.getDisplayName()));
        }

        teleportPlayersFromArena();
        clearArena();

    }

    public void savePlayerCoordinates(EntityPlayerMP player) {
        if (playersOnArena == null) {
            playersOnArena = new HashMap<String, Coordinates>();
        }
        playersOnArena.put(player.getDisplayName(), new Coordinates(player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ));
    }

    public void teleportPlayersFromArena() {
        for (Map.Entry<String, Coordinates> playerOnArena : playersOnArena.entrySet()) {
            if (arenaWorld.getPlayerEntityByName(playerOnArena.getKey()) != null) {
                EntityPlayerMP player = (EntityPlayerMP) arenaWorld.getPlayerEntityByName(playerOnArena.getKey());
                player.setWorld(playerOnArena.getValue().getWorld());
                player.setPositionAndUpdate(playerOnArena.getValue().x, playerOnArena.getValue().y, playerOnArena.getValue().z);
                playersOnArena.remove(playerOnArena.getKey());
            }
        }
    }

    public void clearArena() {
        this.setBusy(false);
        this.player1 = null;
        this.player2 = null;
        this.requestTime = 0;
        this.setDuelTime(false);
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public String getPlayer1Name() {
        if (player1 != null) {
            return player1.getDisplayName();
        }
        return "";
    }

    public String getPlayer2Name() {
        if (player2 != null) {
            return player2.getDisplayName();
        }
        return "";
    }

    public boolean isDuelTime() {
        return duelTime;
    }

    public void setDuelTime(boolean duelTime) {
        this.duelTime = duelTime;
    }
}
