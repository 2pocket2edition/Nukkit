package net.twoptwoe.mobplugin.entities.animal.swimming;

import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.passive.EntitySquid;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;
import net.twoptwoe.mobplugin.entities.animal.WalkingAnimal;
import net.twoptwoe.mobplugin.utils.Utils;

public class Squid extends WalkingAnimal {

    public static final int NETWORK_ID = EntitySquid.NETWORK_ID;

    public Squid(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.8f;
    }

    @Override
    public float getHeight() {
        return 0.8f;
    }

    @Override
    public float getEyeHeight() {
        return 0.7f;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setMaxHealth(10);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        return false;
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
        }
        return new Item[]{new ItemDye(DyeColor.BLACK.getDyeData())};
    }

    @Override
    public int getKillExperience() {
        return Utils.rand(1, 4); // gain 1-3 experience
    }

}
