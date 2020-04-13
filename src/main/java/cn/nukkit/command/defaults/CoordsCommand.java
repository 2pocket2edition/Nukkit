package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.GameRulesChangedPacket;
import cn.nukkit.utils.TextFormat;

public class CoordsCommand extends VanillaCommand {

    public CoordsCommand(String name) {
        super(name, "Show/hide your coordinates", "/coords", new String[]{"togglecoords"});
        this.setPermission("nukkit.command.help");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean showCoords = !player.namedTag.getBoolean("2p2e_showCoords");
            player.namedTag.putBoolean("2p2e_showCoords", showCoords);
            GameRulesChangedPacket packet = new GameRulesChangedPacket();
            packet.showCoords = showCoords;
            packet.gameRules = player.level.gameRules;
            player.dataPacket(packet);
            player.sendMessage("Coordinates are now " + (showCoords ? "shown." : "hidden."));
        }
        return true;
    }
}
