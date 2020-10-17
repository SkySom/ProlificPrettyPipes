package xyz.brassgoggledcoders.prolificprettypipes.pipe;

import de.ellpeck.prettypipes.items.IModule;
import de.ellpeck.prettypipes.network.PipeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import xyz.brassgoggledcoders.prolificprettypipes.ProlificPrettyPipes;
import xyz.brassgoggledcoders.prolificprettypipes.content.ProlificItems;

import javax.annotation.Nullable;

public class FluidPipeType implements IPipeType<FluidStack, IFluidHandler, IFluidModule, FluidPipeItem> {
    public static final FluidPipeType INSTANCE = setup();

    @Nullable
    @Override
    public IFluidModule getModuleExtension(IModule module) {
        if (module instanceof IFluidModule) {
            return (IFluidModule) module;
        }
        return null;
    }

    @Override
    public ItemStack getStackFrom(FluidStack value) {
        return ProlificItems.FLUID.map(item -> item.fromFluidStack(value))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public FluidStack extract(IFluidHandler fluidHandler, int amount, boolean simulate) {
        return fluidHandler.drain(amount, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
    }

    @Override
    public FluidStack insert(IFluidHandler fluidHandler, FluidStack fluidStack, boolean simulate) {
        int amountInsert = fluidHandler.fill(fluidStack, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
        FluidStack remainder = fluidStack.copy();
        remainder.shrink(amountInsert);
        return remainder;
    }

    @Override
    public Capability<IFluidHandler> getCapability() {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    public FluidPipeItem getPipeItem(FluidStack fluidStack, float speed) {
        return new FluidPipeItem(fluidStack, speed);
    }

    @Override
    public FluidPipeItem getPipeItem(ResourceLocation resourceLocation, CompoundNBT compoundNBT) {
        return new FluidPipeItem(null, 0.0F);
    }

    @Override
    public FluidStack copy(FluidStack fluidStack) {
        return fluidStack.copy();
    }

    @Override
    public void setCount(FluidStack fluidStack, int count) {
        fluidStack.setAmount(count);
    }

    @Override
    public void reduce(FluidStack fluidStack, int amount) {
        fluidStack.shrink(amount);
    }

    @Override
    public int getCount(FluidStack fluidStack) {
        return fluidStack.getAmount();
    }

    @Override
    public int getTotalSpace(IFluidHandler fluidHandler, FluidStack fluidStack) {
        return 1000000;
    }

    private static FluidPipeType setup() {
        FluidPipeType fluidPipeType = new FluidPipeType();
        PipeItem.TYPES.put(ProlificPrettyPipes.rl("fluid"), fluidPipeType::getPipeItem);
        return fluidPipeType;
    }
}
