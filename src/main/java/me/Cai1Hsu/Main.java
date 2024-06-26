// CopyRight(C) 2024 Cai1Hsu
// Licensed under MIT, you should have received a copy of the license with this code
// Source repo: https://github.com/Cai1Hsu/AnotherSnakeGame

package me.Cai1Hsu;

import me.Cai1Hsu.CommandLine.Parser;

public class Main {
    public static void main(String[] args) {
        if (args.length == 1) {
            args = args[0].split(" ");
        }

        Parser.Parse(args)
                .Run();
    }
}