package me.Cai1Hsu.CommandLine.Subcommands;

public class Connect extends ISubcommand {

    @Override
    public void Run(String[] args) {
        // Safely assume that the format is correct

        String[] ipPort = args[1].split(":");
        String ip = ipPort[0];
        String port = ipPort[1];

        System.out.printf("Connecting to %s, port: %s\n", ip, port);

        System.out.println("Connection failed: Not implemented yet :P");
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

        return true;
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
