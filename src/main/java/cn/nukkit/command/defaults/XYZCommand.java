package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

public class XYZCommand extends VanillaCommand {

    public XYZCommand(String name) {
        super(name, "Sends your coordinates", "/xyz", new String[]{});
        this.setPermission("nukkit.command.help");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Position position = (Position) sender;
            sender.sendMessage(TextFormat.colorize("&bx:" + position.x + " y:" + position.y + " z:" + position.z));
        }
        return true;
    }
}
