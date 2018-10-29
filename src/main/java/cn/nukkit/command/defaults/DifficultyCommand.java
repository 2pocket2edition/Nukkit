package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.SetDifficultyPacket;

import java.util.ArrayList;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class DifficultyCommand extends VanillaCommand {

    public DifficultyCommand(String name) {
        super(name, "%nukkit.command.difficulty.description", "%commands.difficulty.usage");
        this.setPermission("nukkit.command.difficulty");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("difficulty", CommandParameter.ARG_TYPE_INT, false)
        });
        this.commandParameters.put("byString", new CommandParameter[]{
                new CommandParameter("difficulty", new String[]{"peaceful", "p", "easy", "e",
                        "normal", "n", "hard", "h"})
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        } else {
            if (sender.isPlayer())  {
                Player player = (Player) sender;
                //debug: test tnt optimization
                //note to self: remove this before commit to github
                for (int x = -3; x < 3; x++)    {
                    for (int y = -5; y < 0; y++)    {
                        for (int z = -3; z < 3; z++)    {
                            player.level.setBlockFullIdAt(player.getFloorX() + x, player.getFloorY() + y, player.getFloorZ() + z, Block.TNT << 4);
                        }
                    }
                }
                return true;
            }
        }

        if (args.length != 1) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        int difficulty = Server.getDifficultyFromString(args[0]);

        if (sender.getServer().isHardcore()) {
            difficulty = 3;
        }

        if (difficulty != -1) {
            sender.getServer().setPropertyInt("difficulty", difficulty);

            SetDifficultyPacket pk = new SetDifficultyPacket();
            pk.difficulty = sender.getServer().getDifficulty();
            Server.broadcastPacket(new ArrayList<>(sender.getServer().getOnlinePlayers().values()), pk);

            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.difficulty.success", String.valueOf(difficulty)));
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return false;
        }

        return true;
    }
}
