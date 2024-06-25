package me.Cai1Hsu.CommandLine.Subcommands;

import me.Cai1Hsu.Game.Client.GameHost;
import me.Cai1Hsu.Game.Server.RemoteServer;

public class Connect implements ISubcommand {

    @Override
    public void Run(String[] args) {
        // Safely assume that the format is correct

        String[] ipPort = args[1].split(":");
        String ip = ipPort[0];

        // safely assume that isValid ensured that the port is a valid number
        int port = Integer.parseInt(ipPort[1]);

        try {
            var remoteServer = new RemoteServer(ip, port);
            new GameHost(remoteServer).runMainLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean isValid(String[] args) {
        if (args.length != 2) {
            System.out.println("[Error] Invalid number of arguments");
            return false;
        }

        if (!args[1].contains(":")) {
            System.out.println("[Error] Invalid format");
            return false;
        }

        try {
            int port = Integer.parseInt(args[1].split(":")[1]);

            return port > 0 && port < 65536;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getFormat() {
        return "<ip>:<port>";
    }

    @Override
    public String getDescription() {
        return "Connect to a server, e.g. 'connect 127.0.0.1:8080'";
    }
}
