package net.twoptwoe.mobplugin.entities.monster.walking;

import cn.nukkit.entity.mob.EntityZombieVillager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class ZombieVillager extends Zombie {

    public static final int NETWORK_ID = EntityZombieVillager.NETWORK_ID;

    public ZombieVillager(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }
}
