package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/12/08 by Pub4Game.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class XYZCommand extends VanillaCommand {

    public XYZCommand(String name) {
        super(name, "%nukkit.command.kill.description", "%nukkit.command.kill.usage", new String[]{"suicide"});
        this.setPermission("nukkit.command.kill.self;"
                + "nukkit.command.kill.other");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParameter.ARG_TYPE_TARGET, true)
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
