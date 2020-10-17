package xyz.brassgoggledcoders.prolificprettypipes.pipe;

import de.ellpeck.prettypipes.items.IModule;
import de.ellpeck.prettypipes.pipe.PipeTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFluidModule extends IModule {
    boolean canAcceptFluid(ItemStack moduleStack, PipeTileEntity pipeTileEntity, FluidStack fluidStack);

    int getMaxInsertionAmount(ItemStack moduleStack, PipeTileEntity pipeTileEntity, FluidStack fluidStack, IFluidHandler fluidHandler);
}
