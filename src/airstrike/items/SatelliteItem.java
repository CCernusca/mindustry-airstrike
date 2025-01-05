package airstrike.items;

import airstrike.airstrikeweapons.AirstrikeWeapon;
import arc.Core;
import arc.graphics.Color;
import mindustry.type.Item;

public class SatelliteItem extends Item {
    public static AirstrikeWeapon weapon;

    // Static sprite name to be used for all instances
    private static final String sharedSpriteName = "satellite";

    public SatelliteItem(String name) {
        super(name);
        this.color = Color.valueOf("#757575");
        this.buildable = false;
        this.explosiveness = 0f;
        this.flammability = 0f;
        this.radioactivity = 0f;
        this.charge = 0f;
    }
}
