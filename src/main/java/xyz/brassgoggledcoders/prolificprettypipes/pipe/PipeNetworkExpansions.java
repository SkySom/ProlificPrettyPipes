package xyz.brassgoggledcoders.prolificprettypipes.pipe;

import de.ellpeck.prettypipes.items.IModule;
import de.ellpeck.prettypipes.misc.ItemEqualityType;
import de.ellpeck.prettypipes.network.PipeItem;
import de.ellpeck.prettypipes.network.PipeNetwork;
import de.ellpeck.prettypipes.pipe.PipeTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PipeNetworkExpansions {
    @Nonnull
    public static LazyOptional<IFluidHandler> getFluidHandler(PipeTileEntity pipeTileEntity, Direction direction) {
        if (pipeTileEntity.getWorld() != null && pipeTileEntity.isConnected(direction)) {
            World world = pipeTileEntity.getWorld();
            BlockPos pos = pipeTileEntity.getPos().offset(direction);
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
            }
        }
        return LazyOptional.empty();
    }

    public static FluidStack routeFluid(PipeNetwork pipeNetwork, World world, BlockPos startPipePos, BlockPos startInventory,
                                        FluidStack fluidStack, boolean preventOversending) {
        return routeFluid(pipeNetwork, world, startPipePos, startInventory, fluidStack, FluidPipeItem::new,
                preventOversending);
    }

    public static FluidStack routeFluid(PipeNetwork pipeNetwork, World world, BlockPos startPipePos, BlockPos startInventory,
                                        FluidStack fluidStack, BiFunction<FluidStack, Float, PipeItem> itemSupplier,
                                        boolean preventOversending) {
        if (!pipeNetwork.isNode(startPipePos)) {
            return fluidStack;
        } else if (!world.isBlockLoaded(startPipePos)) {
            return fluidStack;
        } else {
            PipeTileEntity startPipe = pipeNetwork.getPipe(startPipePos);
            if (startPipe == null) {
                return fluidStack;
            } else {
                pipeNetwork.startProfile("find_destination");

                for (BlockPos pipePos : pipeNetwork.getOrderedNetworkNodes(startPipePos)) {
                    if (world.isBlockLoaded(pipePos)) {
                        PipeTileEntity pipe = pipeNetwork.getPipe(pipePos);
                        Pair<BlockPos, FluidStack> dest = pipe.getAvailableDestination(fluidStack, false, preventOversending);
                        if (dest != null && !dest.getLeft().equals(startInventory)) {
                            Function<Float, PipeItem> sup = (speed) -> (PipeItem) itemSupplier.apply(dest.getRight(), speed);
                            if (routeFluidToLocation(startPipePos, startInventory, pipe.getPos(), (BlockPos) dest.getLeft(), (ItemStack) dest.getRight(), sup)) {
                                ItemStack remain = stack.copy();
                                remain.shrink(((ItemStack) dest.getRight()).getCount());
                                pipeNetwork.endProfile();
                                return remain;
                            }
                        }
                    }
                }

                pipeNetwork.endProfile();
                return fluidStack;
            }
        }
    }

    public static Pair<BlockPos, FluidStack> getAvailableDestination(PipeTileEntity pipeTileEntity, FluidStack fluidStack,
                                                                     boolean force, boolean preventOversending) {
        World world = pipeTileEntity.getWorld();
        if (world != null || pipeTileEntity.canWork())
        if (!pipeTileEntity.canWork()) {
            return null;
        } else if (!canAcceptFluidStack(pipeTileEntity, fluidStack, force)) {
            return null;
        } else {
            for (Direction direction : Direction.values()) {
                getFluidHandler(pipeTileEntity, direction)
                        .ifPresent(fluidHandler -> {
                            int filled = fluidHandler.fill(fluidStack, FluidAction.SIMULATE);
                            if (filled != fluidStack.getAmount()) {
                                FluidStack toInsert = fluidStack.copy();
                                toInsert.shrink(filled);
                                int maxAmount = getMaxInsertion(pipeTileEntity, fluidStack, fluidHandler);
                                if (maxAmount < toInsert.getAmount()) {
                                    toInsert.setAmount(maxAmount);
                                }

                                BlockPos offset = pipeTileEntity.getPos().offset(direction);
                                if (preventOversending || maxAmount < 2147483647) {
                                    PipeNetwork network = PipeNetwork.get(pipeTileEntity.getWorld());
                                    int onTheWay = network.getItemsOnTheWay(offset, null);
                                    if (onTheWay > 0) {
                                        if (maxAmount < 2147483647) {
                                            int onTheWaySame = network.getItemsOnTheWay(offset, stack, new ItemEqualityType[0]);
                                            if (toInsert.getAmount() + onTheWaySame > maxAmount) {
                                                toInsert.setAmount(maxAmount - onTheWaySame);
                                            }
                                        }

                                        ItemStack copy = stack.copy();
                                        copy.setCount(copy.getMaxStackSize());
                                        int totalSpace = 0;

                                        for (int i = 0; i < handler.getSlots(); ++i) {
                                            ItemStack left = handler.insertItem(i, copy, true);
                                            totalSpace += copy.getMaxStackSize() - left.getCount();
                                        }

                                        if (onTheWay + toInsert.getCount() > totalSpace) {
                                            toInsert.setCount(totalSpace - onTheWay);
                                        }
                                    }
                                }

                                if (!toInsert.isEmpty()) {
                                    return Pair.of(offset, toInsert);
                                }
                            }
                        });
            }

            return null;
        }
    }

    public static boolean canAcceptFluidStack(PipeTileEntity pipeTileEntity, FluidStack fluidStack, boolean force) {
        return pipeTileEntity.streamModules()
                .anyMatch(module -> {
                    if (module.getRight() instanceof IFluidModule) {
                        return force || ((IFluidModule) module.getRight()).canAcceptFluid(module.getLeft(), pipeTileEntity,
                                fluidStack);
                    } else {
                        return false;
                    }
                });
    }

    public static int getMaxInsertion(PipeTileEntity pipeTileEntity, FluidStack fluidStack, IFluidHandler fluidHandler) {
        return pipeTileEntity.streamModules()
                .mapToInt((modulePair) -> {
                    if (modulePair.getRight() instanceof IFluidModule) {
                        return ((IFluidModule)modulePair.getRight()).getMaxInsertionAmount(modulePair.getLeft(),
                                pipeTileEntity, fluidStack, fluidHandler)

                    } else {
                        return 0;
                    }
                })
                .min()
                .orElse(2147483647);
    }

    public static int getItemsOnTheWay(PipeNetwork pipeNetwork, BlockPos goalInv, FluidStack type, ItemEqualityType... equalityTypes) {
        return pipeNetwork.getPipeItemsOnTheWay(goalInv).filter((i) -> {
            return type == null || ItemEqualityType.compareItems(i.stack, type, equalityTypes);
        }).mapToInt((i) -> {
            return i.stack.getCount();
        }).sum();
    }
}
