package ru.flametaichou.duelarena.Model;

import javax.vecmath.Point3d;

public class ArenaPoint extends Point3d {

    private int id;

    public ArenaPoint() {
    }

    public ArenaPoint(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ArenaPoint(int id, int x, int y, int z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
