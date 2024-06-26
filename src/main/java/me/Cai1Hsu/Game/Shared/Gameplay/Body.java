package me.Cai1Hsu.Game.Shared.Gameplay;

import java.io.Serializable;

import me.Cai1Hsu.Game.Client.Graphics.Canvas;
import me.Cai1Hsu.Game.Client.Graphics.Color;
import me.Cai1Hsu.Math.Vector2D;

public class Body implements Serializable {
    // Red #ff0000
    public static final Color SELF_HEAD_COLOR = new Color(Canvas.DEFAULT_FCOLOR, 9);
    // Blue #0000ff
    public static final Color ENEMY_HEAD_COLOR = new Color(Canvas.DEFAULT_FCOLOR, 4);
    // Teal #008080
    public static final Color ESSENTIAL_COLOR = new Color(Canvas.DEFAULT_FCOLOR, 6);
    // Grey #808080
    public static final Color BODY_COLOR = new Color(Canvas.DEFAULT_FCOLOR, 8);

    public Vector2D _position;

    public Body(Vector2D pos) {
        _position = pos;
    }
}
