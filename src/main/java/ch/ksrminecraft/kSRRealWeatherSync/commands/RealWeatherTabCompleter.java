package ch.ksrminecraft.kSRRealWeatherSync.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RealWeatherTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "status",
            "reload",
            "force",
            "current"
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        // Only first argument is autocompleted
        if (args.length == 1) {

            String current = args[0].toLowerCase();
            List<String> result = new ArrayList<>();

            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(current)) {
                    result.add(sub);
                }
            }

            return result;
        }

        return null; // Keine weiteren Vorschläge
    }
}
