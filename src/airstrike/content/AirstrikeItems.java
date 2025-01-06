package airstrike.content;

import airstrike.items.SatelliteItem;
import arc.util.Log;

public class AirstrikeItems {
    public static SatelliteItem nukeSatellite;

    public static void load() {

        nukeSatellite = new SatelliteItem("nuke-satellite") {{
            weapon = AirstrikeWeapons.nuke;
            alwaysUnlocked = true;
        }};

    };
}
