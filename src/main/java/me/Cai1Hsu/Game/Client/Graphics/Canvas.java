package me.Cai1Hsu.Game.Client.Graphics;

import java.io.Serializable;

import me.Cai1Hsu.Math.Vector2D;

public class Canvas implements Serializable {
    public static final char EMPTY = ' ';
    public static final int DEFAULT_FCOLOR = 0x0F;
    public static final int DEFAULT_BCOLOR = 0x00;
    public static final Color DEFAULT_COLOR = new Color(DEFAULT_FCOLOR, DEFAULT_BCOLOR);

    public static final Color DEFAULT_TEXT_COLOR = new Color(DEFAULT_FCOLOR, -1);

    private Vector2D _size;
    private DrawableChar[][] _buffer;

    private Color _clearColor = DEFAULT_COLOR.clone();

    public Canvas(Vector2D size) {
        _size = size;
        _buffer = new DrawableChar[size._y][size._x];

        for (int y = 0; y < size._y; y++) {
            for (int x = 0; x < size._x; x++) {
                _buffer[y][x] = new DrawableChar(EMPTY, _clearColor);
            }
        }
    }

    public void setClearColor(Color color) {
        _clearColor = color.clone();
    }

    public void clearScreen() {
        for (int y = 0; y < _size._y; y++) {
            for (int x = 0; x < _size._x; x++) {
                _buffer[y][x].setBoth(EMPTY, _clearColor);
            }
        }
    }

    // Return that if the string was cut off
    public boolean drawText(Vector2D pos, String text, Color color) {
        pos.rangeNormalize(_size, false, true);

        for (int i = 0; i < text.length(); i++) {

            // Cut off the text if it goes out of the canvas
            if (pos._x + i >= _size._x) {
                return false;
            }

            _buffer[pos._y][pos._x + i].setBoth(text.charAt(i), color);
        }

        return true;
    }

    public boolean drawText(int x, int y, String text, Color color) {
        return drawText(new Vector2D(x, y), text, color);
    }

    public boolean drawText(Vector2D pos, String text) {
        return drawText(pos, text, DEFAULT_TEXT_COLOR);
    }

    public boolean drawText(int x, int y, String text) {
        return drawText(new Vector2D(x, y), text, DEFAULT_TEXT_COLOR);
    }

    public boolean drawTextCentered(int y, String text) {
        return drawTextCentered(y, text, DEFAULT_TEXT_COLOR);
    }

    public boolean drawTextCentered(int y, String text, Color color) {
        var len = text.length();
        var clipped = false;

        int x = (_size._x - len) / 2;

        if (x < 0) {
            clipped = true;

            var startIdx = (len - _size._x) / 2;
            len = _size._x;
            text = text.substring(startIdx, startIdx + len);

            x = 0;
        }

        assert x >= 0;

        return drawText(x, y, text, color) || clipped;
    }

    public boolean drawChar(Vector2D pos, char c, Color color) {
        pos.rangeNormalize(_size, false, true);

        if (pos._x >= _size._x || pos._y >= _size._y)
            return false;

        _buffer[pos._y][pos._x].setBoth(c, color);

        return true;
    }

    public boolean drawChar(int x, int y, char c, Color color) {
        return drawChar(new Vector2D(x, y), c, color);
    }

    public boolean drawChar(Vector2D pos, char c) {
        return drawChar(pos, c, DEFAULT_TEXT_COLOR);
    }

    public boolean drawChar(int x, int y, char c) {
        return drawChar(new Vector2D(x, y), c, DEFAULT_TEXT_COLOR);
    }

    // Square is two continuous characters
    // It is usually used to draw a section of the snake body
    public boolean drawSquare(Vector2D pos, Color color) {
        if (pos._x >= _size._x || pos._y >= _size._y)
            return false;

        if (pos._x < 0 || pos._y < 0)
            return false;

        _buffer[pos._y][pos._x].setBoth(' ', color);

        // We've checked the y-axis, so we don't need to check it again
        if (pos._x + 1 >= _size._x/* || pos._y >= _size._y */)
            return false;

        _buffer[pos._y][pos._x + 1].setBoth(' ', color);

        return true;
    }

    public void fillRow(int row, int len, char c, Color color) {
        if (row < 0) {
            row = (row % _size._y) + _size._y;
        }

        for (int x = 0; x < len; x++) {
            if (x >= _size._x)
                break;

            _buffer[row][x].setBoth(c, color);
        }
    }

    public void fillRow(int row, char c, Color color) {
        fillRow(row, _size._x, c, color);
    }

    public void fillColumn(int column, int len, char c, Color color) {
        if (column < 0) {
            column = (column % _size._x) + _size._x;
        }

        for (int y = 0; y < len; y++) {
            if (y >= _size._y)
                break;

            _buffer[y][column].setBoth(c, color);
        }
    }

    public void fillColumn(int col, char c, Color color) {
        fillColumn(col, _size._y, c, color);
    }

    public void drawBlock(Vector2D topLeft, Vector2D size, char c, Color color) {
        for (int y = 0; y < size._y; y++) {
            for (int x = 0; x < size._x; x++) {
                Vector2D pos = new Vector2D(topLeft._x + x, topLeft._y + y);

                if (pos._x >= _size._x || pos._y >= _size._y) {
                    continue;
                }

                _buffer[pos._y][pos._x].setBoth(c, color);
            }
        }
    }

    public String Render() {
        CharSequenceBuilder builder = new CharSequenceBuilder();

        for (int y = 0; y < _size._y; y++) {
            for (int x = 0; x < _size._x; x++) {
                builder.write(_buffer[y][x]);
            }
 
            // Do not make the cursor go to the next line if it is the last line
            if (y != _size._y - 1)
                builder.write('\n');
        }

        return builder.build();
    }
}
