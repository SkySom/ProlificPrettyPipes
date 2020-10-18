package xyz.brassgoggledcoders.prolificprettypipes.pipe;

import de.ellpeck.prettypipes.items.IModule;
import de.ellpeck.prettypipes.network.PipeNetwork;
import de.ellpeck.prettypipes.pipe.PipeTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface IPipeType<VALUE, HANDLER> {
    @Nullable
    IExtendedModule<VALUE, HANDLER> getModuleExtension(IModule module);

    ItemStack getStackFrom(VALUE value);

    /**
     * @return The Value extracted
     */
    VALUE extract(HANDLER handler, int amount, boolean simulate);

    /**
     * @return The Value remain
     */
    VALUE insert(HANDLER handler, VALUE value, boolean simulate);

    Capability<HANDLER> getCapability();

    PipeTypePipeItem<VALUE, HANDLER> getPipeItem(VALUE value, float speed);

    PipeTypePipeItem<VALUE, HANDLER> getPipeItem(ResourceLocation resourceLocation,
                                                 CompoundNBT compoundNBT);

    default void route(PipeNetwork pipeNetwork, World world, HANDLER handler, Predicate<VALUE> filterCheck, BlockPos pipePosition,
                       BlockPos handlerPosition, int maxExtraction, boolean preventOversending) {
        VALUE value = this.extract(handler, maxExtraction, true);
        if (this.getCount(value) != 0 && filterCheck.test(value)) {
            VALUE remain = PipeNetworkExpansions.route(pipeNetwork, world, this, pipePosition, handlerPosition,
                    value, preventOversending);
            if (this.getCount(remain) != this.getCount(value)) {
                this.extract(handler, this.getCount(value) - this.getCount(remain), false);
            }
        }
    }

    VALUE copy(VALUE value);

    void setCount(VALUE value, int count);

    default void reduce(VALUE remain, VALUE toRemove) {
        this.reduce(remain, this.getCount(toRemove));
    }

    void reduce(VALUE value, int amount);

    int getCount(VALUE value);

    default int getMaxStackSize(VALUE value) {
        return 2147483647;
    }

    default boolean canAccept(IModule module, ItemStack moduleStack, PipeTileEntity pipeTileEntity, VALUE value) {
        IExtendedModule<VALUE, HANDLER> moduleExtension = this.getModuleExtension(module);
        if (moduleExtension != null) {
            return moduleExtension.canAccept(moduleStack, pipeTileEntity, value);
        } else {
            return true;
        }
    }

    default int getMaxInsertion(IModule module, ItemStack moduleStack, PipeTileEntity pipeTileEntity, VALUE value,
                                HANDLER handler) {
        IExtendedModule<VALUE, HANDLER> moduleExtension = this.getModuleExtension(module);
        if (moduleExtension != null) {
            return moduleExtension.getMaxInsertion(moduleStack, pipeTileEntity, value, handler);
        } else {
            return 2147483647;
        }
    }

    int getTotalSpace(HANDLER handler, VALUE value);

    ResourceLocation getType();

    VALUE getValueFromNBT(CompoundNBT nbt);
}
