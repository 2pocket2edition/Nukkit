package net.twoptwoe.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityEnderman;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import net.daporkchop.mcpe.RandomSpawn;
import net.twoptwoe.mobplugin.MobPlugin;
import net.twoptwoe.mobplugin.entities.monster.WalkingMonster;
import net.twoptwoe.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Enderman extends WalkingMonster {

    public static final int NETWORK_ID = EntityEnderman.NETWORK_ID;

    public Enderman(FullChunk chunk, CompoundTag nbt) {
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
        return 2.9f;
    }

    @Override
    public double getSpeed() {
        return 1.21;
    }

    protected void initEntity() {
        this.setMaxHealth(40);
        super.initEntity();

        this.setDamage(new int[]{0, 4, 7, 10});
    }

    public void attackEntity(Entity player) {
        if (this.attackDelay > 10 && this.distanceSquared(player) < 1) {
            this.attackDelay = 0;

            teleport(1, player.clone().add(0, 1000, 0));

            HashMap<EntityDamageEvent.DamageModifier, Float> damage = new HashMap<>();
            damage.put(EntityDamageEvent.DamageModifier.BASE, (float) this.getDamage());

            if (player instanceof Player) {
                float points = 0;
                for (Item i : ((Player) player).getInventory().getArmorContents()) {
                    points += MobPlugin.armorValues.get(i.getId());
                }

                damage.put(EntityDamageEvent.DamageModifier.ARMOR,
                        (float) (damage.getOrDefault(EntityDamageEvent.DamageModifier.ARMOR, 0f) - Math.floor(damage.getOrDefault(EntityDamageEvent.DamageModifier.BASE, 1f) * points * 0.04)));
            }
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
        }
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            int enderPearls = Utils.rand(0, 2); // drops 0-1 enderpearls
            for (int i = 0; i < enderPearls; i++) {
                drops.add(Item.get(Item.ENDER_PEARL, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }

    @Override
    public int getKillExperience() {
        return 5; // gain 5 experience
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        boolean success = super.attack(source);
        teleport(20, this.clone().add(0, 1000, 0));
        return success;
    }

    public boolean teleport(int radius, Position ok) {
        Position pos = RandomSpawn.getSpawnPos(level, ok, radius, 256);
        if (pos.y > 1000) {
            return false;
        }
        this.teleport(pos);
        return true;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        if (Utils.rand(0, 700) == 0) {
            teleport(20, this.clone().add(0, 1000, 0));
        }

        return super.entityBaseTick(tickDiff);
    }
}
