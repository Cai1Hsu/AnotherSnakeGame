package me.Cai1Hsu.Game.Client.Graphics;

import java.io.Serializable;

public class DrawableChar implements Serializable {
    private char _character;
    private Color _color;

    public void setCharacter(char character) {
        _character = character;
    }

    public void setColor(Color color) {
        if (_color == null) {
            _color = color.clone();
        } else {
            var of = _color._fColor;
            var ob = _color._bColor;

            _color = color.clone();

            if (_color._bColor < 0)
                _color._bColor = ob;
            
            if (_color._fColor < 0)
                _color._fColor = of;
        }
    }

    public void setBoth(char character, Color color) {
        _character = character;
        setColor(color);
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
