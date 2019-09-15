package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockLiquid extends BlockTransparentMeta {
    public static boolean isWater(int fullId) {
        return (fullId >>> 4) == WATER || (fullId >>> 4) == STILL_WATER;
    }

    public static boolean isLava(int fullId) {
        return (fullId >>> 4) == LAVA || (fullId >>> 4) == STILL_LAVA;
    }
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

    protected abstract boolean isSelfType(int fullId);

    protected boolean checkForHarden(int x, int y, int z) {
        return false;
    }

    protected boolean canSpreadInto(int fullId) {
        return flowable[fullId >>> 4];
    }

    protected void spreadIntoBlock(int selfFullId, int targetFullId, int x, int y, int z, int deltaX, int deltaY, int deltaZ) {
        int decay = this.getFlowDecayPerBlock();
        if (this.isSelfType(targetFullId)) {
            //we are going to be replacing another block of this type, ensure that the level would make that possible
            if ((targetFullId & 0xF) == 0 || (targetFullId & 0x8) != 0) {
                //don't replace source or downwards flowing blocks in any case
                return;
            } else if ((selfFullId & 0xF) == 0 || (selfFullId & 0x8) != 0) {
                //source or downwards flowing blocks can always replace other blocks of their same type
            } else if ((selfFullId & 0x7) + decay > 7 || (selfFullId & 0x7) + decay > (targetFullId & 0x7)) {
                //don't replace other blocks if this block cannot flow any further, or the target block is a higher level than this one
                return;
            }
        } else if ((targetFullId >>> 4) != AIR && this.doReplace(targetFullId, x, y, z, deltaX, deltaY, deltaZ)) {
            //if replacing something that isn't air and the handler returns true, don't set the block
            return;
        }

        int toSet = selfFullId ^ (selfFullId & 0xF); //strip meta
        if ((selfFullId & 0x8) != 0) {
            //downwards flowing liquids should have their "offspring" be at full height
            toSet |= (deltaX != 0 || deltaZ != 0) ? 0x1 : 0x8;
        } else if (deltaY < 0) {
            //if the next fluid will be flowing downwards, only set the down flag
            toSet |= 0x8;
        } else {
            //otherwise, we're just flowing normally to the side, so just increment the side counter
            toSet |= (selfFullId & 0x7) + decay;
        }
        this.level.setBlockFullIdAt(x, y, z, toSet);
        this.level.scheduleUpdate(this.level.getBlock(x, y, z), this.tickRate());
    }

    protected boolean doReplace(int targetFullId, int x, int y, int z, int deltaX, int deltaY, int deltaZ) {
        //TODO: override in water and lava classes for generation of other block types
        this.level.useBreakOn(new Vector3(x, y, z));
        return false;
    }

    protected void spreadToSides(int selfFullId, int x, int y, int z) {
        int fullId;
        if (this.canSpreadInto(fullId = this.level.getFullBlock(x + 1, y, z))) {
            this.spreadIntoBlock(selfFullId, fullId, x + 1, y, z, 1, 0, 0);
        }
        if (this.canSpreadInto(fullId = this.level.getFullBlock(x, y, z + 1))) {
            this.spreadIntoBlock(selfFullId, fullId, x, y, z + 1, 0, 0, 1);
        }
        if (this.canSpreadInto(fullId = this.level.getFullBlock(x - 1, y, z))) {
            this.spreadIntoBlock(selfFullId, fullId, x - 1, y, z, -1, 0, 0);
        }
        if (this.canSpreadInto(fullId = this.level.getFullBlock(x, y, z - 1))) {
            this.spreadIntoBlock(selfFullId, fullId, x, y, z - 1, 0, 0, -1);
        }
    }

    @Override
    public int onUpdate(int type) {
        final int x = this.getFloorX();
        final int y = this.getFloorY();
        final int z = this.getFloorZ();

        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (false && !this.checkForHarden(x, y, z)) {
            }
            this.level.scheduleUpdate(this, this.tickRate());
            return 0;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (this.checkForHarden(x, y, z)) {
                return 0;
            }

            final int meta = this.getDamage(); //allow JIT to do register inlining
            final int fullId = (this.getId() << 4) | meta;
            final int decay = this.getFlowDecayPerBlock();

            int otherFullId; //scratch variable

            //first of all, examine decay and update if needed
            if (meta != 0) {
                //source blocks cannot decay
                int level = meta & 0x7;
                if ((meta >>> 3) != 0) {
                    //we are a liquid flowing down
                    if (!this.isSelfType(this.level.getFullBlock(x, y + 1, z))) {
                        //block above is not of this type, so we need to decay
                        int incomingLevel = 8;
                        if (this.isSelfType(otherFullId = (this.level.getFullBlock(x + 1, y, z))) && (otherFullId == 0 || (otherFullId & 0x7) < incomingLevel)) {
                            incomingLevel = otherFullId & 0x7;
                        }
                        if (this.isSelfType(otherFullId = (this.level.getFullBlock(x, y, z + 1))) && (otherFullId == 0 || (otherFullId & 0x7) < incomingLevel)) {
                            incomingLevel = otherFullId & 0x7;
                        }
                        if (this.isSelfType(otherFullId = (this.level.getFullBlock(x - 1, y, z))) && (otherFullId == 0 || (otherFullId & 0x7) < incomingLevel)) {
                            incomingLevel = otherFullId & 0x7;
                        }
                        if (this.isSelfType(otherFullId = (this.level.getFullBlock(x, y, z - 1))) && (otherFullId == 0 || (otherFullId & 0x7) < incomingLevel)) {
                            incomingLevel = otherFullId & 0x7;
                        }
                        if (incomingLevel + decay > 7) {
                            //reset to air with no inputs
                            this.level.setBlockFullIdAt(x, y, z, AIR << 4);
                        } else {
                            this.level.setBlockFullIdAt(x, y, z, (fullId ^ (fullId & 0xF)) | (incomingLevel + decay));
                        }
                        this.level.updateAroundFast(x, y, z);
                        return 0;
                    }
                } else {
                    //we are a liquid flowing sideways
                    if (this.isSelfType(this.level.getFullBlock(x, y + 1, z)))  {
                        //turn into liquid flowing downwards
                        this.level.setBlockFullIdAt(x, y, z, (fullId ^ (fullId & 0xF)) | 0x8);
                        this.level.updateAroundFast(x, y, z);
                    } else {
                        int incomingLevel = 8;
                        if (this.isSelfType(otherFullId = (this.level.getFullBlock(x + 1, y, z))) && (otherFullId == 0 || (otherFullId & 0x7) < incomingLevel)) {
                            incomingLevel = otherFullId & 0x7;
                        }
                        if (this.isSelfType(otherFullId = (this.level.getFullBlock(x, y, z + 1))) && (otherFullId == 0 || (otherFullId & 0x7) < incomingLevel)) {
                            incomingLevel = otherFullId & 0x7;
                        }
                        if (this.isSelfType(otherFullId = (this.level.getFullBlock(x - 1, y, z))) && (otherFullId == 0 || (otherFullId & 0x7) < incomingLevel)) {
                            incomingLevel = otherFullId & 0x7;
                        }
                        if (this.isSelfType(otherFullId = (this.level.getFullBlock(x, y, z - 1))) && (otherFullId == 0 || (otherFullId & 0x7) < incomingLevel)) {
                            incomingLevel = otherFullId & 0x7;
                        }
                        if (incomingLevel + decay != level) {
                            if (incomingLevel + decay > 7) {
                                //reset to air with no inputs
                                this.level.setBlockFullIdAt(x, y, z, AIR << 4);
                            } else {
                                this.level.setBlockFullIdAt(x, y, z, (fullId ^ (fullId & 0xF)) | (incomingLevel + decay));
                            }
                            this.level.updateAroundFast(x, y, z);
                        }
                    }
                }
            }

            //then check if we can spread into any neighboring blocks
            if ((meta >>> 3) != 0) {
                //we are a liquid flowing down
                if (this.canSpreadInto(otherFullId = this.level.getFullBlock(x, y - 1, z))) {
                    //spread one block downwards
                    this.spreadIntoBlock(fullId, otherFullId, x, y - 1, z, 0, -1, 0);
                } else {
                    //we can't continue to flow downwards, so spread to the sides instead
                    if ((meta & 0x7) + decay <= 7) {
                        //don't spread to sides if we are already at the lowest water level
                        this.spreadToSides(fullId, x, y, z);
                    }
                }
            } else if (meta == 0) {
                //we are a source block
                //spread to sides and downwards
                this.spreadToSides(fullId, x, y, z);
                if (this.canSpreadInto(otherFullId = this.level.getFullBlock(x, y - 1, z))) {
                    //spread one block downwards
                    this.spreadIntoBlock(fullId, otherFullId, x, y - 1, z, 0, -1, 0);
                }
            } else {
                //we are a flowing liquid block
                //flow downwards if we can, to the sides otherwise
                if (this.canSpreadInto(otherFullId = this.level.getFullBlock(x, y - 1, z))) {
                    //spread one block downwards
                    this.spreadIntoBlock(fullId, otherFullId, x, y - 1, z, 0, -1, 0);
                } else {
                    //we can't flow downwards, so spread to the sides instead
                    if ((meta & 0x7) + decay <= 7) {
                        //don't spread to sides if we are already at the lowest water level
                        this.spreadToSides(fullId, x, y, z);
                    }
                }
            }
        }
        return 0;
    }

    protected void triggerLavaMixEffects(int x, int y, int z) {
        this.level.addSound(new Vector3(x + 0.5d, y + 0.5d, z + 0.5d), Sound.RANDOM_FIZZ, 1, 2.6F + (float) (Math.random() * 2.0d - 1.0d) * 0.8F);

        for (int i = 0; i < 8; i++) {
            this.level.addParticle(new SmokeParticle(new Vector3(x + Math.random(), y + 1.2d, z + Math.random())));
        }
    }

    @Override
    public double getHardness() {
        return 100d;
    }

    @Override
    public double getResistance() {
        return 500;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockAir());
    }
}
