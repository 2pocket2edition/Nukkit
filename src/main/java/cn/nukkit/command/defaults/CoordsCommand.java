package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.GameRulesChangedPacket;
import cn.nukkit.utils.TextFormat;

public class CoordsCommand extends VanillaCommand {

    public CoordsCommand(String name) {
        super(name, "Show your coordinates", "/coords", new String[]{"togglecoords"});
        this.setPermission("nukkit.command.help");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            GameRulesChangedPacket packet = new GameRulesChangedPacket();
            packet.showCoords = player.showCoords = !player.showCoords;
            packet.gameRules = player.level.gameRules;
            player.dataPacket(packet);
        }
        return true;
    }
}
