package airstrike.blocks;

import airstrike.OrbitalData;
import airstrike.items.AirstrikeWeapon;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.ui.Styles;
import mindustry.world.Tile;

public class OrbitalController extends Beacon {
    public Tile target;

    public OrbitalController(String name) {
        super(name);
        buildType = OrbitalControllerBuild::new;
    }

    public class OrbitalControllerBuild extends BeaconBuild {

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            Building building = super.init(tile, team, shouldAdd, rotation);
            target = this.tile;
            return building;
        }

        @Override
        public void call() {
            if (selected < weapons.size()) {
                AirstrikeWeapon selectedWeapon = weapons.get(selected);
                if (OrbitalData.removeOrbitalWeapon(selectedWeapon)) {
                    deselect();
                    selected = 0; // Reset selection, in case block is not destroyed
                    selectedWeapon.impact(target, impactDelay);
                } else {
                    // Should be impossible, only weapons in orbit are displayed
                    Log.err("Selected weapon not in orbit");
                }
            } else {
                Log.err("Invalid selection");
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            super.buildConfiguration(table);

            // Add target selection
            Table targetTable = new Table();
            targetTable.background(Styles.black6);
            // Inputs
            TextField targetPosX = new TextField(String.valueOf(target.x), Styles.defaultField);
            TextField targetPosY = new TextField(String.valueOf(target.y), Styles.defaultField);
            // Labels
            Label xLabel = new Label("x", Styles.defaultLabel);
            Label yLabel = new Label("y", Styles.defaultLabel);
            // Filter input to digits only
            targetPosX.setFilter(TextField.TextFieldFilter.digitsOnly);
            targetPosY.setFilter(TextField.TextFieldFilter.digitsOnly);
            // Exit listeners to update target
            targetPosX.exited(() -> {
                target = Vars.world.tile(Integer.parseInt(targetPosX.getText()), target.y);
            });
            targetPosY.exited(() -> {
                target = Vars.world.tile(target.x, Integer.parseInt(targetPosY.getText()));
            });
            // Add to the targetTable
            targetTable.add(xLabel).size(10f, 50f);
            targetTable.add(targetPosX).size(90f, 50f);
            targetTable.add(yLabel).size(10f, 50f);
            targetTable.add(targetPosY).size(90f, 50f);
            // Add the targetTable to the main table below the ScrollPane
            table.add(targetTable).row();
        }
    }

}
