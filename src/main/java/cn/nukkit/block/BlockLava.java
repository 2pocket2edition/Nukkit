package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.event.entity.EntityCombustByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockLava extends BlockLiquid {

    public BlockLava() {
        this(0);
    }

    public BlockLava(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LAVA;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public String getName() {
        return "Lava";
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.highestPosition -= (entity.highestPosition - entity.y) * 0.5;

        // Always setting the duration to 15 seconds? TODO
        EntityCombustByBlockEvent ev = new EntityCombustByBlockEvent(this, entity, 15);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (!ev.isCancelled()
                // Making sure the entity is actually alive and not invulnerable.
                && entity.isAlive()
                && entity.noDamageTicks == 0) {
            entity.setOnFire(ev.getDuration());
        }

        if (!entity.hasEffect(Effect.FIRE_RESISTANCE)) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.LAVA, 4));
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        boolean ret = this.getLevel().setBlock(this, this, true, false);
        this.getLevel().scheduleUpdate(this, this.tickRate());

        return ret;
    }

    protected boolean isSurroundingBlockFlammable(Block block) {
        for (BlockFace face : BlockFace.values()) {
            if (block.getSide(face).getBurnChance() > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LAVA_BLOCK_COLOR;
    }

    @Override
    public int tickRate() {
        return this.level.getDimension() == Level.DIMENSION_NETHER ? 10 : 30;
    }

    @Override
    public int getFlowDecayPerBlock() {
        return this.level.getDimension() == Level.DIMENSION_NETHER ? 1 : 2;
    }

    @Override
    public void addVelocityToEntity(Entity entity, Vector3 vector) {
        if (!(entity instanceof EntityPrimedTNT)) {
            super.addVelocityToEntity(entity, vector);
        }
    }

    @Override
    protected boolean isSelfType(int fullId) {
        return isLava(fullId);
    }

    @Override
    protected void spreadIntoBlock(int selfFullId, int targetFullId, int x, int y, int z, int deltaX, int deltaY, int deltaZ) {
        super.spreadIntoBlock(selfFullId, targetFullId, x, y, z, deltaX, deltaY, deltaZ);
    }

    @Override
    protected boolean doReplace(int targetFullId, int x, int y, int z, int deltaX, int deltaY, int deltaZ) {
        if (isWater(targetFullId))  {
            if (deltaY < 0) {
                //lava flowing downwards into water makes smooth stone
                this.level.setBlockFullIdAt(x, y, z, STONE << 4);
                this.triggerLavaMixEffects(x, y, z);
                this.level.updateAroundFast(x, y, z);
            } else {
                //lava flowing into water from any other direction makes cobblestone
                this.level.setBlockFullIdAt(x - deltaX, y, z - deltaZ, COBBLESTONE << 4);
                this.triggerLavaMixEffects(x - deltaX, y, z - deltaZ);
            }
            return true;
        } else {
            //no need to break the block, because even if it makes drops they'll be burned up instantly
            //the block will be set to lava afterwards by spreadIntoBlock
            return false;
        }
    }

    @Override
    public int onUpdate(int type) {
        return super.onUpdate(type);
    }

    @Override
    protected boolean checkForHarden(int x, int y, int z) {
        if (isWater(this.level.getFullBlock(x + 1, y, z))
                || isWater(this.level.getFullBlock(x, y, z + 1))
                || isWater(this.level.getFullBlock(x - 1, y, z))
                || isWater(this.level.getFullBlock(x, y, z - 1)))   {
            if (this.getDamage() == 0)  {
                //this is a source block, turn into obsidian
                this.level.setBlockFullIdAt(x, y, z, OBSIDIAN << 4);
            } else {
                //everything else should turn into cobblestone
                this.level.setBlockFullIdAt(x, y, z, COBBLESTONE << 4);
            }
            this.level.updateAroundFast(x, y, z);
            return true;
        }
        return false;
    }
}
