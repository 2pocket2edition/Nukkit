package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.nbt.tag.CompoundTag;
import net.daporkchop.mcpe.MultiMOTD;

public class MOTDCommand extends VanillaCommand {
    public MOTDCommand(String name) {
        super(name, "Submit your own MOTD!", "/submit <motd>");
        this.setPermission("nukkit.command.help"); //permission that only console has
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("action ...", CommandParameter.ARG_TYPE_RAW_TEXT, false)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("You must give an MOTD to submit!");
            return false;
        }

        if (sender instanceof Player) {
            CompoundTag tag = ((Player) sender).namedTag;
            int currentVotes = tag.getInt("votes");

            if (currentVotes < 10) {
                sender.sendMessage("You must have at least 10 votes to submit an MOTD! Go to http://2p2e.net for vote links.");
                sender.sendMessage("You currently have " + currentVotes + " votes.");
                return false;
            }

            tag.putInt("votes", currentVotes - 10);
        }

        String msg = "";
        for (String arg : args) {
            msg += arg + " ";
        }
        msg = msg.trim();
        MultiMOTD.putMOTD(msg);
        sender.sendMessage("Successfully added MOTD: \"" + msg + "\" and subtracted 10 votes.");
        return true;
    }
}
