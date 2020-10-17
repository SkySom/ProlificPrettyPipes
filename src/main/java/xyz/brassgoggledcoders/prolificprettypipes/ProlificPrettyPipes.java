package xyz.brassgoggledcoders.prolificprettypipes;

import com.tterrag.registrate.Registrate;
import de.ellpeck.prettypipes.Registry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.fml.common.Mod;
import xyz.brassgoggledcoders.prolificprettypipes.content.ProlificBlocks;
import xyz.brassgoggledcoders.prolificprettypipes.content.ProlificItems;

@Mod(ProlificPrettyPipes.ID)
public class ProlificPrettyPipes {
    public static final String ID = "prolific_pretty_pipes";

    public static final NonNullLazy<Registrate> REGISTRATE = NonNullLazy.of(() -> Registrate.create(ID)
            .itemGroup(() -> Registry.GROUP)
    );

    public ProlificPrettyPipes() {
        ProlificItems.setup();
        ProlificBlocks.setup();
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(ID, path);
    }

    public static Registrate getRegistrate() {
        return REGISTRATE.get();
    }
}
