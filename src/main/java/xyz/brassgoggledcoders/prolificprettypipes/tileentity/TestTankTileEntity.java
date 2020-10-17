package xyz.brassgoggledcoders.prolificprettypipes.tileentity;

import de.ellpeck.prettypipes.Registry;
import de.ellpeck.prettypipes.pipe.IPipeConnectable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.lwjgl.system.CallbackI;
import xyz.brassgoggledcoders.prolificprettypipes.pipe.PipeConnectable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class TestTankTileEntity extends TileEntity {
    private final LazyOptional<IPipeConnectable> pipeConnectableLazy;
    private final LazyOptional<IFluidHandler> fluidTankLazy;
    private final FluidTank fluidTank;

    public TestTankTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
        this.fluidTank = new FluidTank(FluidAttributes.BUCKET_VOLUME * 16);
        this.fluidTankLazy = LazyOptional.of(() -> fluidTank);

        this.pipeConnectableLazy = LazyOptional.of(PipeConnectable::new);
    }

    public void onRightClick(PlayerEntity playerEntity, Hand hand) {
        if (!playerEntity.getEntityWorld().isRemote()) {
            ItemStack itemStack = playerEntity.getHeldItem(hand);
            if (itemStack.getItem() instanceof BucketItem) {
                Fluid fluid = ((BucketItem) itemStack.getItem()).getFluid();
                fluidTank.fill(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME), FluidAction.EXECUTE);
            } else {
                playerEntity.sendStatusMessage(new StringTextComponent("Fluid Level: " + fluidTank.getFluidAmount()), false);
                playerEntity.sendStatusMessage(new StringTextComponent("Fluid Name: " + fluidTank.getFluid()
                        .getDisplayName().getString()), false);
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? fluidTankLazy.cast() :
                cap == Registry.pipeConnectableCapability ? pipeConnectableLazy.cast() :
                        super.getCapability(cap, side);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        fluidTank.readFromNBT(nbt.getCompound("fluidTank"));
    }

    @Override
    @Nonnull
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        compound = super.write(compound);
        compound.put("fluidTank", fluidTank.writeToNBT(new CompoundNBT()));
        return compound;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {

    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        fluidTankLazy.invalidate();
    }
}
