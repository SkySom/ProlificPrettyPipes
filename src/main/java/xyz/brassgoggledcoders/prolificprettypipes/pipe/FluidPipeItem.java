package xyz.brassgoggledcoders.prolificprettypipes.pipe;

import de.ellpeck.prettypipes.network.PipeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidPipeItem extends PipeItem {
    private final FluidStack fluidStack;

    public FluidPipeItem(FluidStack fluidStack, float speed) {
        super(ItemStack.EMPTY, speed);
        this.fluidStack = fluidStack;
    }
}
