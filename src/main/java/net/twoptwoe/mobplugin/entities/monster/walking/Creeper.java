package net.twoptwoe.mobplugin.entities.monster.walking;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.mob.EntityCreeper;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.ExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import co.aikar.timings.Timings;
import net.twoptwoe.mobplugin.entities.monster.WalkingMonster;
import net.twoptwoe.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Creeper extends WalkingMonster implements EntityExplosive {

    public static final int NETWORK_ID = EntityCreeper.NETWORK_ID;

    public static final int DATA_POWERED = 19;

    private int bombTime = 0;

    private boolean exploded = false;

    public Creeper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.7f;
    }

    @Override
    public double getSpeed() {
        return 0.9;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        if (this.namedTag.getBoolean("powered") || this.namedTag.getBoolean("IsPowered")) {
            this.dataProperties.putBoolean(DATA_POWERED, true);
        }
        setMaxHealth(20);
    }

    public boolean isPowered() {
        return this.getDataPropertyBoolean(DATA_POWERED);
    }

    public void setPowered(boolean powered) {
        this.namedTag.putBoolean("powered", powered);
        this.setDataProperty(new ByteEntityData(DATA_POWERED, powered ? 1 : 0));
    }

    public void setPowered() {
        this.namedTag.putBoolean("powered", true);
        this.setDataProperty(new ByteEntityData(DATA_POWERED, 1));
    }

    public int getBombTime() {
        return this.bombTime;
    }

    @Override
    public void explode() {
        ExplosionPrimeEvent ev = new ExplosionPrimeEvent(this, 2.8);
        this.server.getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            Explosion explosion = new Explosion(this, (float) ev.getForce(), this);
            if (ev.isBlockBreaking()) {
                explosion.explodeA();
            }
            explosion.explodeB();
            this.exploded = true;
        }
        this.close();
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate = false;
        Timings.entityBaseTickTimer.startTiming();

        hasUpdate = super.entityBaseTick(tickDiff);

        Entity target = getTarget();
        if (target != null) {
            if (distanceSquared(target) < 9) { //3 blocks
                bombTime++;
                if (bombTime >= 64) {
                    explode();
                }
            } else {
                bombTime = 0;
            }
        }

        Timings.entityBaseTickTimer.stopTiming();
        return hasUpdate;
    }

    public void attackEntity(Entity player) {

    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (this.exploded && this.isPowered()) {
            // TODO: add creeper head
        }
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            int gunPowder = Utils.rand(0, 3); // drops 0-2 gunpowder
            for (int i = 0; i < gunPowder; i++) {
                drops.add(Item.get(Item.GUNPOWDER, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }

    @Override
    public int getKillExperience() {
        return 5; // gain 5 experience
    }

    public int getMaxFallHeight() {
        return this.followTarget == null ? 3 : 3 + (int) (this.getHealth() - 1.0F); //TODO: change this to attack target only
    }

}
