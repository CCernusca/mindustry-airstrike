package airstrike.blocks;

import airstrike.OrbitalData;
import airstrike.items.AirstrikeWeapon;
import arc.Core;
import arc.graphics.Color;
import arc.scene.event.Touchable;
import arc.scene.ui.*;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
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
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            // Create a Label & Background for the title
            Table titleBackground = new Table();
            titleBackground.background(Styles.black6);
            Label titleLabel = new Label(Core.bundle.get("label.select-weapon"), Styles.defaultLabel);
            titleBackground.add(titleLabel);
            table.add(titleBackground).size(200f, 50f).row();

            // Create a table to hold the weapon names
            Table weaponsTable = new Table();
            weaponsTable.center().top();
            weaponsTable.background(Styles.black6);

            // Create a yellow outline style
            Label.LabelStyle yellowOutlineStyle = new Label.LabelStyle(Styles.defaultLabel);
            yellowOutlineStyle.fontColor = Color.yellow; // Set the font color to yellow
            yellowOutlineStyle.font = Styles.defaultLabel.font; // Use the default font

            // Iterate through the weapons and add them to the weaponsTable
            int index = 0;
            for (AirstrikeWeapon weapon : weapons) {
                Label weaponLabel = new Label(Core.bundle.format("label.weapon-name", weapon.localizedName), Styles.defaultLabel);
                weaponLabel.touchable = Touchable.enabled; // Make the label touchable

                // Add a click listener to handle selection
                int finalIndex = index;
                weaponLabel.clicked(() -> {
                    selected = finalIndex;
                    table.clear();
                    buildConfiguration(table); // Refresh the configuration UI after selection
                });

                // Highlight selected
                if (index == selected) {
                    weaponLabel.setStyle(yellowOutlineStyle);
                }

                weaponLabel.setAlignment(Align.center, Align.center);
                weaponsTable.add(weaponLabel).pad(10).row();

                index++;
            }

            // Create a ScrollPane to make the weaponsTable scrollable
            ScrollPane scrollPane = new ScrollPane(weaponsTable);
            scrollPane.setFadeScrollBars(false); // Disable fade effect on scrollbars
            // Add the ScrollPane to the main table
            table.add(scrollPane).size(200f, 150f).row();

            // Create the target selection table
            Table targetTable = new Table();
            targetTable.background(Styles.black6);
            // Inputs
            TextField targetPosX = new TextField(String.valueOf(target.x), Styles.defaultField);
            TextField targetPosY = new TextField(String.valueOf(target.y), Styles.defaultField);
            // Labels
            Label xLabel = new Label(Core.bundle.get("label.target-x"), Styles.defaultLabel);
            Label yLabel = new Label(Core.bundle.get("label.target-y"), Styles.defaultLabel);
            xLabel.setAlignment(Align.center);
            yLabel.setAlignment(Align.center);
            // Filter input to digits only
            targetPosX.setFilter(TextField.TextFieldFilter.digitsOnly);
            targetPosY.setFilter(TextField.TextFieldFilter.digitsOnly);
            // Update listeners to change target position
            targetPosX.changed(() -> {
                if (!targetPosX.getText().isEmpty()) {
                    target = Vars.world.tile(Integer.parseInt(targetPosX.getText()), target.y);
                }
            });
            targetPosY.changed(() -> {
                if (!targetPosY.getText().isEmpty()) {
                    target = Vars.world.tile(target.x, Integer.parseInt(targetPosY.getText()));
                }
            });
            // Add components to the targetTable
            targetTable.add(xLabel).size(20f, 50f);
            targetTable.add(targetPosX).size(80f, 50f);
            targetTable.add(yLabel).size(20f, 50f);
            targetTable.add(targetPosY).size(80f, 50f);
            // Add to main table
            table.add(targetTable).row();

            // Create a Table to hold the "call" button
            Table buttonTable = new Table();
            // Create the "call" button
            Button closeButton = new TextButton(Core.bundle.get("button.call-strike"), Styles.defaultt);
            // Deselects the block and closes the configuration UI
            closeButton.clicked(this::call);
            // Add the "call" button to the buttonTable
            buttonTable.add(closeButton).size(200f, 50f);
            // Add the buttonTable to the main table below the ScrollPane
            table.add(buttonTable).row();

        }

    }

}
