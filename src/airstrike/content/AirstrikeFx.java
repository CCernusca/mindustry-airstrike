package airstrike.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.content.Fx;
import mindustry.graphics.Drawf;

import static arc.input.KeyCode.e;

public class AirstrikeFx {

    public static Effect shockwave(float lifeTime,
                                   Color fromColor, Color toColor,
                                   float fromRadius, float toRadius,
                                   float fromThickness, float toThickness) {

        return new Effect(lifeTime, e -> {
            // Color transition
            Draw.color(fromColor, toColor, e.fin());
            // Thickness transition
            Lines.stroke(fromThickness + (e.fin() * (toThickness - fromThickness)));
            // Radius transition
            Lines.circle(e.x, e.y, fromRadius + (e.fin() * (toRadius - fromRadius)));
        });
    }

    public static Effect shockwave(float lifeTime, float toRadius, float fromThickness) {
        return shockwave(lifeTime, Color.white, Color.lightGray, 0f, toRadius, fromThickness, 0);
    }

    public static Effect fireball(float lifetime,
                                  float fromBeamLength, float toBeamLength,
                                  float fromBeamWidth, float toBeamWidth,
                                  Color fromBeamColor, Color toBeamColor,
                                  float totalBeamMovement,
                                  int beamCount,
                                  float fromFireballRadius, float toFireballRadius,
                                  Color fromFireballColor, Color toFireballColor) {

        return new Effect(lifetime, e -> {
            // Save the current draw color
            Color previousColor = Draw.getColor().cpy();

            // Interpolate the fireball color from start to end color, apply fade-out
            Color fireballColor = fromFireballColor.cpy().lerp(toFireballColor, (float) Math.pow(e.fin(), 1.5)).mul(1f, 1f, 1f, (float) Math.pow(e.fout(), 0.1f));

            // Draw the central fireball (circle) with dynamic size and color
            float fireballSize = Mathf.lerp(fromFireballRadius, toFireballRadius, (float) Math.pow(e.fin(), 0.75));
            Draw.color(fireballColor);
            Fill.circle(e.x, e.y, fireballSize);

            // Draw light source
            Drawf.light(e.x, e.y, fireballSize, fireballColor, 1f);

            // Create rotating expanding beams (cones)
            float rotationAngle = totalBeamMovement * e.fin();
            float angleStep = 360f / beamCount;
            for (int i = 0; i < beamCount; i++) {
                float angle = Mathf.degRad * (rotationAngle + angleStep * i);

                // Calculate the current length and width of the beam based on time
                float beamLength = Mathf.lerp(fromBeamLength, toBeamLength, (float) Math.pow(e.fin(), 0.75));
                float halfWidth = Mathf.lerp(fromBeamWidth, toBeamWidth, (float) Math.pow(e.fin(), 0.75)) / 2f;

                // Interpolate the beam color from start to end, apply fade-out
                Color beamColor = fromBeamColor.cpy().lerp(toBeamColor, (float) Math.pow(e.fin(), 1.5)).mul(1f, 1f, 1f, (float) Math.pow(e.fout(), 0.1f));

                // Calculate the direction vectors for the beam's edges
                float cosAngle = Mathf.cos(angle);
                float sinAngle = Mathf.sin(angle);
                float cosPerp = Mathf.cos(angle + Mathf.PI / 2);
                float sinPerp = Mathf.sin(angle + Mathf.PI / 2);

                // Calculate the vertices of the triangle representing the cone
                float x1 = e.x + cosPerp * halfWidth;
                float y1 = e.y + sinPerp * halfWidth;
                float x2 = e.x - cosPerp * halfWidth;
                float y2 = e.y - sinPerp * halfWidth;
                float x3 = e.x + cosAngle * beamLength;
                float y3 = e.y + sinAngle * beamLength;

                // Draw the filled triangle (cone)
                Draw.color(beamColor);
                Fill.tri(x1, y1, x2, y2, x3, y3);
            }

            // Restore the previous draw color
            Draw.color(previousColor);
        });
    }


    public static Effect fireball(float lifeTime, float toBeamLength, float toBeamWidth, float totalBeamMovement, int beamCount, float toFireballRadius) {
        return fireball(lifeTime, 0f, toBeamLength, 0f, toBeamWidth, Color.white, Color.gold, totalBeamMovement, beamCount, 0f, toFireballRadius, Color.yellow, Color.orange);
    }

    public static Effect mushroomCloud(float lifetime,
                                       float fromRadius, float toRadius,
                                       Color fromColor, Color toColor) {

        return new Effect(lifetime, e -> {
            // Save the current draw color
            Color previousColor = Draw.getColor().cpy();

            // Base cloud
            float radius = Mathf.lerp(fromRadius, toRadius, e.fin());
            Color cloudColor = fromColor.cpy().lerp(toColor, e.fin()).mul(1f, 1f, 1f, 0.5f * (float) Math.pow(e.fout(), 0.1f));
            Draw.color(cloudColor);
            Fill.circle(e.x, e.y, radius);

            // Restore the previous draw color
            Draw.color(previousColor);
        });
    }

    public static Effect mushroomCloud(float lifeTime, float toRadius) {
        return mushroomCloud(lifeTime, 0f, toRadius, Color.gray, Color.darkGray);
    }

    public static Effect nukeExplosion(float lifeTime, float damageRadius) {
        return new Effect(lifeTime, e -> {
        }) {
            public void at(float x, float y) {
                super.at(x, y);

                // Shockwave
                shockwave(lifeTime, damageRadius * 2, damageRadius / 5).at(x, y);

                // Explosion
                fireball(lifeTime * 2, damageRadius * 2, damageRadius / 2, 60f, 8, damageRadius).at(x, y);

                // Mushroom cloud
                mushroomCloud(lifeTime * 12, damageRadius).at(x, y);

            }
        };
    }

}
