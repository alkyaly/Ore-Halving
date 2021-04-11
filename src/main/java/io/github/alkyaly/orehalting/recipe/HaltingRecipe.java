package io.github.alkyaly.orehalting.recipe;

import com.google.gson.JsonObject;
import io.github.alkyaly.orehalting.OreHalting;
import io.github.alkyaly.orehalting.block.OreHalterBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class HaltingRecipe implements Recipe<OreHalterBlockEntity> {

    private final Identifier id;
    private final Ingredient ingredient;
    private final ItemStack output;

    public HaltingRecipe(Identifier id, Ingredient ingredient, ItemStack output) {
        this.id = id;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean matches(OreHalterBlockEntity inv, World world) {
        return ingredient.test(inv.getStack(1));
    }

    @Override
    public ItemStack craft(OreHalterBlockEntity inv) {
        if (inv.getStack(1).getCount() >= 2) {
            return output;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return OreHalting.HALTING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return OreHalting.HALTING_RECIPE;
    }


    public static class HaltingRecipeSerializer implements RecipeSerializer<HaltingRecipe> {
        @Override
        public HaltingRecipe read(Identifier id, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(json, "ingredient"));
            String outputName = JsonHelper.getString(json, "result");
            ItemStack output = new ItemStack(Registry.ITEM.getOrEmpty(new Identifier(outputName)).orElseThrow(() ->
                new IllegalStateException("Item: " + outputName + " does not exist")
            ));
            return new HaltingRecipe(id, ingredient, output);
        }

        @Override
        public HaltingRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient ingredient = Ingredient.fromPacket(buf);
            ItemStack output = buf.readItemStack();

            return new HaltingRecipe(id, ingredient, output);
        }

        @Override
        public void write(PacketByteBuf buf, HaltingRecipe recipe) {
            recipe.ingredient.write(buf);
            buf.writeItemStack(recipe.output);
        }
    }
}
