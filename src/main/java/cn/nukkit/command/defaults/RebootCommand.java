package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import net.daporkchop.mcpe.UtilsPE;

public class RebootCommand extends VanillaCommand {

    public RebootCommand(String name) {
        super(name, "Reboots the server", "/reboot", new String[]{});
        this.setPermission("nukkit.command.help");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§c§lYe cannot do this!");
            sender.sendMessage("§c§lScrub.");
            return true;
        }
        UtilsPE.stopNow();
        return true;
    }
}
