package io.github.alkyaly.orehalving.block;

import io.github.alkyaly.orehalving.OreHalving;
import io.github.alkyaly.orehalving.gui.TheOneThatHalvesScreenHandler;
import io.github.alkyaly.orehalving.recipe.HalvingRecipe;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TheOneThatHalvesBlockEntity extends BlockEntity implements SidedInventory, NamedScreenHandlerFactory {

    private int processingTime;
    public DefaultedList<ItemStack> inventory;
    private final RecipeType<HalvingRecipe> recipeType;
    private final PropertyDelegate propertyDelegate;

    public TheOneThatHalvesBlockEntity(BlockPos pos, BlockState state) {
        super(OreHalving.THE_ONE_THAT_HALVES_BLOCK_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.recipeType = OreHalving.HALVING_RECIPE;
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return TheOneThatHalvesBlockEntity.this.processingTime;
            }

            @Override
            public void set(int index, int value) {
                TheOneThatHalvesBlockEntity.this.processingTime = value;
            }

            @Override
            public int size() {
                return 1;
            }
        };
    }


    public static void tick(World world, BlockPos pos, BlockState state, TheOneThatHalvesBlockEntity blockEntity) {
        if (world == null || world.isClient) return;
        if (blockEntity.isWorking()) {
            if (canCraft(blockEntity.inventory) && blockEntity.processingTime == 1) {
                Recipe<?> recipe = world.getRecipeManager().getFirstMatch(blockEntity.recipeType, blockEntity, world).orElse(null);
                craft(recipe, blockEntity.inventory);
                markDirty(world, pos, state);
            } else if (!canCraft(blockEntity.inventory)) {
                blockEntity.processingTime = 0;
                markDirty(world, pos, state);
            }

            state = state.with(TheOneThatHalvesBlock.ACTIVE, true);
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
            markDirty(world, pos, state);

            if (!world.isReceivingRedstonePower(pos)) {
                blockEntity.processingTime--;
            }

        } else if (canCraft(blockEntity.inventory)) {
            blockEntity.processingTime = 200;
            markDirty(world, pos, state);
        }

        if (!blockEntity.isWorking() && !canCraft(blockEntity.inventory)) {
            state = state.with(TheOneThatHalvesBlock.ACTIVE, false);
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
            markDirty(world, pos, state);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("processingTime", this.processingTime);
        Inventories.writeNbt(nbt, this.inventory);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.inventory.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.processingTime = nbt.getInt("processingTime");
    }

    public boolean isWorking() {
        return processingTime >= 0;
    }

    public static boolean canCraft(DefaultedList<ItemStack> inventory) {
        ItemStack stack = inventory.get(1);
        return stack.getCount() >= 2 && stack.isIn(TagRegistry.item(new Identifier("c", "raw_ores")));
    }

    public static void craft(Recipe<?> recipe, DefaultedList<ItemStack> inventory) {
        if (recipe != null) {
            ItemStack stack = inventory.get(1);
            ItemStack outputSlot = inventory.get(0);
            ItemStack output = recipe.getOutput();
            if (outputSlot.isEmpty()) {
                inventory.set(0, output.copy());
            } else if (outputSlot.isOf(output.getItem())) {
                outputSlot.increment(1);
            }

            stack.decrement(2);

        }
    }


    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return new int[] {0};
        } else {
            return new int[] {1};
        }
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return stack.isIn(TagRegistry.item(new Identifier("c", "raw_ores")));
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot == 0;
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        //return this.inventory.get(slot);
        return slot >= 0 && slot < this.inventory.size() ? this.inventory.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.inventory.size()) {
            this.inventory.set(slot, stack);
        }
    }

    
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        //noinspection ConstantConditions
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new TheOneThatHalvesScreenHandler(syncId, inv, this, this.propertyDelegate);
    }
}
