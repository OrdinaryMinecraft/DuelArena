package ru.flametaichou.duelarena.Handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import ru.flametaichou.duelarena.DuelArena;
import ru.flametaichou.duelarena.Model.ArenaPoint;
import ru.flametaichou.duelarena.Util.ConfigHelper;
import ru.flametaichou.duelarena.Util.DatabaseHelper;

public class DuelArenaPlayerCommands extends CommandBase
{ 
    private final List<String> aliases;
  
    protected String fullEntityName; 
    protected Entity conjuredEntity; 
  
    public DuelArenaPlayerCommands()
    { 
        aliases = new ArrayList<String>(); 
        aliases.add("duel");
    } 
  
    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
    
    @Override 
    public int compareTo(Object o)
    { 
        return 0; 
    } 

    @Override 
    public String getCommandName() 
    { 
        return "duel";
    } 

    @Override         
    public String getCommandUsage(ICommandSender var1) 
    { 
        return "/duel <(player)/points (player)/top/spectate/yes/no>";
    } 

    @Override 
    public List<String> getCommandAliases() 
    { 
        return this.aliases;
    } 

    @Override 
    public void processCommand(ICommandSender sender, String[] argString) {
        World world = sender.getEntityWorld(); 
    
        if (!world.isRemote) {
            if (argString.length == 0) {
                sender.addChatMessage(new ChatComponentText("/duel <(player)/points (player)/top/spectate/yes/no>"));
                return;
            }
            if (argString[0].equals("points")) {
                if (sender instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) sender;
                    Integer points;
                    if (argString.length < 2) {
                        points = DatabaseHelper.getPlayerPoints(player.getDisplayName());
                        sender.addChatMessage(new ChatComponentTranslation("duel.points.your", points));
                    } else {
                        String anotherPlayerName = argString[1];
                        if (world.getPlayerEntityByName(anotherPlayerName) == null) {
                            sender.addChatMessage(new ChatComponentTranslation("duel.points.none"));
                            return;
                        } else {
                            points = DatabaseHelper.getPlayerPoints(anotherPlayerName);
                            sender.addChatMessage(new ChatComponentTranslation("duel.points.another", anotherPlayerName, points));
                        }
                    }
                }
                return;
            } else if (argString[0].equals("spectate")) {
                if (sender instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) sender;
                    if (DuelArena.arena.isDuelTime()) {
                        DuelArena.arena.teleportToDimension((EntityPlayerMP) player, DuelArena.arena.arenaWorld.provider.dimensionId, ConfigHelper.spectator_x, ConfigHelper.spectator_y, ConfigHelper.spectator_z);
                    }
                }
                return;
            } else if (argString[0].equals("yes")) {
                if (sender instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) sender;
                    if (player.getDisplayName().equals(DuelArena.arena.getPlayer2Name())) {
                        DuelArena.arena.requestAccepted();
                    }
                }
                return;
            } else if (argString[0].equals("no")) {
                if (sender instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) sender;
                    if (player.getDisplayName().equals(DuelArena.arena.getPlayer2Name())) {
                        DuelArena.arena.requestDenied();
                    }
                    return;
                }
                return;
            } else if (argString[0].equals("top")) {
                if (sender instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) sender;
                    List<String> honorPoints = DatabaseHelper.fetchTop10HonorPoints();
                    sender.addChatMessage(new ChatComponentTranslation("duel.points.top"));
                    for (String playerHonorPoints : honorPoints) {
                        sender.addChatMessage(new ChatComponentTranslation(playerHonorPoints));
                    }
                }
                return;
            } else {
                //duel request
                if (sender instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) sender;
                    String secondPlayerName = argString[0];
                    if (!DuelArena.arena.isBusy() && !player.getDisplayName().equals(secondPlayerName)) {

                        boolean playerFounded = false;
                        for (String worldId : ConfigHelper.availableWorlds) {
                            World searchWorld = DimensionManager.getWorld(Integer.parseInt(worldId));
                            if (searchWorld != null && searchWorld.getPlayerEntityByName(secondPlayerName) != null) {
                                sender.addChatMessage(new ChatComponentTranslation("duel.player.sent"));
                                EntityPlayerMP secondPlayer = (EntityPlayerMP) searchWorld.getPlayerEntityByName(secondPlayerName);
                                secondPlayer.addChatMessage(new ChatComponentTranslation("duel.player.recieve"));
                                DuelArena.arena.sendRequest((EntityPlayerMP) player, secondPlayer);
                                playerFounded = true;
                            }
                        }
                        if (!playerFounded) {
                            sender.addChatMessage(new ChatComponentTranslation("duel.player.none"));
                        }

                    } else {
                        sender.addChatMessage(new ChatComponentTranslation("duel.player.arenabusy"));
                    }
                }
                return;
            }
        }
    } 

    @Override 
    public boolean canCommandSenderUseCommand(ICommandSender var1) 
    { 
        return true;
    } 

    @Override  
    public List<?> addTabCompletionOptions(ICommandSender var1, String[] var2) 
    { 
        return null; 
    } 

    @Override 
    public boolean isUsernameIndex(String[] var1, int var2) 
    { 
        return false;
    }
}
