package net.twoptwoe.mobplugin.entities.animal.walking;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;
import net.twoptwoe.mobplugin.entities.animal.WalkingAnimal;
import net.twoptwoe.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Sheep extends WalkingAnimal {

    public static final int NETWORK_ID = 13;

    public static int randomColor() {
        return randomColor(ThreadLocalRandom.current());
    }

    public static int randomColor(Random random) {
        int rand = random.nextInt(100) + 1;

        if (rand <= 15) {
            return random.nextBoolean() ? DyeColor.BLACK.getDyeData() : random.nextBoolean() ? DyeColor.GRAY.getDyeData() : DyeColor.LIGHT_GRAY.getDyeData();
        }

        return DyeColor.WHITE.getDyeData();
    }

    public boolean sheared = false;
    public int color = 0;

    public Sheep(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public Sheep(FullChunk chunk, CompoundTag nbt, int color) {
        super(chunk, nbt.putInt("Color", color));
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.65f;
        }
        return 1.3f;
    }

    @Override
    public float getEyeHeight() {
        if (isBaby()) {
            return 0.65f;
        }
        return 1.1f;
    }

    @Override
    public boolean isBaby() {
        return this.getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY);
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(8);

        if (!this.namedTag.contains("Color")) {
            this.setColor(randomColor());
        } else {
            this.setColor(this.namedTag.getByte("Color"));
        }

        if (!this.namedTag.contains("Sheared")) {
            this.namedTag.putByte("Sheared", 0);
        } else {
            this.sheared = this.namedTag.getBoolean("Sheared");
        }

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, this.sheared);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putByte("Color", this.color);
        this.namedTag.putBoolean("Sheared", this.sheared);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            return player.spawned && player.isAlive() && !player.closed && player.getInventory().getItemInHand().getId() == Item.WHEAT && distance <= 49;
        }
        return false;
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            drops.add(Item.get(Item.WOOL, 0, 1)); // each time drops 1 wool
            int muttonDrop = Utils.rand(1, 3); // drops 1-2 muttons / cooked muttons
            for (int i = 0; i < muttonDrop; i++) {
                drops.add(Item.get(this.isOnFire() ? Item.COOKED_MUTTON : Item.RAW_MUTTON, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        if (item.getId() == Item.DYE) {
            this.setColor(((ItemDye) item).getDyeColor().getWoolData());
            return true;
        }

        return item.getId() == Item.SHEARS && this.shear();
    }

    public boolean shear() {
        if (this.sheared) {
            return false;
        }

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, this.sheared = true);

        this.level.dropItem(this, Item.get(Item.WOOL, getColor(), ThreadLocalRandom.current().nextInt(2) + 1));
        return true;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        //if this sheep has been sheared, is standing on grass, make it regrow its wool
        //1000 ticks is 50 seconds (so on average it'll take that long for wool to regrow)
        if (this.sheared && ThreadLocalRandom.current().nextInt(1000) == 0 && this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() - 1, this.getFloorZ()) == BlockID.GRASS) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, this.sheared = false);
            this.level.setBlockIdAt(this.getFloorX(), this.getFloorY() - 1, this.getFloorZ(), BlockID.DIRT);
        }
        return super.onUpdate(currentTick);
    }

    public int getColor() {
        return namedTag.getByte("Color");
    }

    public void setColor(int color) {
        this.color = color;
        this.setDataProperty(new ByteEntityData(DATA_COLOUR, color));
    }

    @Override
    public int getKillExperience() {
        return Utils.rand(1, 4); // gain 1-3 experience
    }

}
