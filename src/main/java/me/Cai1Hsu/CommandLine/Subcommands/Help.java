package me.Cai1Hsu.CommandLine.Subcommands;

import me.Cai1Hsu.CommandLine.Parser;

public class Help implements ISubcommand {

    @Override
    public void Run(String[] args) {
        System.out.println("Another Snake Game - by Cai1Hsu");
        System.out.println("Repo: https://github.com/Cai1Hsu/AnotherSnakeGame");
        System.out.println("===============================");

        System.out.println("Usage: java -jar AnotherSnakeGame.jar [subcommand] [args]");

        System.out.println("Subcommands:");

        for (var entry : Parser.Subcommands()) {
            String format = entry.getValue().getFormat();

            if (format != "") {
                format = " " + format;
            }

            String name = entry.getKey();
            String description = entry.getValue().getDescription();

            System.out.printf("%s%s\n", name, format);
            System.out.printf("\t%s\n", description);

            System.out.println();
        }
    }

    @Override
    public Boolean isValid(String[] args) {
        return true;
    }

    @Override
    public String getFormat() {
        // No args required
        return "";
    }

    @Override
    public String getDescription() {
        return "Prints this help message";
    }
}
