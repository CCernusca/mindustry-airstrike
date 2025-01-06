package airstrike.items;

import airstrike.airstrikeweapons.AirstrikeWeapon;
import airstrike.content.AirstrikeWeapons;
import arc.graphics.Color;
import mindustry.type.Item;

public class SatelliteItem extends Item {
    public static AirstrikeWeapon weapon;

    public SatelliteItem(String name) {
        super(name);
        this.color = Color.valueOf("#757575");
        this.buildable = false;
        this.explosiveness = 0f;
        this.flammability = 0f;
        this.radioactivity = 0f;
        this.charge = 0f;
    }

    public AirstrikeWeapon getWeapon() {
        return weapon;
    }
}
