package net.twoptwoe.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityCaveSpider;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import net.twoptwoe.mobplugin.MobPlugin;
import net.twoptwoe.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CaveSpider extends Spider {

    public static final int NETWORK_ID = EntityCaveSpider.NETWORK_ID;

    public CaveSpider(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @Override
    public double getSpeed() {
        return 1.3;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setMaxHealth(12);
        this.setDamage(new int[]{0, 2, 3, 3});
    }

    @Override
    public void attackEntity(Entity player) {
        if (this.attackDelay > 10 && this.distanceSquared(player) < 1.32) {
            this.attackDelay = 0;
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
            int strings = Utils.rand(0, 3); // drops 0-2 strings
            int spiderEye = Utils.rand(0, 3) == 0 ? 1 : 0; // with a 1/3 chance it drops spider eye
            for (int i = 0; i < strings; i++) {
                drops.add(Item.get(Item.STRING, 0, 1));
            }
            for (int i = 0; i < spiderEye; i++) {
                drops.add(Item.get(Item.SPIDER_EYE, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }

    @Override
    public int getKillExperience() {
        return 5; // gain 5 experience
    }

}
