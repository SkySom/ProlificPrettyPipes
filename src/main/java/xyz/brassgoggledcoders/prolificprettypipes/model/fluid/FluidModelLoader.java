package xyz.brassgoggledcoders.prolificprettypipes.model.fluid;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.IModelLoader;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class FluidModelLoader implements IModelLoader<FluidItemModel> {
    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {

    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public FluidItemModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return new FluidItemModel();
    }
}
