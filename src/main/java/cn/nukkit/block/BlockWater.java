package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWater extends BlockLiquid {
    public BlockWater() {
        this(0);
    }

    public BlockWater(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WATER;
    }

    @Override
    public String getName() {
        return "Water";
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        boolean ret = this.getLevel().setBlock(this, this, true, false);
        this.getLevel().scheduleUpdate(this, this.tickRate());

        return ret;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WATER_BLOCK_COLOR;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        super.onEntityCollide(entity);
        entity.resetFallDistance();

        if (entity.fireTicks > 0) {
            entity.extinguish();
        }
    }

    @Override
    public int tickRate() {
        return 5;
    }

    @Override
    protected boolean isSelfType(int fullId) {
        return isWater(fullId);
    }

    @Override
    protected void spreadIntoBlock(int selfFullId, int targetFullId, int x, int y, int z, int deltaX, int deltaY, int deltaZ) {
        super.spreadIntoBlock(selfFullId, targetFullId, x, y, z, deltaX, deltaY, deltaZ);
    }

    @Override
    protected boolean doReplace(int targetFullId, int x, int y, int z, int deltaX, int deltaY, int deltaZ) {
        if (isLava(targetFullId))   {
            //lava should harden
            if ((targetFullId & 0xF) == 0)  {
                //source block into obsidian
                this.level.setBlockFullIdAt(x, y, z, OBSIDIAN << 4);
            } else {
                //other lava into cobblestone
                this.level.setBlockFullIdAt(x, y, z, COBBLESTONE << 4);
            }
            this.level.updateAroundFast(x, y, z);
            return true;
        }
        return super.doReplace(targetFullId, x, y, z, deltaX, deltaY, deltaZ);
    }

    @Override
    public int onUpdate(int type) {
        /*if (type == Level.BLOCK_UPDATE_NORMAL)  {
            //check if this block should harden
        }*/
        return super.onUpdate(type);
    }

    @Override
    protected boolean checkForHarden(int x, int y, int z) {
        if (isLava(this.level.getFullBlock(x + 1, y, z)))   {
            ((BlockLiquid) this.level.getBlock(x + 1, y, z)).checkForHarden(x + 1, y, z);
        }
        if (isLava(this.level.getFullBlock(x, y, z + 1)))   {
            ((BlockLiquid) this.level.getBlock(x, y, z + 1)).checkForHarden(x, y, z + 1);
        }
        if (isLava(this.level.getFullBlock(x - 1, y, z)))   {
            ((BlockLiquid) this.level.getBlock(x - 1, y, z)).checkForHarden(x - 1, y, z);
        }
        if (isLava(this.level.getFullBlock(x, y, z - 1)))   {
            ((BlockLiquid) this.level.getBlock(x, y, z - 1)).checkForHarden(x, y, z - 1);
        }
        return super.checkForHarden(x, y, z);
    }
}
