package xyz.brassgoggledcoders.prolificprettypipes.pipe;

import de.ellpeck.prettypipes.pipe.ConnectionType;
import de.ellpeck.prettypipes.pipe.IPipeConnectable;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class PipeConnectable implements IPipeConnectable {
    @Override
    public ConnectionType getConnectionType(BlockPos blockPos, Direction direction) {
        return ConnectionType.CONNECTED;
    }

    @Override
    public boolean allowsModules(BlockPos pipePos, Direction direction) {
        return true;
    }
}
