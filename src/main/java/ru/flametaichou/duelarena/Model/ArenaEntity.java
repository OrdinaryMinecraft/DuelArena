package ru.flametaichou.duelarena.Model;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import ru.flametaichou.duelarena.Util.ConfigHelper;
import ru.flametaichou.duelarena.Util.DatabaseHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ArenaEntity {

    private boolean isBusy;
    private boolean duelTime;
    private long requestTime;
    private EntityPlayerMP player1;
    private EntityPlayerMP player2;
    private Random randomGenerator = new Random();
    public Map<String, Coordinates> playersOnArena;
    public World arenaWorld;
    private ArenaPoint player1Point;
    private ArenaPoint player2Point;
    private long finishTime;

    public void sendRequest(EntityPlayerMP player1, EntityPlayerMP player2) {
        this.setBusy(true);
        this.player1 = player1;
        this.player2 = player2;
        this.requestTime = MinecraftServer.getServer().getTickCounter();
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("duelarena.request.sent", player1.getDisplayName(), player2.getDisplayName()));
    }

    public void requestDenied() {
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("duelarena.request.denied", player2.getDisplayName(), player1.getDisplayName()));
        clearArena();
        DatabaseHelper.updatePlayerPoints(getPlayer2Name(), -10);
    }

    public void requestAccepted() {
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("duelarena.request.accepted", player2.getDisplayName(), player1.getDisplayName()));
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("duelarena.request.accepted2"));
        List<ArenaPoint> arenaPointList = DatabaseHelper.fetchAllArenaPoints();
        if (arenaPointList.size() < 2) {
            clearArena();
        } else {
            setUpPlayerPoints(arenaPointList);
            initArenaWorld();
            savePlayerCoordinates(player1);
            teleportPlayer1ToArena();
            savePlayerCoordinates(player2);
            teleportPlayer2ToArena();
        }
        this.setDuelTime(true);
    }

    public void teleportPlayer1ToArena() {
        //player1.travelToDimension(0);
        //player1.travelToDimension(arenaWorld.provider.dimensionId);
        //player1.mcServer.getConfigurationManager().transferPlayerToDimension(player1, arenaWorld.provider.dimensionId);
        //player1.playerNetServerHandler.setPlayerLocation(arenaPoint.x, arenaPoint.y, arenaPoint.z, player1.rotationYaw, player1.rotationPitch);
        teleportToDimension(player1, arenaWorld.provider.dimensionId, player1Point.x, player1Point.y, player1Point.z);
    }

    public void teleportPlayer2ToArena() {
        //player2.travelToDimension(0);
        //player2.travelToDimension(arenaWorld.provider.dimensionId);
        //player2.mcServer.getConfigurationManager().transferPlayerToDimension(player2, arenaWorld.provider.dimensionId);
        //player2.playerNetServerHandler.setPlayerLocation(arenaPoint.x, arenaPoint.y, arenaPoint.z, player2.rotationYaw, player2.rotationPitch);
        teleportToDimension(player2, arenaWorld.provider.dimensionId, player2Point.x, player2Point.y, player2Point.z);
    }

    public void setUpPlayerPoints(List<ArenaPoint> arenaPointList) {
        int index = randomGenerator.nextInt(arenaPointList.size());
        player1Point = arenaPointList.get(index);
        arenaPointList.remove(index);
        index = randomGenerator.nextInt(arenaPointList.size());
        player2Point = arenaPointList.get(index);
    }

    public void initArenaWorld() {
        arenaWorld = DimensionManager.getWorld(Integer.parseInt(ConfigHelper.arena_world));
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
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("duelarena.win", winPlayer.getDisplayName()));
        DatabaseHelper.updatePlayerPoints(winPlayer.getDisplayName(), 25);
        if (reason.equals("win")) {
            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("duelarena.loose", loosePlayer.getDisplayName()));
            DatabaseHelper.updatePlayerPoints(loosePlayer.getDisplayName(), 5);
            playersOnArena.remove(loosePlayer.getDisplayName());
        } else if (reason.equals("logout")) {
            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("duelarena.logout", loosePlayer.getDisplayName()));
        }

        this.setDuelTime(false);
        this.finishTime = MinecraftServer.getServer().getTickCounter();
    }

    public void duelEnd() {
        teleportPlayersFromArena();
        clearArena();
    }

    public void savePlayerCoordinates(EntityPlayerMP player) {
        if (playersOnArena == null) {
            playersOnArena = new ConcurrentHashMap<String, Coordinates>();
        }
        playersOnArena.put(player.getDisplayName(), new Coordinates(player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ));
    }

    public void teleportPlayersFromArena() {
        initArenaWorld();
        for (Map.Entry<String, Coordinates> playerOnArena : playersOnArena.entrySet()) {
            if (arenaWorld.getPlayerEntityByName(playerOnArena.getKey()) != null) {
                EntityPlayerMP player = (EntityPlayerMP) arenaWorld.getPlayerEntityByName(playerOnArena.getKey());

                //player.travelToDimension(0);
                //player.travelToDimension(arenaWorld.provider.dimensionId);
                //player.mcServer.getConfigurationManager().transferPlayerToDimension(player, playerOnArena.getValue().getWorld().provider.dimensionId);
                //player.playerNetServerHandler.setPlayerLocation(playerOnArena.getValue().x, playerOnArena.getValue().y, playerOnArena.getValue().z, player.rotationYaw, player.rotationPitch);
                teleportToDimension(player, playerOnArena.getValue().getWorld().provider.dimensionId, playerOnArena.getValue().x, playerOnArena.getValue().y, playerOnArena.getValue().z);

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
        this.player1Point = null;
        this.player2Point = null;
        this.finishTime = 0;
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

    public void teleportToDimension(EntityPlayerMP player, int dimensionId, int x, int y, int z) {
        boolean dimensionTeleport = dimensionId != player.getEntityWorld().provider.dimensionId;
        if(dimensionTeleport) {
            player.travelToDimension(dimensionId);
        }
        //player.rotationYaw = getRotationYaw(facing);
        player.setPositionAndUpdate(x + 0.5, y + 0.5, z + 0.5);
    }

    public boolean isDuelTime() {
        return duelTime;
    }

    public void setDuelTime(boolean duelTime) {
        this.duelTime = duelTime;
    }

    public boolean pointIsOutsideTheArena(int x, int z) {
        int x1, x2, z1, z2;

        if (ConfigHelper.arena_x1 > ConfigHelper.arena_x2) {
            x1 = ConfigHelper.arena_x1;
            x2 = ConfigHelper.arena_x2;
        } else {
            x2 = ConfigHelper.arena_x1;
            x1 = ConfigHelper.arena_x2;
        }
        if (ConfigHelper.arena_z1 > ConfigHelper.arena_z2) {
            z1 = ConfigHelper.arena_z1;
            z2 = ConfigHelper.arena_z2;
        } else {
            z2 = ConfigHelper.arena_z1;
            z1 = ConfigHelper.arena_z2;
        }

        if (x > x1 || x < x2) {
            return true;
        }

        if (z > z1 || z < z2) {
            return true;
        }

        return false;
    }

    public long getFinishTime() {
        return finishTime;
    }
}
