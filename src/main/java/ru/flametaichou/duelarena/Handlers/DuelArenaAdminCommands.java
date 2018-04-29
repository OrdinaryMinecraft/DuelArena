package ru.flametaichou.duelarena.Handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import ru.flametaichou.duelarena.DuelArena;
import ru.flametaichou.duelarena.Model.ArenaPoint;

public class DuelArenaAdminCommands extends CommandBase
{
    private final List<String> aliases;

    protected String fullEntityName;
    protected Entity conjuredEntity;

    public DuelArenaAdminCommands()
    {
        aliases = new ArrayList<String>();
        aliases.add("duelarena");
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
        return "duelarena";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/duelarena <listpoints/addpoint/removepoint>";
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
                sender.addChatMessage(new ChatComponentText("/duelarena <listpoints/addpoint/removepoint (id)>"));
                return;
            }
            if (argString[0].equals("listpoints")) {
                if (sender instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) sender;
                    List<ArenaPoint> arenaPoints = DuelArena.database.fetchAllArenaPoints();
                    sender.addChatMessage(new ChatComponentTranslation("listpoints.message"));
                    for (ArenaPoint point : arenaPoints) {
                        sender.addChatMessage(new ChatComponentTranslation("ID: "+point.getId()+", X:"+point.getX()+", Y:"+point.getY()+", Z:"+point.getZ()));
                    }
                }
                return;
            }
            if (argString[0].equals("addpoint")) {
                if (sender instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) sender;
                    int player_x = (int) player.posX;
                    int player_y = (int) player.posY;
                    int player_z = (int) player.posZ;
                    DuelArena.database.addArenaPoint(player_x, player_y, player_z);
                    sender.addChatMessage(new ChatComponentTranslation("addpoint.done"));
                }
                return;
            }
            if (argString[0].equals("removepoint")) {
                if (sender instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) sender;
                    if (argString.length < 2) {
                        sender.addChatMessage(new ChatComponentTranslation("removepoint.noid"));
                        return;
                    }
                    Integer id = Integer.parseInt(argString[1]);
                    if (id == null) {
                        sender.addChatMessage(new ChatComponentTranslation("removepoint.noid"));
                        return;
                    }
                    DuelArena.database.removeArenaPoint(id);
                    sender.addChatMessage(new ChatComponentTranslation("removepoint.done"));
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
