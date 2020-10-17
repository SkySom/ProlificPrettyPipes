package xyz.brassgoggledcoders.prolificprettypipes.item;

import de.ellpeck.prettypipes.items.ModuleTier;
import de.ellpeck.prettypipes.network.PipeNetwork;
import de.ellpeck.prettypipes.pipe.PipeTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import xyz.brassgoggledcoders.prolificprettypipes.pipe.PipeNetworkExpansions;

public class FluidExtractionModule extends FluidModuleItem {
    private final int maxExtraction;
    private final int speed;
    private final boolean preventOversending;
    public final int filterSlots;

    public FluidExtractionModule(Properties properties, ModuleTier moduleTier) {
        super(properties);
        this.maxExtraction = moduleTier.forTier(10, 100, 1000);
        this.speed = moduleTier.forTier(20, 15, 10);
        this.filterSlots = moduleTier.forTier(1, 2, 3);
        this.preventOversending = moduleTier.forTier(false, false, true);
    }

    public void tick(ItemStack module, PipeTileEntity tile) {
        if (tile.getWorld() != null && tile.getWorld().getGameTime() % (long) this.speed == 0L) {
            if (tile.canWork()) {
                PipeNetwork network = PipeNetwork.get(tile.getWorld());
                for (Direction direction : Direction.values()) {
                    PipeNetworkExpansions.getFluidHandler(tile, direction)
                            .ifPresent(fluidHandler -> {
                                FluidStack fluidStack = fluidHandler.drain(this.maxExtraction, FluidAction.SIMULATE);
                                if (!fluidStack.isEmpty()) {

                                }
                            });

                    for (int j = 0; j < handler.getSlots(); j++) {
                        ItemStack stack = handler.extractItem(j, this.maxExtraction, true);
                        if (stack.isEmpty())
                            continue;
                        ItemStack remain = network.routeItem(tile.getPos(), tile.getPos().offset(direction), stack, this.preventOversending);
                        if (remain.getCount() != stack.getCount()) {
                            handler.extractItem(j, stack.getCount() - remain.getCount(), false);
                            return;
                        }
                    }
                }
            }
        }
    }
}
