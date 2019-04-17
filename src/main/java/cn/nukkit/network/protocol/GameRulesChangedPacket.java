package cn.nukkit.network.protocol;

import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class GameRulesChangedPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.GAME_RULES_CHANGED_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public GameRules gameRules;
    public Boolean showCoords = null;

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        putGameRules(gameRules, showCoords == null ? gameRules.getBoolean(GameRule.SHOW_COORDINATES) : showCoords);
    }
}
