package com.lothrazar.storagenetwork.block.exchange;

import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.api.IConnectable;
import com.lothrazar.storagenetwork.block.TileConnectable;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileExchange extends TileConnectable {

  private ExchangeItemStackHandler itemHandler;

  public TileExchange(BlockPos pos, BlockState state) {
    super(SsnRegistry.Tiles.EXCHANGE.get(), pos, state);
    itemHandler = new ExchangeItemStackHandler();
  }

  @Override
  public void load(CompoundTag compound) {
    super.load(compound);
  }

  @Override
  public void saveAdditional(CompoundTag compound) {
    super.saveAdditional(compound);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      try {
        IConnectable capabilityConnectable = super.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, side).orElse(null);
        DimPos m = getMain();
        if (capabilityConnectable != null && m != null
            && itemHandler != null &&
            itemHandler.tileMain == null) {
          TileMain tileMain = m.getTileEntity(TileMain.class);
          if (tileMain != null) {
            itemHandler.setMain(tileMain);
          }
        }
        return LazyOptional.of(new NonNullSupplier<T>() {

          public @Override T get() {
            return (T) itemHandler;
          }
        });
      }
      catch (Exception e) {
        StorageNetworkMod.LOGGER.error("Exchange caught error from a mod", e);
      }
    }
    return super.getCapability(cap, side);
  }

  private void tick() {
    if (this.itemHandler != null && getLevel().getGameTime() % StorageNetworkMod.CONFIG.refreshTicks() == 0) {
      this.itemHandler.update();
    }
  }

  public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileExchange tile) {
    tile.tick();
  }

  public static <E extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileExchange tile) {
    tile.tick();
  }
}
