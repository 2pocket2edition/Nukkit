package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

public class ReplyCommand extends VanillaCommand {

    public ReplyCommand(String name) {
        super(name, "Reply to the last DM", "/reply <msg>", new String[]{"r"});
        this.setPermission("nukkit.command.tell");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("message")
        });
    }

    @Override
    public boolean execute(CommandSender senderC, String commandLabel, String[] args) {
        if (args.length < 1) {
            senderC.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return false;
        }

        Player sender = senderC instanceof Player ? (Player) senderC : null;
        if (sender != null) {
            Player target = sender.getServer().getPlayer(sender.lastDM);
            if (target == null) {
                sender.sendMessage(TextFormat.colorize("&cThat player is not online"));
            } else {
                String msg = "";
                for (int i = 0; i < args.length; i++) {
                    msg += args[i] + " ";
                }
                if (msg.length() > 0) {
                    msg = msg.substring(0, msg.length() - 1);
                }
                sender.sendMessage(TextFormat.colorize("&5[&dme &5-> &d" + target.getName() + "&5] &d") + msg);
                target.sendMessage(TextFormat.colorize("&5[&d" + sender.getName() + " &5-> &dme&5] &d") + msg);
                target.lastDM = sender.getName();
            }
        }

        return true;
    }
}
