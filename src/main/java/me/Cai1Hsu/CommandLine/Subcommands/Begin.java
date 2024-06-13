package me.Cai1Hsu.CommandLine.Subcommands;

import java.io.IOException;

import me.Cai1Hsu.Game.Client.GameHost;
import me.Cai1Hsu.Game.Server.IServer;
import me.Cai1Hsu.Game.Server.LocalServer;

public class Begin implements ISubcommand {
    @Override
    public void Run(String[] args) {
        try {
            int port = 8080;

            if (args.length == 2) {
                try {
                    // isValid should ensure that this is a valid number
                    port = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                }
            }

            IServer localServer = new LocalServer(port);
            new GameHost(localServer).runMainLoop();
        } catch (IOException e) {
        }
    }

    @Override
    public Boolean isValid(String[] args) {
        if (args.length == 1)
            return true;

        if (args.length == 2) {
            try {
                int port = Integer.parseInt(args[1]);

                return port > 0 && port < 65536;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
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
