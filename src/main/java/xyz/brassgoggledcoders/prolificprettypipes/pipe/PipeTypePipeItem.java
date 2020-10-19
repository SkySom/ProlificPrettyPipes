package xyz.brassgoggledcoders.prolificprettypipes.pipe;

import com.google.common.collect.Lists;
import de.ellpeck.prettypipes.Utility;
import de.ellpeck.prettypipes.network.PipeItem;
import de.ellpeck.prettypipes.network.PipeNetwork;
import de.ellpeck.prettypipes.pipe.PipeTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import java.util.Objects;

public class PipeTypePipeItem<VALUE, HANDLER> extends PipeItem {
    private final IPipeType<VALUE, HANDLER> pipeType;
    private final VALUE value;

    public PipeTypePipeItem(IPipeType<VALUE, HANDLER> pipeType, VALUE value, float speed) {
        super(pipeType.getType(), pipeType.getStackFrom(value), speed);
        this.pipeType = pipeType;
        this.value = value;
        this.path = Lists.newArrayList();
    }

    public PipeTypePipeItem(IPipeType<VALUE, HANDLER> pipeType, CompoundNBT compoundNBT) {
        super(pipeType.getType(), pipeType.getStackFrom(pipeType.getValueFromNBT(compoundNBT.getCompound("value"))), 0.0F);
        this.pipeType = pipeType;
        this.value = pipeType.getValueFromNBT(compoundNBT.getCompound("value"));
        this.path = Lists.newArrayList();
        this.deserializeNBT(compoundNBT);
    }

    public IPipeType<VALUE, HANDLER> getPipeType() {
        return this.pipeType;
    }

    public VALUE getValue() {
        return this.value;
    }

    @Override
    public void drop(World world, ItemStack stack) {
        System.out.println(stack.getCount());
    }

    @Override
    protected ItemStack store(PipeTileEntity currPipe) {
        if (this.getPipeType().getCount(this.getValue()) != 0) {
            Direction dir = Utility.getDirectionFromOffset(this.destInventory, this.getDestPipe());
            HANDLER handler = currPipe.getNeighborCap(dir, this.getPipeType().getCapability());
            if (handler != null) {
                VALUE remaining = this.getPipeType().insert(handler, this.getValue(), false);
                int count = this.getPipeType().getCount(remaining);
                this.getPipeType().setCount(this.getValue(), count);
                this.stack.setCount(count);
            }
            return this.stack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    protected void onPathObstructed(PipeTileEntity currPipe, boolean tryReturn) {
        if (!Objects.requireNonNull(currPipe.getWorld()).isRemote) {
            PipeNetwork network = PipeNetwork.get(currPipe.getWorld());
            if (tryReturn) {
                if (!this.retryOnObstruction && network.routeItemToLocation(currPipe.getPos(), this.destInventory,
                        this.getStartPipe(), this.startInventory, this.stack, (speed) -> this)) {
                    this.retryOnObstruction = true;
                    return;
                }

                VALUE remain = PipeNetworkExpansions.route(network, currPipe.getWorld(), this.getPipeType(),
                        currPipe.getPos(), this.destInventory, value, false);

                if (this.getPipeType().getCount(remain) != 0) {
                    this.drop(currPipe.getWorld(), this.getPipeType().getStackFrom(remain));
                }
            } else {
                this.drop(currPipe.getWorld(), this.stack);
            }
        }
    }
}
