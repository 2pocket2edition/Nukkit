package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
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
}
