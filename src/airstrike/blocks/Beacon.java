package airstrike.blocks;

import airstrike.OrbitalData;
import airstrike.items.AirstrikeWeapon;
import airstrike.content.AirstrikeItems;
import airstrike.meta.AirstrikeStat;
import arc.Core;
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
import mindustry.world.meta.StatUnit;

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

    @Override
    public void setStats() {
        super.setStats();
        stats.add(AirstrikeStat.impactDelay, impactDelay, StatUnit.seconds);
    }

    public class BeaconBuild extends Building {

        public void call() {
            if (selected < weapons.size()) {
                AirstrikeWeapon selectedWeapon = weapons.get(selected);
                if (OrbitalData.removeOrbitalWeapon(selectedWeapon)) {
                    deselect();
                    selected = 0; // Reset selection, in case block is not destroyed
                    selectedWeapon.impact(tile, impactDelay);
                } else {
                    // Should be impossible, only weapons in orbit are displayed
                    Log.err("Selected weapon not in orbit");
                }
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();

            // Update the weapons list
            LinkedList<String> orbitalWeapons = OrbitalData.getOrbitalWeapons();
            weapons.clear();
            for (String weaponName : orbitalWeapons) {
                AirstrikeWeapon weapon = AirstrikeItems.getWeapon(weaponName);
                if (weapon == null) {
                    Log.err("Invalid weapon");
                    continue;
                }
                weapons.add(weapon);
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
