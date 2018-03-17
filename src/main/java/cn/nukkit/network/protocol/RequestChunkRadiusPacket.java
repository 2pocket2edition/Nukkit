package cn.nukkit.network.protocol;

import cn.nukkit.Player;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RequestChunkRadiusPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET;

    public int radius;

    @Override
    public void decode() {
        this.radius = this.getVarInt();
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void handle(Player player) {
        player.server.logger.debug("Request chunk radius packet");
        player.setViewDistance(Math.max(3, Math.min(this.radius, player.viewDistance)));

        if (!player.spawned)    {
            player.requestedChunks = true;
            player.orderChunks();
        }
    }
}
