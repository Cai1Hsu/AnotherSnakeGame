package me.Cai1Hsu.Game.Shared.Gameplay;

import java.io.Serializable;

import me.Cai1Hsu.Game.Client.Graphics.Canvas;
import me.Cai1Hsu.Game.Client.Graphics.Color;
import me.Cai1Hsu.Math.Vector2D;

public class Food implements Serializable {
    // Lime #00ff00
    public static final Color SMALL_FOOD_COLOR = new Color(Canvas.DEFAULT_FCOLOR, 10);
    // Yellow #ffff00
    public static final Color MEDIUM_FOOD_COLOR = new Color(Canvas.DEFAULT_FCOLOR, 11);

    // Olive #808000
    public static final Color POISON_FOOD_COLOR = new Color(Canvas.DEFAULT_FCOLOR, 3);

    private FoodType _type;
    private Vector2D _position;

    public FoodType getType() {
        return _type;
    }

    public Vector2D getPosition() {
        return _position;
    }

    public boolean testCollision(Vector2D position) {
        return _position.equals(position);
    }

    public Food(FoodType type, Vector2D position) {
        this._type = type;
        this._position = position;
    }
}