package airstrike;

import airstrike.content.AirstrikeBlocks;
import airstrike.content.AirstrikeItems;
import arc.Events;
import mindustry.mod.*;
import mindustry.game.EventType;

public class AirstrikeMod extends Mod {

    public AirstrikeMod() {}

    @Override
    public void init() {
        super.init();

        // Hook into save & load events
        // Orbital data is saved whenever the current sector is saved
        Events.on(EventType.SaveWriteEvent.class, event -> OrbitalData.saveOrbitalData());
        // Orbital data is loaded whenever a new sector is loaded
        Events.on(EventType.SaveLoadEvent.class, event -> OrbitalData.loadOrbitalData());
    }

    @Override
    public void loadContent() {
        super.loadContent();
        AirstrikeItems.load();  // Items need to be loaded before blocks, as they need them
        AirstrikeBlocks.load();

        // Load orbital data, in case it is needed before a sector is loaded
        OrbitalData.loadOrbitalData();
    }

}
