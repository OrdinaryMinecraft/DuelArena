package ru.flametaichou.duelarena.Util;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.common.DimensionManager;
import ru.flametaichou.duelarena.DuelArena;
import ru.flametaichou.duelarena.Model.MessageDimension;

public class HandlerDimension implements IMessageHandler<MessageDimension, IMessage> {

    @Override
    public IMessage onMessage(final MessageDimension message, MessageContext ctx) {
        DuelArena.proxy.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                System.out.println("DUELARENA DEBUG: Cheching provider for dim " + message.getDim());
                if (!DimensionManager.isDimensionRegistered(message.getDim())) {
                    System.out.println("DUELARENA DEBUG: Provider not exist, creating");
                    DimensionManager.registerDimension(message.getDim(), 0);
                } else {
                    System.out.println("DUELARENA DEBUG: Provider exist");
                }
            }
        });
        return null;
    }
}
