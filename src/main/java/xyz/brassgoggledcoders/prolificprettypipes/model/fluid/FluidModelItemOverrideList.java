package xyz.brassgoggledcoders.prolificprettypipes.model.fluid;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.fluids.FluidStack;
import xyz.brassgoggledcoders.prolificprettypipes.item.FluidItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class FluidModelItemOverrideList extends ItemOverrideList {
    private final LoadingCache<FluidStack, IBakedModel> cache;

    private final Function<RenderMaterial, TextureAtlasSprite> spriteGetter;

    public FluidModelItemOverrideList(Function<RenderMaterial, TextureAtlasSprite> spriteGetter) {
        this.spriteGetter = spriteGetter;
        this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(60, TimeUnit.SECONDS)
                .build(new CacheLoader<FluidStack, IBakedModel>() {
                    @Override
                    public IBakedModel load(@Nonnull FluidStack key) {
                        return createFor(key);
                    }
                });
    }

    @Override
    @ParametersAreNonnullByDefault
    public IBakedModel getOverrideModel(IBakedModel bakedModel, ItemStack itemStack, @Nullable ClientWorld clientWorld,
                                        @Nullable LivingEntity livingEntity) {
        FluidStack fluid = FluidItem.getFluidStackFromStack(itemStack);
        if (fluid != null) {
            return cache.getUnchecked(fluid);
        } else {
            return Minecraft.getInstance().getModelManager().getMissingModel();
        }
    }

    @Nonnull
    private IBakedModel createFor(@Nonnull FluidStack fluidStack) {
        TextureAtlasSprite fluidSprite = spriteGetter.apply(ForgeHooksClient.getBlockMaterial(
                fluidStack.getFluid().getAttributes().getStillTexture()));
        int color = fluidStack.getFluid().getAttributes().getColor(fluidStack);
        int luminosity = fluidStack.getFluid().getAttributes().getLuminosity(fluidStack);

        float r = ((color >> 16) & 0xFF) / 255f; // red
        float g = ((color >> 8) & 0xFF) / 255f; // green
        float b = ((color) & 0xFF) / 255f; // blue
        float a = ((color >> 24) & 0xFF) / 255f; // alpha
        float[] colors = new float[]{r, g, b, a};

        ImmutableList.Builder<BakedQuad> bakedQuadList = ImmutableList.builder();
        for (Direction direction : Direction.values()) {
            bakedQuadList.add(createQuadForSide(direction, fluidSprite, colors, luminosity));
        }

        return new BakedItemModel(
                bakedQuadList.build(),
                fluidSprite,
                ImmutableMap.<ItemCameraTransforms.TransformType, TransformationMatrix>builder().build(),
                new EmptyItemOverrides(),
                false,
                true
        );
    }

    private BakedQuad createQuadForSide(@Nonnull Direction side, TextureAtlasSprite fluidTexture, float[] color,
                                        int luminosity) {
        return createBakedQuad(DefaultVertexFormats.BLOCK, this.getDefaultVertices(side), side, fluidTexture,
                new double[]{0, 0, 16, 16}, color, side.getAxisDirection() == Direction.AxisDirection.NEGATIVE,
                new float[]{1, 1, 1, 1}, luminosity);
    }

    private Vector3d[] getDefaultVertices(Direction facing) {
        Vector3d[] vertices = new Vector3d[4];
        switch (facing) {
            case DOWN:
                vertices[0] = new Vector3d(0.125F, 0, 0.125F);
                vertices[1] = new Vector3d(0.125F, 0, 0.875F);
                vertices[2] = new Vector3d(0.875F, 0, 0.875F);
                vertices[3] = new Vector3d(0.875F, 0, 0.125F);
                break;
            case UP:
                vertices[0] = new Vector3d(0.125F, 1, 0.125F);
                vertices[1] = new Vector3d(0.125F, 1, 0.875F);
                vertices[2] = new Vector3d(0.875F, 1, 0.875F);
                vertices[3] = new Vector3d(0.875F, 1, 0.125F);
                break;
            case NORTH:
                vertices[0] = new Vector3d(0.125F, 0.875F, 0);
                vertices[1] = new Vector3d(0.125F, 0.125F, 0);
                vertices[2] = new Vector3d(0.875F, 0.125F, 0);
                vertices[3] = new Vector3d(0.875F, 0.875F, 0);
                break;
            case EAST:
                vertices[0] = new Vector3d(1, 0.875F, 0.875F);
                vertices[1] = new Vector3d(1, 0.125F, 0.875F);
                vertices[2] = new Vector3d(1, 0.125F, 0.125F);
                vertices[3] = new Vector3d(1, 0.875F, 0.125F);
                break;
            case SOUTH:
                vertices[0] = new Vector3d(0.125F, 0.875F, 1);
                vertices[1] = new Vector3d(0.125F, 0.125F, 1);
                vertices[2] = new Vector3d(0.875F, 0.125F, 1);
                vertices[3] = new Vector3d(0.875F, 0.875F, 1);
                break;
            case WEST:
                vertices[0] = new Vector3d(0, 0.875F, 0.875F);
                vertices[1] = new Vector3d(0, 0.125F, 0.875F);
                vertices[2] = new Vector3d(0, 0.125F, 0.125F);
                vertices[3] = new Vector3d(0, 0.875F, 0.125F);
                break;
        }
        return vertices;
    }

    public static BakedQuad createBakedQuad(VertexFormat format, Vector3d[] vertices, Direction facing,
                                            TextureAtlasSprite sprite, double[] uvs, float[] colour, boolean invert,
                                            float[] alpha, int luminosity) {
        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
        builder.setQuadOrientation(facing);
        builder.setApplyDiffuseLighting(true);
        Vector3i normalInt = facing.getDirectionVec();
        Vector3d faceNormal = new Vector3d(normalInt.getX(), normalInt.getY(), normalInt.getZ());
        int vId = invert ? 3 : 0;
        int u = vId > 1 ? 2 : 0;
        putVertexData(format, builder, vertices[vId], faceNormal, uvs[u], uvs[1], sprite, colour, alpha[vId], luminosity);
        vId = invert ? 2 : 1;
        u = vId > 1 ? 2 : 0;
        putVertexData(format, builder, vertices[vId], faceNormal, uvs[u], uvs[3], sprite, colour, alpha[vId], luminosity);
        vId = invert ? 1 : 2;
        u = vId > 1 ? 2 : 0;
        putVertexData(format, builder, vertices[vId], faceNormal, uvs[u], uvs[3], sprite, colour, alpha[vId], luminosity);
        vId = invert ? 0 : 3;
        u = vId > 1 ? 2 : 0;
        putVertexData(format, builder, vertices[vId], faceNormal, uvs[u], uvs[1], sprite, colour, alpha[vId], luminosity);
        return builder.build();
    }

    public static void putVertexData(VertexFormat format, BakedQuadBuilder builder, Vector3d pos, Vector3d faceNormal,
                                     double u, double v, TextureAtlasSprite sprite, float[] colour, float alpha,
                                     int luminosity) {
        for (int e = 0; e < format.getElements().size(); e++)
            switch (format.getElements().get(e).getUsage()) {
                case POSITION:
                    builder.put(e, (float) pos.x, (float) pos.y, (float) pos.z);
                    break;
                case COLOR:
                    float d = 1;//LightUtil.diffuseLight(faceNormal.x, faceNormal.y, faceNormal.z);
                    builder.put(e, d * colour[0], d * colour[1], d * colour[2], 1 * colour[3] * alpha);
                    break;
                case UV:
                    if (format.getElements().get(e).getType() == VertexFormatElement.Type.FLOAT) {
                        // Actual UVs
                        if (sprite == null)//Double Safety. I have no idea how it even happens, but it somehow did .-.
                            sprite = Minecraft.getInstance()
                                    .getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE)
                                    .apply(MissingTextureSprite.getLocation());
                        builder.put(e, sprite.getInterpolatedU(u), sprite.getInterpolatedV(v));
                    } else {
                        builder.put(e, (luminosity << 4) / 32768.0f, (luminosity << 4) / 32768.0f, 0f, 1f);
                    }
                    break;
                case NORMAL:
                    builder.put(e, (float) faceNormal.getX(), (float) faceNormal.getY(), (float) faceNormal.getZ());
                    break;
                default:
                    builder.put(e);
            }
    }
}
