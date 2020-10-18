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
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import xyz.brassgoggledcoders.prolificprettypipes.item.FluidItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

public class FluidModelItemOverrideList extends ItemOverrideList {
    private final LoadingCache<Fluid, IBakedModel> cache;

    private final Function<RenderMaterial, TextureAtlasSprite> spriteGetter;

    public FluidModelItemOverrideList(Function<RenderMaterial, TextureAtlasSprite> spriteGetter) {
        this.spriteGetter = spriteGetter;
        this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(60, TimeUnit.SECONDS)
                .build(new CacheLoader<Fluid, IBakedModel>() {
                    @Override
                    public IBakedModel load(@Nonnull Fluid key) {
                        return createFor(key);
                    }
                });
    }

    @Override
    @ParametersAreNonnullByDefault
    public IBakedModel getOverrideModel(IBakedModel bakedModel, ItemStack itemStack, @Nullable ClientWorld clientWorld,
                                        @Nullable LivingEntity livingEntity) {
        Fluid fluid = FluidItem.getFluidFromStack(itemStack);
        if (fluid != null) {
            return cache.getUnchecked(fluid);
        } else {
            return Minecraft.getInstance().getModelManager().getMissingModel();
        }
    }

    @Nonnull
    private IBakedModel createFor(@Nonnull Fluid fluid) {
        TextureAtlasSprite fluidSprite = spriteGetter.apply(ForgeHooksClient.getBlockMaterial(
                fluid.getAttributes().getStillTexture()));
        return new BakedItemModel(
                Stream.of(Direction.values())
                .map(direction -> createQuadForSide(direction, fluidSprite))
                .collect(ImmutableList.toImmutableList()),
                fluidSprite,
                ImmutableMap.<ItemCameraTransforms.TransformType, TransformationMatrix>builder().build(),
                new EmptyItemOverrides(),
                false,
                true
        );
    }

    private BakedQuad createQuadForSide(@Nonnull Direction side, TextureAtlasSprite fluidTexture) {
        return createBakedQuad(DefaultVertexFormats.BLOCK, getDefaultVertices(side),
                side, fluidTexture, new double[]{2, 2, 14, 14}, new float[]{1, 1, 1, 1},
                side.getAxisDirection() == Direction.AxisDirection.NEGATIVE, new float[]{1, 1, 1, 1});
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
                                            float[] alpha) {
        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
        builder.setQuadOrientation(facing);
        builder.setApplyDiffuseLighting(true);
        Vector3i normalInt = facing.getDirectionVec();
        Vector3d faceNormal = new Vector3d(normalInt.getX(), normalInt.getY(), normalInt.getZ());
        int vId = invert ? 3 : 0;
        int u = vId > 1 ? 2 : 0;
        putVertexData(format, builder, vertices[vId], faceNormal, uvs[u], uvs[1], sprite, colour, alpha[vId]);
        vId = invert ? 2 : 1;
        u = vId > 1 ? 2 : 0;
        putVertexData(format, builder, vertices[vId], faceNormal, uvs[u], uvs[3], sprite, colour, alpha[vId]);
        vId = invert ? 1 : 2;
        u = vId > 1 ? 2 : 0;
        putVertexData(format, builder, vertices[vId], faceNormal, uvs[u], uvs[3], sprite, colour, alpha[vId]);
        vId = invert ? 0 : 3;
        u = vId > 1 ? 2 : 0;
        putVertexData(format, builder, vertices[vId], faceNormal, uvs[u], uvs[1], sprite, colour, alpha[vId]);
        return builder.build();
    }

    public static void putVertexData(VertexFormat format, BakedQuadBuilder builder, Vector3d pos, Vector3d faceNormal,
                                     double u, double v, TextureAtlasSprite sprite, float[] colour, float alpha) {
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
                    } else
                        //Lightmap UVs (0, 0 is "automatic")
                        builder.put(e, 0, 0);
                    break;
                case NORMAL:
                    builder.put(e, (float) faceNormal.getX(), (float) faceNormal.getY(), (float) faceNormal.getZ());
                    break;
                default:
                    builder.put(e);
            }
    }
}
