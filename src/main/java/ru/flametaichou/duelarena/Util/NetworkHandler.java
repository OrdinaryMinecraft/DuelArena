package ru.flametaichou.duelarena.Util;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import ru.flametaichou.duelarena.Model.MessageDimension;

public class NetworkHandler {

    public static final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel("duelarena");

    public static void init() {
        channel.registerMessage(HandlerDimension.class, MessageDimension.class, 0, Side.CLIENT);
    }

}
