package me.Cai1Hsu.Game.Server.Packets;

import java.io.Serializable;

public class ServerPacket implements Serializable {
    public ClientCommand command;

    public Serializable data;
    public int code;
}
