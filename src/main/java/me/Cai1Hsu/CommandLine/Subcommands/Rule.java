package me.Cai1Hsu.CommandLine.Subcommands;

public class Rule extends ISubcommand {

    @Override
    public void Run(String[] _args) {
        System.out.println("Another Snake Game");
        System.out.println("by Cai1Hsu");
        System.out.println("==================");

        System.out.println("Rules:");
        System.out.println("1. Move your player with the arrow keys");
        System.out.println("2. Press 'Shift' for slow mode");
        System.out.println(
                "As a snake, you must eat the food to grow. If you hit the wall or yourself, you lose. Good luck!");

        // TODO: This is not a complete list of rules
    }

    @Override
    public Boolean isValid(String[] _args) {
        return true;
    }

    @Override
    public String getFormat() {
        // No args required
        return "";
    }

    @Override
    public String getDescription() {
        return "Prints the rules of the game";
    }

}
