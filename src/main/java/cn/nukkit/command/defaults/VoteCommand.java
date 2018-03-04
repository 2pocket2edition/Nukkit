package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.nbt.tag.CompoundTag;

public class VoteCommand extends VanillaCommand {
    public VoteCommand(String name) {
        super(name, "Adds a vote to a user's vote count", "/vote user");
        this.setPermission("nukkit.command.give"); //permission that only console has
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        Player player = sender.getServer().getPlayer(args[0]);
        CompoundTag tag;
        if (player == null) {
            tag = sender.getServer().getOfflinePlayerData(args[0]);
        } else {
            tag = player.namedTag;
        }
        int currentVotes = tag.getInt("votes");
        tag.putInt("votes", currentVotes + 1);
        if (player == null) {
            sender.getServer().saveOfflinePlayerData(args[0], tag);
        }
        return true;
    }
}
