package com.enderio.base.common.block.glass;

import com.enderio.base.EnderIO;
import com.enderio.base.common.integration.IntegrationManager;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.base.common.tag.EIOTags;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Container helper for the fused glass/quartz blocks as theres a lot, and this will tidy stuff up.
 */
public class GlassBlocks {
    public final BlockEntry<FusedQuartzBlock> CLEAR;

    @Nullable
    public BlockEntry<FusedQuartzBlock> WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK;

    private final GlassCollisionPredicate collisionPredicate;

    private final boolean explosionResistant;

    private final GlassLighting glassLighting;

    /**
     * Create the entire color family for this configuration of fused glass.
     */
    public GlassBlocks(Registrate registrate, GlassIdentifier identifier) {
        this.collisionPredicate = identifier.collisionPredicate();
        this.glassLighting = identifier.lighting();
        this.explosionResistant = identifier.explosion_resistance();
        String name = createGlassName(identifier);
        String english = createEnglishGlassName(identifier);
        CLEAR = register(registrate, name, english);
        IntegrationManager.DECORATION.ifPresent(
            dummyIntegration -> {
                Registrate decorationRegistrate = dummyIntegration.registrate();
                WHITE = register(decorationRegistrate, name.concat("_white"), "White ".concat(english), DyeColor.WHITE);
                ORANGE = register(decorationRegistrate, name.concat("_orange"), "Orange ".concat(english), DyeColor.ORANGE);
                MAGENTA = register(decorationRegistrate, name.concat("_magenta"), "Magenta ".concat(english), DyeColor.MAGENTA);
                LIGHT_BLUE = register(decorationRegistrate, name.concat("_light_blue"), "Light Blue ".concat(english), DyeColor.LIGHT_BLUE);
                YELLOW = register(decorationRegistrate, name.concat("_yellow"), "Yellow ".concat(english), DyeColor.YELLOW);
                LIME = register(decorationRegistrate, name.concat("_lime"), "Lime ".concat(english), DyeColor.LIME);
                PINK = register(decorationRegistrate, name.concat("_pink"), "Pink ".concat(english), DyeColor.PINK);
                GRAY = register(decorationRegistrate, name.concat("_gray"), "Gray ".concat(english), DyeColor.GRAY);
                LIGHT_GRAY = register(decorationRegistrate, name.concat("_light_gray"), "Light Gray ".concat(english), DyeColor.LIGHT_GRAY);
                CYAN = register(decorationRegistrate, name.concat("_cyan"), "Cyan ".concat(english), DyeColor.CYAN);
                PURPLE = register(decorationRegistrate, name.concat("_purple"), "Purple ".concat(english), DyeColor.PURPLE);
                BLUE = register(decorationRegistrate, name.concat("_blue"), "Blue ".concat(english), DyeColor.BLUE);
                BROWN = register(decorationRegistrate, name.concat("_brown"), "Brown ".concat(english), DyeColor.BROWN);
                GREEN = register(decorationRegistrate, name.concat("_green"), "Green ".concat(english), DyeColor.GREEN);
                RED = register(decorationRegistrate, name.concat("_red"), "Red ".concat(english), DyeColor.RED);
                BLACK = register(decorationRegistrate, name.concat("_black"), "Black ".concat(english), DyeColor.BLACK);
            }
        );
    }

    private ResourceLocation getModelFile() {
        return explosionResistant ? EnderIO.loc("block/fused_quartz") : EnderIO.loc("block/clear_glass");
    }

    private static String createGlassName(GlassIdentifier identifier) {
        StringBuilder main = new StringBuilder();
        if (identifier.explosion_resistance()) {
            main.append("fused_quartz");
        } else {
            main.append("clear_glass");
        }
        StringBuilder modifier = new StringBuilder();
        modifier.append(identifier.lighting().shortName());
        modifier.append(identifier.collisionPredicate().shortName());
        if (modifier.length() != 0) {
            main.append("_");
            main.append(modifier);
        }
        return main.toString();
    }
    private static String createEnglishGlassName(GlassIdentifier identifier) {
        StringBuilder main = new StringBuilder();
        if (identifier.lighting() != GlassLighting.NONE) {
            main.append(identifier.lighting().englishName());
            main.append(" ");
        }
        if (identifier.explosion_resistance()) {
            main.append("Fused Quartz");
        } else {
            main.append("Clear Glass");
        }
        return main.toString();
    }
    // Dirty dirty. TODO: Just access transforms for these in Blocks??
    private static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {
        return false;
    }

    private static boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_, EntityType<?> p_50782_) {
        return false;
    }

    /**
     * Register a non-colored glass
     */
    private BlockEntry<FusedQuartzBlock> register(Registrate registrate, String name, String english) {
        return registrate
            .block(name, props -> new FusedQuartzBlock(props, collisionPredicate, glassLighting, explosionResistant))
            .tag(explosionResistant ? EIOTags.Blocks.FUSED_QUARTZ : EIOTags.Blocks.CLEAR_GLASS)
            .lang(english)
            .blockstate((con, prov) -> prov.simpleBlock(con.get(), prov.models().getExistingFile(getModelFile())))
            .addLayer(() -> RenderType::cutout)
            .properties(props -> props
                .noOcclusion()
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(GlassBlocks::never)
                .isRedstoneConductor(GlassBlocks::never)
                .isSuffocating(GlassBlocks::never)
                .isViewBlocking(GlassBlocks::never))
            .item(FusedQuartzItem::new)
            .tab(() -> EIOCreativeTabs.BLOCKS)
            .tag(explosionResistant ? EIOTags.Items.FUSED_QUARTZ : EIOTags.Items.CLEAR_GLASS)
            .build()
            .register();
    }

    /**
     * Register a colored glass.
     */
    private BlockEntry<FusedQuartzBlock> register(Registrate registrate, String name, String english, DyeColor color) {
        return registrate
            .block(name, props -> new FusedQuartzBlock(props, collisionPredicate, glassLighting, explosionResistant))
            .lang(english)
            .blockstate((con, prov) -> prov.simpleBlock(con.get(), prov.models().getExistingFile(getModelFile())))
            .addLayer(() -> RenderType::cutout)
            .color(() -> () -> (p_92567_, p_92568_, p_92569_, p_92570_) -> color.getMaterialColor().col)
            .properties(props -> props
                .noOcclusion()
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(GlassBlocks::never)
                .isRedstoneConductor(GlassBlocks::never)
                .isSuffocating(GlassBlocks::never)
                .isViewBlocking(GlassBlocks::never)
                .color(color.getMaterialColor()))
            .item(FusedQuartzItem::new)
            .tab(() -> EIOCreativeTabs.BLOCKS)
            .color(() -> () -> (ItemColor) (p_92672_, p_92673_) -> color.getMaterialColor().col)
            .build()
            .register();
    }
}
