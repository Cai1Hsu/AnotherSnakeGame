package me.Cai1Hsu.Math;

import java.io.Serializable;

public class Vector2D implements Serializable {
    public int _x;
    public int _y;

    public Vector2D(int x, int y) {
        this._x = x;
        this._y = y;
    }

    public Vector2D plus(Vector2D other) {
        this._x += other._x;
        this._y += other._y;

        return this;
    }

    public void rangeNormalize(Vector2D max, boolean allowOverflow, boolean allowUnderflow) {
        if (this._x >= max._x) {
            if (allowOverflow) {
                this._x = 0;
            } else {
                this._x = max._x - 1;
            }
        } else if (this._x < 0) {
            if (allowUnderflow) {
                this._x = max._x - 1;
            } else {
                this._x = 0;
            }
        }

        if (this._y >= max._y) {
            if (allowOverflow) {
                this._y = 0;
            } else {
                this._y = max._y - 1;
            }
        } else if (this._y < 0) {
            if (allowUnderflow) {
                this._y = max._y - 1;
            } else {
                this._y = 0;
            }
        }
    }

    public Vector2D clone() {
        return new Vector2D(this._x, this._y);
    }

    public static Vector2D clone(Vector2D other) {
        return new Vector2D(other._x, other._y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Vector2D) {
            Vector2D other = (Vector2D) obj;
            return this._x == other._x && this._y == other._y;
        }

        return false;
    }
}
