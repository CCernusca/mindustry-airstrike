package airstrike.airstrikeweapons;

import mindustry.world.Tile;

public abstract class AirstrikeWeapon {
    public String id;
    public String name;
    public abstract void impact(Tile impactTile);

    public AirstrikeWeapon(String id, String name) {
        this.id = id;
        this.name = name;
    }

}
