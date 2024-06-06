package me.Cai1Hsu.Game.Client.Graphics;

public class DrawableChar {
    private char _character;
    private Color _color;

    public void setCharacter(char character) {
        _character = character;
    }

    public void setColor(Color color) {
        _color = color;
    }

    public void setBoth(char character, Color color) {
        _character = character;
        _color = color;
    }

    public DrawableChar(char character, Color color) {
        _character = character;
        _color = color;
    }

    public char getCharacter() {
        return _character;
    }

    public Color getColor() {
        return _color;
    }
}
