package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.nbt.tag.CompoundTag;
import net.daporkchop.mcpe.MultiMOTD;

import java.util.Arrays;

public class MOTDCommand extends VanillaCommand {
    public MOTDCommand(String name) {
        super(name, "Submit your own MOTD!", "/submit <motd>");
        this.setPermission("nukkit.command.op.give"); //permission that only console has
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

        String msg = String.join(" ", args);
        MultiMOTD.putMOTD(msg);
        sender.sendMessage("Successfully added MOTD: \"" + msg + '"');
        return true;
    }
}
