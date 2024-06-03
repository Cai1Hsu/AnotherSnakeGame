package me.Cai1Hsu.CommandLine.Subcommands.Subcommands;

public class Connect extends ISubcommand {

    @Override
    public void Run(String[] args) {
        System.out.println("Connecting to server...");
        System.out.println("Connection established!");
    }

    @Override
    public Boolean isValid(String[] args) {
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
