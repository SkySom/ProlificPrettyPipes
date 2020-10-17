package xyz.brassgoggledcoders.prolificprettypipes.pipe;

import de.ellpeck.prettypipes.network.PipeItem;
import de.ellpeck.prettypipes.network.PipeNetwork;
import de.ellpeck.prettypipes.pipe.PipeTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;
import java.util.function.Function;

public class PipeNetworkExpansions {
    @SuppressWarnings("deprecation")
    public static <TYPE extends IPipeType<VALUE, HANDLER, MODULE, PIPE>, VALUE, HANDLER, MODULE extends IExtendedModule<VALUE, HANDLER>,
            PIPE extends PipeItem> VALUE route(PipeNetwork pipeNetwork, World world, TYPE type, BlockPos startPipePos,
                                               BlockPos startInventory, VALUE value, boolean preventOversending) {
        if (!pipeNetwork.isNode(startPipePos))
            return value;
        if (!world.isBlockLoaded(startPipePos))
            return value;
        PipeTileEntity startPipe = pipeNetwork.getPipe(startPipePos);
        if (startPipe == null)
            return value;
        pipeNetwork.startProfile("find_destination");
        for (BlockPos pipePos : pipeNetwork.getOrderedNetworkNodes(startPipePos)) {
            if (!world.isBlockLoaded(pipePos))
                continue;
            PipeTileEntity pipe = pipeNetwork.getPipe(pipePos);
            Pair<BlockPos, VALUE> dest = getAvailableDestination(pipe, type, value, false, preventOversending);
            if (dest == null || dest.getLeft().equals(startInventory))
                continue;
            Function<Float, PipeItem> sup = speed -> type.getPipeItem(dest.getRight(), speed);
            if (pipeNetwork.routeItemToLocation(startPipePos, startInventory, pipe.getPos(), dest.getLeft(),
                    type.getStackFrom(dest.getRight()), sup)) {
                VALUE remain = type.copy(value);
                type.reduce(remain, dest.getRight());
                pipeNetwork.endProfile();
                return remain;
            }
        }
        pipeNetwork.endProfile();
        return value;
    }

    public static <TYPE extends IPipeType<VALUE, HANDLER, MODULE, PIPE>, VALUE, HANDLER, MODULE extends IExtendedModule<VALUE, HANDLER>,
            PIPE extends PipeItem> Pair<BlockPos, VALUE> getAvailableDestination(
            PipeTileEntity pipeTileEntity, TYPE type, VALUE input, boolean force, boolean preventOversending) {
        if (!pipeTileEntity.canWork())
            return null;
        if (!force && pipeTileEntity.streamModules().anyMatch(m -> !type.canAccept(m.getRight(), m.getLeft(),
                pipeTileEntity, input)))
            return null;
        for (Direction dir : Direction.values()) {
            HANDLER handler = pipeTileEntity.getNeighborCap(dir, type.getCapability());
            if (handler != null) {
                VALUE remain = type.insert(handler, input, true);
                // did we insert anything?
                if (type.getCount(remain) == type.getCount(input)) {
                    continue;
                }
                VALUE toInsert = type.copy(input);
                type.reduce(toInsert, type.getCount(remain));
                // limit to the max amount that modules allow us to insert
                int maxAmount = pipeTileEntity.streamModules().mapToInt(m ->
                        type.getMaxInsertion(m.getRight(), m.getLeft(), pipeTileEntity, input, handler)
                )
                        .min()
                        .orElse(Integer.MAX_VALUE);
                if (maxAmount < type.getCount(toInsert)) {
                    type.setCount(toInsert, maxAmount);
                }
                BlockPos offset = pipeTileEntity.getPos().offset(dir);
                if (preventOversending || maxAmount < Integer.MAX_VALUE) {
                    PipeNetwork network = PipeNetwork.get(Objects.requireNonNull(pipeTileEntity.getWorld()));
                    // these are the items that are currently in the pipes, going to this inventory
                    int onTheWay = network.getItemsOnTheWay(offset, null);
                    if (onTheWay > 0) {
                        if (maxAmount < Integer.MAX_VALUE) {
                            // these are the items on the way, limited to items of the same type as stack
                            int onTheWaySame = network.getItemsOnTheWay(offset, type.getStackFrom(input));
                            // check if any modules are limiting us
                            if (type.getCount(toInsert) + onTheWaySame > maxAmount)
                                type.setCount(toInsert, maxAmount - onTheWaySame);
                        }
                        VALUE copy = type.copy(input);
                        type.setCount(copy, type.getMaxStackSize(copy));
                        // totalSpace will be the amount of items that fit into the attached container
                        int totalSpace = type.getTotalSpace(handler, copy);
                        // if the items on the way plus the items we're trying to move are too much, reduce
                        if (onTheWay + type.getCount(toInsert) > totalSpace) {
                            type.setCount(toInsert, totalSpace - onTheWay);
                        }
                    }
                }
                // we return the item that can actually be inserted, NOT the remainder!
                if (type.getCount(toInsert) != 0) {
                    return Pair.of(offset, toInsert);
                }
            }

        }
        return null;
    }
}
