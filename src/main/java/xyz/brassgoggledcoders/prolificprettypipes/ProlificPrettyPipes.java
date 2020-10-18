package xyz.brassgoggledcoders.prolificprettypipes;

import com.tterrag.registrate.Registrate;
import de.ellpeck.prettypipes.Registry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.brassgoggledcoders.prolificprettypipes.content.ProlificBlocks;
import xyz.brassgoggledcoders.prolificprettypipes.content.ProlificItems;
import xyz.brassgoggledcoders.prolificprettypipes.model.fluid.FluidModelLoader;

@Mod(ProlificPrettyPipes.ID)
public class ProlificPrettyPipes {
    public static final String ID = "prolific_pretty_pipes";

    public static final NonNullLazy<Registrate> REGISTRATE = NonNullLazy.of(() -> Registrate.create(ID)
            .itemGroup(() -> Registry.GROUP)
    );

    public ProlificPrettyPipes() {
        ProlificItems.setup();
        ProlificBlocks.setup();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modelLoader);
    }

    public void modelLoader(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(ProlificPrettyPipes.rl("fluid"), new FluidModelLoader());
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(ID, path);
    }

    public static Registrate getRegistrate() {
        return REGISTRATE.get();
    }
}
