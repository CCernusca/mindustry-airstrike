package airstrike;

import airstrike.airstrikeweapons.AirstrikeWeapon;
import airstrike.content.AirstrikeBlocks;
import airstrike.content.AirstrikeItems;
import airstrike.content.AirstrikeWeapons;
import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import mindustry.Vars;
import mindustry.mod.*;
import mindustry.type.Planet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static airstrike.AirstrilkeUtils.getSaves;

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
    }






}
