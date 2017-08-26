package com.buuz135.industrial.tile.misc;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.LockedInventoryTogglePiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.inventory.LockableItemHandler;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

import javax.annotation.Nonnull;
import java.util.List;

public class BlackHoleControllerTile extends SidedTileEntity {

    private LockableItemHandler input;
    private ItemStackHandler storage;
    private LockableItemHandler output;
    private BlackHoleControllerHandler itemHandler = new BlackHoleControllerHandler(this);

    public BlackHoleControllerTile() {
        super(BlackHoleControllerTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        input = new LockableItemHandler(9) {
            @Override
            protected void onContentsChanged(int slot) {
                BlackHoleControllerTile.this.markDirty();
            }
        };
        this.addInventory(new ColoredItemHandler(input, EnumDyeColor.BLUE, "Input items", new BoundingRectangle(15, 18, 9 * 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                if (stack.getItem().equals(Item.getItemFromBlock(BlockRegistry.blackHoleUnitBlock))) return false;
                if (storage.getStackInSlot(slot).isEmpty()) return false;
                ItemStack contained = BlockRegistry.blackHoleUnitBlock.getItemStack(storage.getStackInSlot(slot));
                if (stack.isItemEqual(contained)) return true;
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return super.canExtractItem(slot);
            }
        });
        this.addInventoryToStorage(input, "input");
        storage = new ItemStackHandler(9) {
            @Override
            protected void onContentsChanged(int slot) {
                BlackHoleControllerTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.addInventory(new ColoredItemHandler(storage, EnumDyeColor.YELLOW, "Black hole units", new BoundingRectangle(15, 22 + 18, 9 * 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem().equals(Item.getItemFromBlock(BlockRegistry.blackHoleUnitBlock));
            }

            @Override
            public boolean canExtractItem(int slot) {
                return super.canExtractItem(slot);
            }
        });
        this.addInventoryToStorage(storage, "storage");
        output = new LockableItemHandler(9) {
            @Override
            protected void onContentsChanged(int slot) {
                BlackHoleControllerTile.this.markDirty();
            }
        };
        this.addInventory(new ColoredItemHandler(output, EnumDyeColor.ORANGE, "Output items", new BoundingRectangle(15, 27 + 18 * 2, 9 * 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }
        });
        this.addInventoryToStorage(output, "output");

    }

    @Override
    protected boolean supportsAddons() {
        return false;
    }

    @Override
    protected void innerUpdate() {
        if (WorkUtils.isDisabled(this.getBlockType())) return;
        input.setLocked(output.getLocked());
        input.setFilter(output.getFilter());
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = storage.getStackInSlot(i);
            if (!stack.isEmpty()) {
                int amount = BlockRegistry.blackHoleUnitBlock.getAmount(stack);
                ItemStack s = BlockRegistry.blackHoleUnitBlock.getItemStack(stack);
                if (!s.isEmpty()) {
                    ItemStack in = input.getStackInSlot(i);
                    if (!in.isEmpty() && in.getCount() + amount < Integer.MAX_VALUE) {
                        BlockRegistry.blackHoleUnitBlock.setAmount(stack, amount + in.getCount());
                        in.setCount(0);
                        continue;
                    }
                    ItemStack out = output.getStackInSlot(i);
                    if (out.isEmpty()) { // Slot is empty
                        out = s.copy();
                        out.setCount(Math.min(amount, 64));
                        BlockRegistry.blackHoleUnitBlock.setAmount(stack, amount - out.getCount());
                        output.setStackInSlot(i, out);
                        continue;
                    }
                    if (out.getCount() < out.getMaxStackSize()) {
                        int increase = Math.min(amount, out.getMaxStackSize() - out.getCount());
                        out.setCount(out.getCount() + increase);
                        BlockRegistry.blackHoleUnitBlock.setAmount(stack, amount - increase);
                        continue;
                    }
                }
            }
        }
    }

    public ItemStackHandler getInput() {
        return input;
    }

    public ItemStackHandler getStorage() {
        return storage;
    }

    public ItemStackHandler getOutput() {
        return output;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == null) return false;
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) itemHandler;
        return super.getCapability(capability, facing);
    }

    public void dropItems() {
        for (ItemStackHandler items : new ItemStackHandler[]{input, storage, output}) {
            for (int i = 0; i < items.getSlots(); ++i) {
                ItemStack stack = items.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    InventoryHelper.spawnItemStack(this.getWorld(), pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer<?> container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new LockedInventoryTogglePiece(18 * 8 + 9, 83, this, EnumDyeColor.ORANGE));
        return pieces;
    }

    private class BlackHoleControllerHandler implements IItemHandler {

        private BlackHoleControllerTile tile;

        public BlackHoleControllerHandler(BlackHoleControllerTile tile) {
            this.tile = tile;
        }

        @Override
        public int getSlots() {
            int slots = 0;
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = tile.getStorage().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    int amount = BlockRegistry.blackHoleUnitBlock.getAmount(stack) + tile.getOutput().getStackInSlot(i).getCount();
                    ItemStack s = BlockRegistry.blackHoleUnitBlock.getItemStack(stack);
                    slots += Math.ceil(amount / (double) s.getMaxStackSize());
                }
            }
            return slots;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            int slots = 0;
            for (int i = 0; i < 9; ++i) {
                ItemStack hole = tile.getStorage().getStackInSlot(i);
                if (!hole.isEmpty()) {
                    int amount = BlockRegistry.blackHoleUnitBlock.getAmount(hole) + tile.getOutput().getStackInSlot(i).getCount();
                    ItemStack s = BlockRegistry.blackHoleUnitBlock.getItemStack(hole);
                    double toAdd = (amount / (double) s.getMaxStackSize());
                    if (slot >= slots && slot < slots + toAdd) {
                        ItemStack stack = s.copy();
                        int z = slot - slots;
                        stack.setCount(z < (int) toAdd ? s.getMaxStackSize() : z == (int) toAdd ? (int) ((toAdd - (int) toAdd) * s.getMaxStackSize()) : 0);
                        return stack;
                    }
                    slots += Math.ceil(toAdd);
                }
            }
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            for (int i = 0; i < 9; ++i) {
                ItemStack contained = BlockRegistry.blackHoleUnitBlock.getItemStack(storage.getStackInSlot(i));
                if (stack.isItemEqual(contained)) {
                    return tile.getInput().insertItem(i, stack, simulate);
                }
            }
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            int slots = 0;
            for (int i = 0; i < 9; ++i) {
                ItemStack hole = tile.getStorage().getStackInSlot(i);
                if (!hole.isEmpty()) {
                    ItemStack s = BlockRegistry.blackHoleUnitBlock.getItemStack(hole);
                    int a = BlockRegistry.blackHoleUnitBlock.getAmount(hole) + tile.getOutput().getStackInSlot(i).getCount();
                    double toAdd = Math.ceil(a / (double) s.getMaxStackSize());
                    if (slots == slot) {
                        slots = i;
                        break;
                    }
                    slots += toAdd;
                }
            }
            return tile.getOutput().extractItem(slots, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }
    }

    @Override
    public boolean getAllowRedstoneControl() {
        return false;
    }

    @Override
    protected boolean getShowPauseDrawerPiece() {
        return false;
    }

}