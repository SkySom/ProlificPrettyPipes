package xyz.brassgoggledcoders.prolificprettypipes.item;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

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

    public static Fluid getFluidFromStack(ItemStack itemStack) {
        CompoundNBT fluidStackNBT = itemStack.getChildTag("fluidStack");
        if (fluidStackNBT != null) {
            String fluidName = fluidStackNBT.getString("Fluid");
            if (!fluidName.isEmpty()) {
                return ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
            }
        }
        return null;
    }
}
