package io.github.alkyaly.orehalting;

import io.github.alkyaly.orehalting.block.OreHalterBlock;
import io.github.alkyaly.orehalting.block.OreHalterBlockEntity;
import io.github.alkyaly.orehalting.gui.OreHalterScreenHandler;
import io.github.alkyaly.orehalting.recipe.HaltingRecipe;
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

public class OreHalting implements ModInitializer {

    public static final String MOD_ID = "orehalting";
    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static final RecipeType<HaltingRecipe> HALTING_RECIPE = new RecipeType<HaltingRecipe>() {
        @Override
        public <C extends Inventory> Optional<HaltingRecipe> get(Recipe<C> recipe, World world, C inventory) {
            return recipe.matches(inventory, world) ? Optional.of((HaltingRecipe) recipe) : Optional.empty();
        }
    };
    public static final HaltingRecipe.HaltingRecipeSerializer HALTING_RECIPE_SERIALIZER = new HaltingRecipe.HaltingRecipeSerializer();
    public static final Block ORE_HALTER_BLOCK = new OreHalterBlock(FabricBlockSettings.of(Material.METAL).strength(2f).sounds(BlockSoundGroup.STONE).breakByTool(FabricToolTags.PICKAXES, 1).breakByHand(false));
    public static final BlockEntityType<OreHalterBlockEntity> ORE_HALTER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(
            OreHalterBlockEntity::new, ORE_HALTER_BLOCK).build(null);
    public static final ScreenHandlerType<OreHalterScreenHandler> ORE_HALTER_SCREEN_HANDLER;

    @Override
    public void onInitialize() {
        LogManager.getLogger("Ore Halting").info("Why would you install this? \uD83E\uDD80");
        Registry.register(Registry.RECIPE_TYPE, id("halting"), HALTING_RECIPE);
        Registry.register(Registry.RECIPE_SERIALIZER, id("halting"), HALTING_RECIPE_SERIALIZER);
        Registry.register(Registry.BLOCK, id("ore_halter"), ORE_HALTER_BLOCK);
        Registry.register(Registry.ITEM, id("ore_halter"), new BlockItem(ORE_HALTER_BLOCK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("ore_halter"), ORE_HALTER_BLOCK_ENTITY);
    }

    static {
        ORE_HALTER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id("ore_halter"), OreHalterScreenHandler::new);
    }
}
