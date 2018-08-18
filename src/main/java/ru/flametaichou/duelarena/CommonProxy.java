package ru.flametaichou.duelarena;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
    }

    public void addScheduledTask(Runnable runnable) {
        runnable.run();
    }
}
