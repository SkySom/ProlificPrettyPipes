package xyz.brassgoggledcoders.prolificprettypipes.item;

import de.ellpeck.prettypipes.items.IModule;
import de.ellpeck.prettypipes.items.ModuleTier;
import de.ellpeck.prettypipes.network.PipeNetwork;
import de.ellpeck.prettypipes.pipe.PipeTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import xyz.brassgoggledcoders.prolificprettypipes.pipe.IPipeType;

public class BasicExtractionModule<TYPE extends IPipeType<VALUE, HANDLER>, VALUE, HANDLER> extends BasicModuleItem {
    private final TYPE pipeType;
    private final int maxExtraction;
    private final int speed;
    private final boolean preventOversending;
    public final int filterSlots;

    public BasicExtractionModule(Properties properties, TYPE pipeType, ModuleTier moduleTier) {
        super(properties);
        this.pipeType = pipeType;
        this.maxExtraction = moduleTier.forTier(10, 100, 1000);
        this.speed = moduleTier.forTier(20, 15, 10);
        this.filterSlots = moduleTier.forTier(1, 2, 3);
        this.preventOversending = moduleTier.forTier(false, false, true);
    }

    @Override
    public void tick(ItemStack module, PipeTileEntity tile) {
        if (tile.getWorld() != null && tile.getWorld().getGameTime() % (long) this.speed == 0L) {
            if (tile.canWork()) {
                PipeNetwork network = PipeNetwork.get(tile.getWorld());
                for (Direction direction : Direction.values()) {
                    HANDLER handler = tile.getNeighborCap(direction, pipeType.getCapability());
                    if (handler != null) {
                        pipeType.route(network, tile.getWorld(), handler, value -> true,
                                tile.getPos(), tile.getPos().offset(direction), this.maxExtraction,
                                this.preventOversending);
                    }
                }
            }
        }
    }

    @Override
    public boolean canNetworkSee(ItemStack moduleStack, PipeTileEntity pipeTileEntity) {
        return false;
    }

    @Override
    public boolean canAcceptItem(ItemStack module, PipeTileEntity tile, ItemStack stack) {
        return false;
    }

    @Override
    public boolean isCompatible(ItemStack module, PipeTileEntity tile, IModule other) {
        return other != this;
    }

}
