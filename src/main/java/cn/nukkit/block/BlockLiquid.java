package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockFromToEvent;
import cn.nukkit.event.block.LiquidFlowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockLiquid extends BlockTransparentMeta {
    private static final ThreadLocal<Long2ByteOpenHashMap> flowCostVisited = ThreadLocal.withInitial(Long2ByteOpenHashMap::new);
    private static final ThreadLocal<int[]> flowCost = ThreadLocal.withInitial(() -> new int[4]);

    private static final byte CAN_FLOW_DOWN = 1;
    private static final byte CAN_FLOW = 0;
    private static final byte BLOCKED = -1;

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
        return this.y + 1 - getFluidHeightPercent();
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    public float getFluidHeightPercent() {
        float d = (float) this.getDamage();
        if (d >= 8) {
            d = 0;
        }

        return (d + 1) / 9f;
    }

    protected int getFlowDecay(Block block) {
        if (block.getId() != this.getId()) {
            return -1;
        }
        return block.getDamage();
    }

    protected int getEffectiveFlowDecay(Block block) {
        if (block.getId() != this.getId()) {
            return -1;
        }
        int decay = block.getDamage();
        if (decay >= 8) {
            decay = 0;
        }
        return decay;
    }

    public Vector3 getFlowVector() {
        if (this.flowVector != null) {
            return this.flowVector;
        }
        Vector3 vector = new Vector3(0, 0, 0);
        int decay = this.getEffectiveFlowDecay(this);
        for (int j = 0; j < 4; ++j) {
            int x = (int) this.x;
            int y = (int) this.y;
            int z = (int) this.z;
            switch (j) {
                case 0:
                    --x;
                    break;
                case 1:
                    x++;
                    break;
                case 2:
                    z--;
                    break;
                default:
                    z++;
            }
            Block sideBlock = this.level.getBlock(x, y, z);
            int blockDecay = this.getEffectiveFlowDecay(sideBlock);
            if (blockDecay < 0) {
                if (!sideBlock.canBeFlowedInto()) {
                    continue;
                }
                blockDecay = this.getEffectiveFlowDecay(this.level.getBlock(x, y - 1, z));
                if (blockDecay >= 0) {
                    int realDecay = blockDecay - (decay - 8);
                    vector.x += (sideBlock.x - this.x) * realDecay;
                    vector.y += (sideBlock.y - this.y) * realDecay;
                    vector.z += (sideBlock.z - this.z) * realDecay;
                }
            } else {
                int realDecay = blockDecay - decay;
                vector.x += (sideBlock.x - this.x) * realDecay;
                vector.y += (sideBlock.y - this.y) * realDecay;
                vector.z += (sideBlock.z - this.z) * realDecay;
            }
        }
        if (this.getDamage() >= 8) {
            if (!this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y, (int) this.z - 1)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y, (int) this.z + 1)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x - 1, (int) this.y, (int) this.z)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x + 1, (int) this.y, (int) this.z)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y + 1, (int) this.z - 1)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y + 1, (int) this.z + 1)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x - 1, (int) this.y + 1, (int) this.z)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x + 1, (int) this.y + 1, (int) this.z))) {
                vector = vector.normalize().add(0, -6, 0);
            }
        }
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

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            this.checkForHarden();
            this.level.scheduleUpdate(this, this.tickRate());
            return 0;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int decay = this.getFlowDecay(this);
            int multiplier = this.getFlowDecayPerBlock();
            if (decay > 0) {
                int smallestFlowDecay = -100;
                this.adjacentSources = 0;
                smallestFlowDecay = this.getSmallestFlowDecay(this.level.getBlock((int) this.x, (int) this.y, (int) this.z - 1), smallestFlowDecay);
                smallestFlowDecay = this.getSmallestFlowDecay(this.level.getBlock((int) this.x, (int) this.y, (int) this.z + 1), smallestFlowDecay);
                smallestFlowDecay = this.getSmallestFlowDecay(this.level.getBlock((int) this.x - 1, (int) this.y, (int) this.z), smallestFlowDecay);
                smallestFlowDecay = this.getSmallestFlowDecay(this.level.getBlock((int) this.x + 1, (int) this.y, (int) this.z), smallestFlowDecay);
                int newDecay = smallestFlowDecay + multiplier;
                if (newDecay >= 8 || smallestFlowDecay < 0) {
                    newDecay = -1;
                }
                int topFlowDecay = this.getFlowDecay(this.level.getBlock((int) this.x, (int) this.y + 1, (int) this.z));
                if (topFlowDecay >= 0) {
                    newDecay = topFlowDecay | 0x08;
                }
                if (this.adjacentSources >= 2 && this instanceof BlockWater) {
                    Block bottomBlock = this.level.getBlock((int) this.x, (int) this.y - 1, (int) this.z);
                    if (bottomBlock.isSolid()) {
                        newDecay = 0;
                    } else if (bottomBlock instanceof BlockWater && bottomBlock.getDamage() == 0) {
                        newDecay = 0;
                    }
                }
                if (newDecay != decay) {
                    decay = newDecay;
                    boolean decayed = decay < 0;
                    Block to;
                    if (decayed) {
                        to = new BlockAir();
                    } else {
                        to = getBlock(decay);
                    }
                    BlockFromToEvent event = new BlockFromToEvent(this, to);
                    level.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        this.level.setBlock(this, to, true, true);
                        if (!decayed) {
                            this.level.scheduleUpdate(this, this.tickRate());
                        }
                    }
                }
            }
            if (decay >= 0) {
                Block bottomBlock = this.level.getBlock((int) this.x, (int) this.y - 1, (int) this.z);
                this.flowIntoBlock(bottomBlock, decay | 0x08);
                if (decay == 0 || !bottomBlock.canBeFlowedInto()) {
                    int adjacentDecay;
                    if (decay >= 8) {
                        adjacentDecay = 1;
                    } else {
                        adjacentDecay = decay + multiplier;
                    }
                    if (adjacentDecay < 8) {
                        int flags = this.getOptimalFlowDirections();
                        if ((flags & (1 << 0)) != 0) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x - 1, (int) this.y, (int) this.z), adjacentDecay);
                        }
                        if ((flags & (1 << 1)) != 0) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x + 1, (int) this.y, (int) this.z), adjacentDecay);
                        }
                        if ((flags & (1 << 2)) != 0) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x, (int) this.y, (int) this.z - 1), adjacentDecay);
                        }
                        if ((flags & (1 << 3)) != 0) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x, (int) this.y, (int) this.z + 1), adjacentDecay);
                        }
                    }
                }
                this.checkForHarden();
            }
        }
        return 0;
    }

    protected void flowIntoBlock(Block block, int newFlowDecay) {
        if (this.canFlowInto(block) && !(block instanceof BlockLiquid)) {
            LiquidFlowEvent event = new LiquidFlowEvent(block, this, newFlowDecay);
            level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                if (block.getId() > 0) {
                    this.level.useBreakOn(block);
                }
                this.level.setBlock(block, getBlock(newFlowDecay), true, true);
                this.level.scheduleUpdate(block, this.tickRate());
            }
        }
    }

    private int calculateFlowCost(Long2ByteOpenHashMap flowCostVisited, int blockX, int blockY, int blockZ, int accumulatedCost, int maxCost, int originOpposite, int lastOpposite) {
        int cost = 1000;
        for (int j = 0; j < 4; ++j) {
            if (j == originOpposite || j == lastOpposite) {
                continue;
            }
            int x = blockX;
            int y = blockY;
            int z = blockZ;
            if (j == 0) {
                --x;
            } else if (j == 1) {
                ++x;
            } else if (j == 2) {
                --z;
            } else if (j == 3) {
                ++z;
            }
            long hash = Level.blockHash(x, y, z);
            if (!flowCostVisited.containsKey(hash)) {
                Block blockSide = this.level.getBlock(x, y, z);
                if (!this.canFlowInto(blockSide)) {
                    flowCostVisited.put(hash, BLOCKED);
                } else if (this.level.getBlock(x, y - 1, z).canBeFlowedInto()) {
                    flowCostVisited.put(hash, CAN_FLOW_DOWN);
                } else {
                    flowCostVisited.put(hash, CAN_FLOW);
                }
            }
            byte status = flowCostVisited.get(hash);
            if (status == BLOCKED) {
                continue;
            } else if (status == CAN_FLOW_DOWN) {
                return accumulatedCost;
            }
            if (accumulatedCost >= maxCost) {
                continue;
            }
            int realCost = this.calculateFlowCost(flowCostVisited, x, y, z, accumulatedCost + 1, maxCost, originOpposite, j ^ 0x01);
            if (realCost < cost) {
                cost = realCost;
            }
        }
        return cost;
    }

    @Override
    public double getHardness() {
        return 100d;
    }

    @Override
    public double getResistance() {
        return 500;
    }

    private int getOptimalFlowDirections() {
        int[] flowCost = BlockLiquid.flowCost.get();
        flowCost[0] = flowCost[1] = flowCost[2] = flowCost[3] = 1000;

        Long2ByteOpenHashMap flowCostVisited = BlockLiquid.flowCostVisited.get();

        int maxCost = 4 / this.getFlowDecayPerBlock();
        for (int j = 0; j < 4; ++j) {
            int x = (int) this.x;
            int y = (int) this.y;
            int z = (int) this.z;
            if (j == 0) {
                --x;
            } else if (j == 1) {
                ++x;
            } else if (j == 2) {
                --z;
            } else {
                ++z;
            }
            Block block = this.level.getBlock(x, y, z);
            if (!this.canFlowInto(block)) {
                flowCostVisited.put(Level.blockHash(x, y, z), BLOCKED);
            } else if (this.level.getBlock(x, y - 1, z).canBeFlowedInto()) {
                flowCostVisited.put(Level.blockHash(x, y, z), CAN_FLOW_DOWN);
                flowCost[j] = maxCost = 0;
            } else if (maxCost > 0) {
                flowCostVisited.put(Level.blockHash(x, y, z), CAN_FLOW);
                flowCost[j] = this.calculateFlowCost(flowCostVisited, x, y, z, 1, maxCost, j ^ 0x01, j ^ 0x01);
                maxCost = Math.min(maxCost, flowCost[j]);
            }
        }
        flowCostVisited.clear();
        int minCost = Integer.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            int j = flowCost[i];
            if (j < minCost) {
                minCost = j;
            }
        }
        return ((flowCost[0] == minCost) ? (1 << 0) : 0)
                | ((flowCost[1] == minCost) ? (1 << 1) : 0)
                | ((flowCost[2] == minCost) ? (1 << 2) : 0)
                | ((flowCost[3] == minCost) ? (1 << 3) : 0);
    }

    private int getSmallestFlowDecay(Block block, int decay) {
        int blockDecay = this.getFlowDecay(block);
        if (blockDecay < 0) {
            return decay;
        } else if (blockDecay == 0) {
            ++this.adjacentSources;
        } else if (blockDecay >= 8) {
            blockDecay = 0;
        }
        return (decay >= 0 && blockDecay >= decay) ? decay : blockDecay;
    }

    protected void checkForHarden() {
    }

    protected void triggerLavaMixEffects(Vector3 pos) {
        this.getLevel().addSound(pos.add(0.5, 0.5, 0.5), Sound.RANDOM_FIZZ, 1, 2.6F + (ThreadLocalRandom.current().nextFloat() - ThreadLocalRandom.current().nextFloat()) * 0.8F);

        for (int i = 0; i < 8; ++i) {
            this.getLevel().addParticle(new SmokeParticle(pos.add(Math.random(), 1.2, Math.random())));
        }
    }

    public abstract BlockLiquid getBlock(int meta);

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    protected boolean liquidCollide(Block cause, Block result) {
        BlockFromToEvent event = new BlockFromToEvent(this, result);
        this.level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        this.level.setBlock(this, result, true, true);
        this.getLevel().addLevelSoundEvent(this.add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_FIZZ);
        return true;
    }

    protected boolean canFlowInto(Block block) {
        return block.canBeFlowedInto() && !(block instanceof BlockLiquid && block.getDamage() == 0);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockAir());
    }
}
