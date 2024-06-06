package me.Cai1Hsu.CommandLine.Subcommands;

import me.Cai1Hsu.Game.Client.GameHost;

public class Begin implements ISubcommand {
    @Override
    public void Run(String[] args) {
        new GameHost().runMainLoop();
    }

    @Override
    public Boolean isValid(String[] args) {
        if (args.length != 1) {
            return false;
        }

        return true;
    }

    @Override
    public String getFormat() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Start game with a local server";
    }

}
