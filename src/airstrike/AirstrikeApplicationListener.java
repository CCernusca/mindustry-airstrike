package airstrike;

import arc.ApplicationListener;
import arc.util.Log;

public class AirstrikeApplicationListener implements ApplicationListener {

    // On closing
    @Override
    public void dispose() {
        // Doesn't work, as it isn't being called for some reason, use exit instead
    }

    @Override
    public void init() {
        // Initialization logic if necessary
    }

    @Override
    public void update() {
        // Update logic if necessary
    }

    @Override
    public void pause() {
        // Pause logic if necessary
    }

    @Override
    public void resume() {
        // Resume logic if necessary
    }

    // On game exit
    @Override
    public void exit() {
        ApplicationListener.super.exit();
        Log.info("Game is exiting, saving satellite data...");
        AirstrikeMod.correctSatelliteData();
        AirstrikeMod.saveSatelliteData();
    }
}
