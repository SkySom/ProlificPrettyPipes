package xyz.brassgoggledcoders.prolificprettypipes;

import com.tterrag.registrate.Registrate;
import de.ellpeck.prettypipes.Registry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.fml.common.Mod;

@Mod(ProlificPrettyPipes.ID)
public class ProlificPrettyPipes {
    public static final String ID = "prolific_pretty_pipes";

    public static final NonNullLazy<Registrate> REGISTRATE = NonNullLazy.of(() -> Registrate.create(ID)
            .itemGroup(() -> Registry.GROUP)
    );

    public ProlificPrettyPipes() {

    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(ID, path);
    }

    public static Registrate getRegistrate() {
        return REGISTRATE.get();
    }
}
