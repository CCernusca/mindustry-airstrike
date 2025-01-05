package airstrike.content;

import airstrike.items.SatelliteItem;
import mindustry.type.Item;

public class AirstrikeItems {
    public static Item satelliteItem;

    public static void load() {

        satelliteItem = new SatelliteItem("nuke-satellite") {{
            weapon = AirstrikeWeapons.nuke;
            alwaysUnlocked = true;
        }};

    };
}
