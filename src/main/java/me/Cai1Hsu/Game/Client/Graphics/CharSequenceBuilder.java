package me.Cai1Hsu.Game.Client.Graphics;

public class CharSequenceBuilder {
    private StringBuilder _builder;

    private int _fColor = -1;
    private int _bColor = -1;

    public CharSequenceBuilder() {
        _builder = new StringBuilder();
    }

    public String build() {
        return _builder.toString();
    }

    private void writeBackground(int bcolor) {
        if (bcolor == _bColor) {
            return;
        }

        _builder.append("\u001B[48;5;");
        _builder.append(bcolor);
        _builder.append("m");

        _bColor = bcolor;
    }

    private void writeForeground(int fcolor) {
        if (fcolor == _fColor) {
            return;
        }

        _builder.append("\u001B[38;5;");
        _builder.append(fcolor);
        _builder.append("m");

        _fColor = fcolor;
    }

    public void write(DrawableChar drawable) {
        var color = drawable.getColor();
        writeBackground(color.getBackColor());
        writeForeground(color.getForeColor());

        write(drawable.getCharacter());
    }

    public void write(char character) {
        _builder.append(character);
    }
}
