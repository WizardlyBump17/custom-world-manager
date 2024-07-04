package com.wizardlybump17.customworldmanager.nms;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalLong;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class DimensionTypeBuilder {

    private @Nullable Long fixedTime = null;
    private boolean hasSkyLight = true;
    private boolean hasCeiling = false;
    private boolean ultraWarm = false;
    private boolean natural = true;
    private double coordinateScale = 1;
    private boolean bedWorks = true;
    private boolean respawnAnchorWorks = false;
    private int minY = -64;
    private int height = 384;
    private int logicalHeight = 384;
    private TagKey<Block> infiniburn = BlockTags.INFINIBURN_OVERWORLD;
    private ResourceLocation effectsLocation = BuiltinDimensionTypes.OVERWORLD_EFFECTS;
    private float ambientLight = 0;
    private DimensionType.MonsterSettings monsterSettings = new DimensionType.MonsterSettings(
            false,
            false,
            UniformInt.of(0, 7),
            0
    );

    public @NonNull DimensionType build() {
        return new DimensionType(
                fixedTime == null ? OptionalLong.empty() : OptionalLong.of(fixedTime),
                hasSkyLight,
                hasCeiling,
                ultraWarm,
                natural,
                coordinateScale,
                bedWorks,
                respawnAnchorWorks,
                minY,
                height,
                logicalHeight,
                infiniburn,
                effectsLocation,
                ambientLight,
                monsterSettings
        );
    }
}
