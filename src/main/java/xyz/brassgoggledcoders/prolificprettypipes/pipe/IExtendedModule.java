package xyz.brassgoggledcoders.prolificprettypipes.pipe;

import de.ellpeck.prettypipes.items.IModule;
import de.ellpeck.prettypipes.pipe.PipeTileEntity;
import net.minecraft.item.ItemStack;

public interface IExtendedModule<T, U> extends IModule {
    default boolean canAccept(ItemStack moduleStack, PipeTileEntity pipeTileEntity, T input) {
        return true;
    }

    default int getMaxInsertion(ItemStack moduleStack, PipeTileEntity pipeTileEntity, T input, U container) {
        return 2147483647;
    }
}
