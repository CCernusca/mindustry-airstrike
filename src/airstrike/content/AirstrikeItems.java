package airstrike.content;

import airstrike.items.SatelliteItem;
import mindustry.type.Item;

public class AirstrikeItems {
    // TODO: Add Satellite class
    public static Item satelliteItem;

    public static void load() {

        satelliteItem = new SatelliteItem("satellite-item") {{
            satellite = "satellite";
            alwaysUnlocked = true;
        }};

    };
}
