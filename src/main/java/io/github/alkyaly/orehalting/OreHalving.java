package io.github.alkyaly.orehalting;

import io.github.alkyaly.orehalting.block.TheOneThatHalvesBlock;
import io.github.alkyaly.orehalting.block.TheOneThatHalvesBlockEntity;
import io.github.alkyaly.orehalting.gui.TheOneThatHalvesScreenHandler;
import io.github.alkyaly.orehalting.recipe.HalvingRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;

import java.util.Optional;

public class OreHalving implements ModInitializer {

    public static final String MOD_ID = "orehalving";
    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static final RecipeType<HalvingRecipe> HALVING_RECIPE = new RecipeType<HalvingRecipe>() {
        @Override
        public <C extends Inventory> Optional<HalvingRecipe> get(Recipe<C> recipe, World world, C inventory) {
            return recipe.matches(inventory, world) ? Optional.of((HalvingRecipe) recipe) : Optional.empty();
        }
    };
    public static final HalvingRecipe.HalvingRecipeSerializer HALVING_RECIPE_SERIALIZER = new HalvingRecipe.HalvingRecipeSerializer();
    public static final Block THE_ONE_THAT_HALVES_BLOCK = new TheOneThatHalvesBlock(FabricBlockSettings.of(Material.METAL).strength(3.5f).sounds(BlockSoundGroup.STONE)
            .breakByTool(FabricToolTags.PICKAXES, 1).breakByHand(false).luminance(state -> state.get(TheOneThatHalvesBlock.ACTIVE) ? 15 : 0));
    public static final BlockEntityType<TheOneThatHalvesBlockEntity> THE_ONE_THAT_HALVES_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(
            TheOneThatHalvesBlockEntity::new, THE_ONE_THAT_HALVES_BLOCK).build(null);
    public static final ScreenHandlerType<TheOneThatHalvesScreenHandler> THE_ONE_THAT_HALVES_SCREEN_HANDLER;

    @Override
    public void onInitialize() {
        LogManager.getLogger("Ore Halting").info("Why would you install this? \uD83E\uDD80");
        Registry.register(Registry.RECIPE_TYPE, id("halving"), HALVING_RECIPE);
        Registry.register(Registry.RECIPE_SERIALIZER, id("halving"), HALVING_RECIPE_SERIALIZER);
        Registry.register(Registry.BLOCK, id("the_one_that_halves"), THE_ONE_THAT_HALVES_BLOCK);
        Registry.register(Registry.ITEM, id("the_one_that_halves"), new BlockItem(THE_ONE_THAT_HALVES_BLOCK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("the_one_that_halves"), THE_ONE_THAT_HALVES_BLOCK_ENTITY);
    }

    static {
        THE_ONE_THAT_HALVES_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id("the_one_that_halves"), TheOneThatHalvesScreenHandler::new);
    }
}
