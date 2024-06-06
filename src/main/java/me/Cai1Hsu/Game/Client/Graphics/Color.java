package me.Cai1Hsu.Game.Client.Graphics;

public class Color {
    public static final int BLACK = 0x00;

    public int _fColor;
    public int _bColor;

    public Color(int fColor, int bColor) {
        _fColor = fColor;
        _bColor = bColor;
    }

    public int getForeColor() {
        return _fColor;
    }

    public int getBackColor() {
        return _bColor;
    }

    public void setForeColor(int fColor) {
        _fColor = fColor;
    }

    public void setBackColor(int bColor) {
        _bColor = bColor;
    }
}
