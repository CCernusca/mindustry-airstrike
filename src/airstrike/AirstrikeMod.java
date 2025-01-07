package airstrike;

import airstrike.content.AirstrikeBlocks;
import airstrike.content.AirstrikeItems;
import airstrike.content.AirstrikeWeapons;
import arc.Core;
import arc.util.Log;
import mindustry.mod.*;

public class AirstrikeMod extends Mod {

    public AirstrikeMod() {}

    @Override
    public void init() {
        super.init();
        // Register the application listener
        Core.app.addListener(new AirstrikeApplicationListener());
    }

    @Override
    public void loadContent() {
        super.loadContent();
        AirstrikeBlocks.load();
        AirstrikeWeapons.load();  // Weapons must be loaded before Items, as they are used by Items
        AirstrikeItems.load();

        SatelliteData.loadSatelliteData();
        Log.info("Planet Satellites: " + SatelliteData.planetSatellites);
        Log.info("Sector Satellites: " + SatelliteData.sectorSatellites);
    }

}
