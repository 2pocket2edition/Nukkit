package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockLiquid extends BlockTransparentMeta {
    public int adjacentSources = 0;
    private Vector3 flowVector;

    protected BlockLiquid(int meta) {
        super(meta);
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public double getMaxY() {
        int d = this.getDamage();
        return this.y + 1 - ((d >= 8 ? 0 : d) + 1.0d) / 9.0d;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    public final float getFluidHeightPercent() {
        int d = this.getDamage();
        return ((d >= 8 ? 0 : d) + 1.0f) / 9.0f;
    }

    public Vector3 getFlowVector() {
        if (this.flowVector != null) {
            return this.flowVector;
        }
        Vector3 vector = new Vector3(0, 0, 0);
        return this.flowVector = vector.normalize();
    }

    @Override
    public void addVelocityToEntity(Entity entity, Vector3 vector) {
        if (entity.canBeMovedByCurrents()) {
            Vector3 flow = this.getFlowVector();
            vector.x += flow.x;
            vector.y += flow.y;
            vector.z += flow.z;
        }
    }

    public int getFlowDecayPerBlock() {
        return 1;
    }

    protected void checkForHarden() {
    }

    protected abstract boolean canSpreadInto(int fullId);

    protected void spreadIntoBlock(int selfFullId, int targetFullId, int x, int y, int z, int deltaX, int deltaY, int deltaZ) {
        int toSet = selfFullId ^ (selfFullId & 0xF); //strip meta
        if ((selfFullId & 0x8) != 0)    {
            //downwards flowing liquids should have their "offspring" be at full height
            toSet |= (deltaX != 0 || deltaZ != 0) ? 0x1 : 0x8;
        } else if (deltaY < 0) {
            //if the next fluid will be flowing downwards, only set the down flag
            toSet |= 0x8;
        } else {
            //otherwise, we're just flowing normally to the side, so just increment the side counter
            toSet |= (selfFullId & 0x7) + 1;
        }
        this.level.setBlockFullIdAt(x + deltaX, y + deltaY, z + deltaZ, toSet);
        this.level.scheduleUpdate(this.level.getBlock(x + deltaX, y + deltaY, z + deltaZ), this.tickRate());
    }

    protected void spreadToSides(int selfFullId, int x, int y, int z)  {
        int fullId;
        if (this.canSpreadInto(fullId = this.level.getFullBlock(x + 1, y, z)))   {
            this.spreadIntoBlock(selfFullId, fullId, x, y, z, 1, 0, 0);
        }
        if (this.canSpreadInto(fullId = this.level.getFullBlock(x, y, z + 1)))   {
            this.spreadIntoBlock(selfFullId, fullId, x, y, z, 0, 0, 1);
        }
        if (this.canSpreadInto(fullId = this.level.getFullBlock(x - 1, y, z)))   {
            this.spreadIntoBlock(selfFullId, fullId, x, y, z, -1, 0, 0);
        }
        if (this.canSpreadInto(fullId = this.level.getFullBlock(x, y, z - 1)))   {
            this.spreadIntoBlock(selfFullId, fullId, x, y, z, 0, 0, -1);
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            this.checkForHarden();
            this.level.scheduleUpdate(this, this.tickRate());
            return 0;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            final int meta = this.getDamage(); //allow JIT to do register inlining
            final int fullId = (this.getId() << 4) | meta;
            final int x = this.getFloorX();
            final int y = this.getFloorY();
            final int z = this.getFloorZ();

            int otherFullId; //scratch variable

            //first of all, check if we can spread into any neighboring blocks
            if ((meta >>> 3) != 0) {
                //we are a liquid flowing down
                if (this.canSpreadInto(otherFullId = this.level.getFullBlock(x, y - 1, z)))  {
                    //spread one block downwards
                    this.spreadIntoBlock(fullId, otherFullId, x, y, z, 0, -1, 0);
                } else {
                    //we can't continue to flow downwards, so spread to the sides instead
                    if ((meta & 0x7) < 0x7) {
                        //don't spread to sides if we are already at the lowest water level
                        this.spreadToSides(fullId, x, y, z);
                    }
                }
            } else if (meta == 0) {
                //we are a source block
                //spread to sides and downwards
                this.spreadToSides(fullId, x, y, z);
                if (this.canSpreadInto(otherFullId = this.level.getFullBlock(x, y - 1, z)))  {
                    //spread one block downwards
                    this.spreadIntoBlock(fullId, otherFullId, x, y, z, 0, -1, 0);
                }
            } else {
                //we are a flowing liquid block
                //flow downwards if we can, to the sides otherwise
                if (this.canSpreadInto(otherFullId = this.level.getFullBlock(x, y - 1, z)))  {
                    //spread one block downwards
                    this.spreadIntoBlock(fullId, otherFullId, x, y, z, 0, -1, 0);
                } else {
                    //we can't flow downwards, so spread to the sides instead
                    if ((meta & 0x7) < 0x7) {
                        //don't spread to sides if we are already at the lowest water level
                        this.spreadToSides(fullId, x, y, z);
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public double getHardness() {
        return 100d;
    }

    @Override
    public double getResistance() {
        return 500;
    }

    /*protected void triggerLavaMixEffects(Vector3 pos) {
        this.getLevel().addSound(pos.add(0.5, 0.5, 0.5), Sound.RANDOM_FIZZ, 1, 2.6F + (ThreadLocalRandom.current().nextFloat() - ThreadLocalRandom.current().nextFloat()) * 0.8F);

        for (int i = 0; i < 8; ++i) {
            this.getLevel().addParticle(new SmokeParticle(pos.add(Math.random(), 1.2, Math.random())));
        }
    }*/

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockAir());
    }
}
