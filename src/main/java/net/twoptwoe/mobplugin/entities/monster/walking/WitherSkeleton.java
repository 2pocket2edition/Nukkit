package net.twoptwoe.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityWitherSkeleton;
import cn.nukkit.item.ItemSwordStone;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import net.twoptwoe.mobplugin.entities.monster.WalkingMonster;

public class WitherSkeleton extends WalkingMonster {

    public static final int NETWORK_ID = EntityWitherSkeleton.NETWORK_ID;

    public WitherSkeleton(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "WitherSkeleton";
    }

    @Override
    protected void initEntity() {
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 2.4f;
    }

    @Override
    public void attackEntity(Entity player) {
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);

        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.eid = this.getId();
        pk.item = new ItemSwordStone();
        pk.hotbarSlot = 10;
        pk.inventorySlot = 10;
        player.dataPacket(pk);
    }

    @Override
    public int getKillExperience() {
        return 5;
    }
}
