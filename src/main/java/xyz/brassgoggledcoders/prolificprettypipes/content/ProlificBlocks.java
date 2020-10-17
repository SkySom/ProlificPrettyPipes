package xyz.brassgoggledcoders.prolificprettypipes.content;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.brassgoggledcoders.prolificprettypipes.ProlificPrettyPipes;
import xyz.brassgoggledcoders.prolificprettypipes.block.TestTankBlock;
import xyz.brassgoggledcoders.prolificprettypipes.tileentity.TestTankTileEntity;

public class ProlificBlocks {
    public static final BlockEntry<TestTankBlock> TEST_TANK = ProlificPrettyPipes.getRegistrate()
            .object("test_tank")
            .block(TestTankBlock::new)
            .simpleItem()
            .tileEntity(TestTankTileEntity::new)
            .build()
            .register();

    public static final RegistryEntry<TileEntityType<TestTankTileEntity>> TEST_TANK_TILE_ENTITY =
            TEST_TANK.getSibling(ForgeRegistries.TILE_ENTITIES);


    public static void setup() {

    }
}
