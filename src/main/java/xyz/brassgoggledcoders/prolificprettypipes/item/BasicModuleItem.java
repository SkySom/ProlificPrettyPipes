package xyz.brassgoggledcoders.prolificprettypipes.item;

import de.ellpeck.prettypipes.items.IModule;
import de.ellpeck.prettypipes.pipe.PipeTileEntity;
import de.ellpeck.prettypipes.pipe.containers.AbstractPipeContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class BasicModuleItem extends Item implements IModule {
    public BasicModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(ItemStack moduleStack, PipeTileEntity pipeTileEntity) {

    }

    @Override
    public boolean canNetworkSee(ItemStack moduleStack, PipeTileEntity pipeTileEntity) {
        return true;
    }

    @Override
    public boolean canAcceptItem(ItemStack moduleStack, PipeTileEntity pipeTileEntity, ItemStack itemStack) {
        return false;
    }

    @Override
    public int getMaxInsertionAmount(ItemStack moduleStack, PipeTileEntity pipeTileEntity, ItemStack itemStack, IItemHandler itemHandler) {
        return 0;
    }

    @Override
    public int getPriority(ItemStack moduleStack, PipeTileEntity pipeTileEntity) {
        return 0;
    }

    @Override
    public boolean isCompatible(ItemStack moduleStack, PipeTileEntity pipeTileEntity, IModule module) {
        return module != this;
    }

    @Override
    public boolean hasContainer(ItemStack moduleStack, PipeTileEntity pipeTileEntity) {
        return false;
    }

    @Override
    public AbstractPipeContainer<?> getContainer(ItemStack moduleStack, PipeTileEntity pipeTileEntity, int windowId,
                                                 PlayerInventory playerInventory, PlayerEntity playerEntity, int i1) {
        return null;
    }

    @Override
    public float getItemSpeedIncrease(ItemStack moduleStack, PipeTileEntity pipeTileEntity) {
        return 0;
    }

    @Override
    public boolean canPipeWork(ItemStack moduleStack, PipeTileEntity pipeTileEntity) {
        return true;
    }

    @Override
    public List<ItemStack> getAllCraftables(ItemStack moduleStack, PipeTileEntity pipeTileEntity) {
        return Collections.emptyList();
    }

    @Override
    public int getCraftableAmount(ItemStack moduleStack, PipeTileEntity pipeTileEntity, Consumer<ItemStack> consumer,
                                  ItemStack itemStack) {
        return 0;
    }

    @Override
    public ItemStack craft(ItemStack moduleStack, PipeTileEntity pipeTileEntity, BlockPos blockPos,
                           Consumer<ItemStack> consumer, ItemStack itemStack) {
        return itemStack;
    }
}
