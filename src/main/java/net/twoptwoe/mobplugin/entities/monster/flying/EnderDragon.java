package net.twoptwoe.mobplugin.entities.monster.flying;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityEnderDragon;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import net.twoptwoe.mobplugin.entities.monster.FlyingMonster;

public class EnderDragon extends FlyingMonster {

    public static final int NETWORK_ID = EntityEnderDragon.NETWORK_ID;

    public EnderDragon(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 13f;
    }

    @Override
    public float getHeight() {
        return 4f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(200);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.DRAGON_EGG), Item.get(410)};
    }

    @Override
    public int getKillExperience() {
        return 500;
    }

    @Override
    public void attackEntity(Entity player) {
        //todo
    }

}
