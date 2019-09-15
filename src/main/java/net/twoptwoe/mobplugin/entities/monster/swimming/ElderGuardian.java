package net.twoptwoe.mobplugin.entities.monster.swimming;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.mob.EntityElderGuardian;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import net.twoptwoe.mobplugin.entities.monster.WalkingMonster;
import net.twoptwoe.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ElderGuardian extends WalkingMonster {

    public static final int NETWORK_ID = EntityElderGuardian.NETWORK_ID;

    public ElderGuardian(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1.9975f;
    }

    @Override
    public float getHeight() {
        return 1.9975f;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setMaxHealth(80);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        return false;
    }

    @Override
    public void attackEntity(Entity player) {
        //TODO
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            int prismarineShard = Utils.rand(0, 3); // drops 0-2 prismarine shard
            for (int i = 0; i < prismarineShard; i++) {
                drops.add(Item.get(Item.PRISMARINE_SHARD, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }

    @Override
    public int getKillExperience() {
        return 10;
    }

}
