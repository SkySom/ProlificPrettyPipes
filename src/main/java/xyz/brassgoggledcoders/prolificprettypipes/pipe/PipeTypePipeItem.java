package xyz.brassgoggledcoders.prolificprettypipes.pipe;

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
    }

    public PipeTypePipeItem(IPipeType<VALUE, HANDLER> pipeType, CompoundNBT compoundNBT) {
        super(pipeType.getType(), ItemStack.EMPTY, 0.0F);
        this.pipeType = pipeType;
        this.value = pipeType.getValueFromNBT(compoundNBT.getCompound("value"));
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
        Direction dir = Utility.getDirectionFromOffset(this.destInventory, this.getDestPipe());
        HANDLER handler = currPipe.getNeighborCap(dir, this.getPipeType().getCapability());
        if (handler != null) {
            VALUE inserted = this.getPipeType().insert(handler, this.getValue(), true);
            int count = this.getPipeType().getCount(inserted);
            this.getPipeType().reduce(this.getValue(), count);
            this.stack.shrink(count);
        }
        return this.stack;
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
