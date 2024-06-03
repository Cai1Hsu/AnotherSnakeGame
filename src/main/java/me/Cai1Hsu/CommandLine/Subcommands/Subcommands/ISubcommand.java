package me.Cai1Hsu.CommandLine.Subcommands.Subcommands;

public abstract class ISubcommand {
    public abstract void Run(String[] args);

    public abstract Boolean isValid(String[] args);

    public abstract String getFormat();

    public abstract String getDescription();
}