package me.Cai1Hsu.CommandLine;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.Cai1Hsu.CommandLine.Subcommands.ISubcommand;
import me.Cai1Hsu.CommandLine.Subcommands.Begin;
import me.Cai1Hsu.CommandLine.Subcommands.Connect;
import me.Cai1Hsu.CommandLine.Subcommands.Help;
import me.Cai1Hsu.CommandLine.Subcommands.Rule;

public class Parser {
    protected static Map<String, ISubcommand> _subcommands = new HashMap<>();

    static {
        _subcommands.put("help", new Help());
        _subcommands.put("rule", new Rule());
        _subcommands.put("connect", new Connect());
        _subcommands.put("begin", new Begin());
    }

    public static Set<Entry<String, ISubcommand>> Subcommands() {
        return _subcommands.entrySet();
    }

    private final String[] _args;

    public static Parser Parse(String[] args) {
        return new Parser(args);
    }

    private Parser(String[] args) {
        this._args = args;
    }

    public void Run() {
        if (_args.length == 0) {
            new Help().Run(_args);
            return;
        }

        ISubcommand cmd = _subcommands.get(_args[0]);

        if (cmd != null && cmd.isValid(_args)) {
            cmd.Run(_args);
        } else {
            System.out.println("");

            if (cmd == null) 
                System.out.println("Unknown subcommand: " + _args[0]);

            new Help().Run(_args);
        }
    }
}
