package me.Cai1Hsu.Game.Server.Packets;

import java.io.Serializable;

public enum ClientCommand implements Serializable {
    MOVE_UP,
    MOVE_DOWN,
    MOVE_LEFT,
    MOVE_RIGHT,
    JOIN,
    REQUEST_FULL_STATE,
    NONE
}
