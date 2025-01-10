package airstrike.items;

import arc.graphics.Color;
import mindustry.type.Item;

public class SatelliteItem extends Item {
    public float volume;

    public SatelliteItem(String name) {
        super(name);
        this.buildable = false;
        this.explosiveness = 0f;
        this.flammability = 0f;
        this.radioactivity = 0f;
        this.charge = 0f;
    }

}
