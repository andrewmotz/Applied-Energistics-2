package appeng.worldgen;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;

import appeng.core.AEConfig;

/**
 * Extends a {@link ReplaceBlockConfig} with a chance.
 */
public class ChargedQuartzOreConfig implements IFeatureConfig {

    public static final Codec<ChargedQuartzOreConfig> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(BlockState.field_235877_b_.fieldOf("target").forGetter((config) -> config.target),
                    BlockState.field_235877_b_.fieldOf("state").forGetter((config) -> config.state),
                    Codec.FLOAT.fieldOf("chance").withDefault(0f).forGetter((config) -> config.chance))
            .apply(instance, ChargedQuartzOreConfig::new));

    public final BlockState target;
    public final BlockState state;
    public final float chance;

    public ChargedQuartzOreConfig(BlockState target, BlockState state, float chance) {
        this.target = target;
        this.state = state;
        this.chance = chance;
    }

}
