package airstrike.blocks;

import airstrike.AirstrikeMod;
import airstrike.airstrikeweapons.AirstrikeWeapon;
import airstrike.content.AirstrikeWeapons;
import arc.graphics.Color;
import arc.scene.event.Touchable;
import arc.scene.ui.Button;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Log;
import mindustry.gen.Building;
import mindustry.ui.Styles;
import mindustry.world.Block;

import java.util.HashMap;
import java.util.LinkedList;

public class Beacon extends Block {
    public int selected;
    public LinkedList<AirstrikeWeapon> weapons;
    public float impactDelay;  // Time from call to impact in seconds

    public Beacon(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = false;
        configurable = true;
        buildType = BeaconBuild::new;
        weapons = new LinkedList<>();
        selected = 0;
    }

    public class BeaconBuild extends Building {

        public void call() {
            if (selected < weapons.size()) {
                AirstrikeWeapon selectedWeapon = weapons.get(selected);
                if (AirstrikeMod.removeWeapon(selectedWeapon, 1)) {
                    deselect();
                    selected = 0; // Reset selection, in case block is not destroyed
                    selectedWeapon.impact(tile, impactDelay);
                } else {
                    // Should be impossible, only weapons in orbit are displayed
                    Log.err("Selected weapon not in orbit");
                }
            } else {
                Log.err("Invalid selection");
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();

            HashMap<String, Integer> satellites = AirstrikeMod.getSatallites();
            weapons.clear();
            for (String weaponId : satellites.keySet()) {
                int weaponAmount = satellites.get(weaponId);
                AirstrikeWeapon weapon = AirstrikeWeapons.get(weaponId);
                if (weapon == null) {
                    Log.err("Invalid weapon");
                    continue;
                }
                for (int i = 0; i < weaponAmount; i++) {
                    weapons.add(weapon);
                }
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            // Create a Label & Background for the title
            Table titleBackground = new Table();
            titleBackground.background(Styles.black6);
            Label titleLabel = new Label("Select Weapon", Styles.defaultLabel);
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

            // Retrieve the available airstrike weapons
            HashMap<String, Integer> satellites = AirstrikeMod.getSatallites();
            // Iterate through the weapons and add them to the weaponsTable
            int index = 0;
            for (String weaponId : satellites.keySet()) {
                int weaponAmount = satellites.get(weaponId);
                AirstrikeWeapon weapon = AirstrikeWeapons.get(weaponId);
                if (weapon == null) {
                    Log.err("Invalid weapon");
                    continue;
                }
                String weaponName = weapon.name;
                for (int i = 0; i < weaponAmount; i++) {
                    Label weaponLabel = new Label(weaponName, Styles.defaultLabel);
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

                    index += 1;
                }
            }

            // Create a ScrollPane to make the weaponsTable scrollable
            ScrollPane scrollPane = new ScrollPane(weaponsTable);
            scrollPane.setFadeScrollBars(false); // Disable fade effect on scrollbars
            // Add the ScrollPane to the main table
            table.add(scrollPane).size(200f, 150f).row();

            // Create a Table to hold the "Close" button
            Table buttonTable = new Table();
            // Create the "Close" button
            Button closeButton = new TextButton("Call", Styles.defaultt);
            // Deselects the block and closes the configuration UI
            closeButton.clicked(this::call);
            // Add the "Close" button to the buttonTable
            buttonTable.add(closeButton).size(200f, 50f);
            // Add the buttonTable to the main table below the ScrollPane
            table.add(buttonTable).row();

        }
    }

}
