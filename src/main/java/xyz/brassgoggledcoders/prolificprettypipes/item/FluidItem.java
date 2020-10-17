package xyz.brassgoggledcoders.prolificprettypipes.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;

public class FluidItem extends Item {
    public FluidItem(Properties properties) {
        super(properties);
    }

    public ItemStack fromFluidStack(FluidStack fluidStack) {
        ItemStack itemStack = new ItemStack(this);
        CompoundNBT fluidStackNBT = fluidStack.writeToNBT(new CompoundNBT());
        fluidStackNBT.remove("Amount");
        itemStack.setTagInfo("fluidStack", fluidStackNBT);
        itemStack.setCount(fluidStack.getAmount());
        return itemStack;
    }
}
