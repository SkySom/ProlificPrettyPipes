package xyz.brassgoggledcoders.prolificprettypipes.content;

import com.tterrag.registrate.util.entry.RegistryEntry;
import de.ellpeck.prettypipes.items.ModuleTier;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xyz.brassgoggledcoders.prolificprettypipes.ProlificPrettyPipes;
import xyz.brassgoggledcoders.prolificprettypipes.item.BasicExtractionModule;
import xyz.brassgoggledcoders.prolificprettypipes.item.FluidItem;
import xyz.brassgoggledcoders.prolificprettypipes.pipe.FluidPipeType;

public class ProlificItems {
    public static final RegistryEntry<FluidItem> FLUID = ProlificPrettyPipes.getRegistrate()
            .object("fluid")
            .item(FluidItem::new)
            .register();

    public static final RegistryEntry<BasicExtractionModule<FluidPipeType, FluidStack, IFluidHandler>> FLUID_EXTRACTION_MODULE =
            ProlificPrettyPipes.getRegistrate()
                    .object("fluid_extraction_module")
                    .item(properties -> new BasicExtractionModule<>(properties, FluidPipeType.INSTANCE, ModuleTier.LOW))
                    .register();

    public static void setup() {

    }
}
