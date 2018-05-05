package ru.flametaichou.duelarena.Model;

import net.minecraft.world.World;

public class Coordinates extends Point3D {

    private World world;

    public Coordinates(World world, int x, int y, int z) {

        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
