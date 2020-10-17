package xyz.brassgoggledcoders.prolificprettypipes.pipe;

import de.ellpeck.prettypipes.Utility;
import de.ellpeck.prettypipes.network.PipeItem;
import de.ellpeck.prettypipes.pipe.PipeTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import xyz.brassgoggledcoders.prolificprettypipes.content.ProlificItems;

public class FluidPipeItem extends PipeItem {
    private final FluidStack fluidStack;

    public FluidPipeItem(FluidStack fluidStack, float speed) {
        super(ProlificItems.FLUID.map(item -> item.fromFluidStack(fluidStack))
                .orElse(ItemStack.EMPTY), speed);
        this.fluidStack = fluidStack;
    }

    public FluidStack getFluidStack() {
        return fluidStack;
    }

    @Override
    public void drop(World world, ItemStack stack) {
        System.out.println(stack.getCount());
    }

    @Override
    protected ItemStack store(PipeTileEntity currPipe) {
        Direction dir = Utility.getDirectionFromOffset(this.destInventory, this.getDestPipe());
        IFluidHandler handler = currPipe.getNeighborCap(dir, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        if (handler != null) {
            int count = handler.fill(this.getFluidStack(), FluidAction.EXECUTE);
            this.getFluidStack().shrink(count);
            this.stack.shrink(count);
        }
        return this.stack;
    }
}
