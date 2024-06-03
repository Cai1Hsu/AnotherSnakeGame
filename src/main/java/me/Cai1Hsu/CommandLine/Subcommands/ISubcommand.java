package me.Cai1Hsu.CommandLine.Subcommands;

public interface ISubcommand {
    void Run(String[] args);

    Boolean isValid(String[] args);

    String getFormat();

    String getDescription();
}