package com.wizardlybump17.customworldmanager.nms;

import com.mojang.serialization.Lifecycle;
import com.wizardlybump17.wlib.util.ReflectionUtil;
import lombok.NonNull;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.sounds.Music;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;

public class MemoryWorld extends ServerLevel {

    public static LevelStorageSource CUSTOM_LEVEL_STORAGE;

    static {
        try {
            Path path = Files.createTempDirectory("swm-" + UUID.randomUUID().toString().substring(0, 5)).toAbsolutePath();
            DirectoryValidator directoryvalidator = LevelStorageSource.parseValidator(path.resolve("allowed_symlinks.txt"));
            CUSTOM_LEVEL_STORAGE = new LevelStorageSource(path, path, directoryvalidator, DataFixers.getDataFixer());

            FileUtils.forceDeleteOnExit(path.toFile());
        } catch (IOException ex) {
            throw new IllegalStateException("Couldn't create dummy file directory.", ex);
        }
    }

    public static final @NotNull PrimaryLevelData DATA = new PrimaryLevelData(
            new LevelSettings(
                    String.valueOf(ThreadLocalRandom.current().nextInt()),
                    GameType.SURVIVAL,
                    false,
                    Difficulty.EASY,
                    false,
                    new GameRules(),
                    WorldDataConfiguration.DEFAULT
            ),
            new WorldOptions(0, false, false),
            PrimaryLevelData.SpecialWorldProperty.NONE,
            Lifecycle.stable()
    );
    public static final @NotNull DimensionType DIMENSION_TYPE = new DimensionTypeBuilder()
            .height(2032)
            .logicalHeight(2032)
            .minY(0)
            .build();
    public static final @NotNull FlatLevelSource CHUNK_GENERATOR = new FlatLevelSource(new FlatLevelGeneratorSettings(
            Optional.empty(),
            Holder.Reference.createIntrusive(new EmptyHolderOwner<>(), biome(
                    false,
                    0.8F,
                    0.4F,
                    new MobSpawnSettings.Builder(),
                    new BiomeGenerationSettings.Builder(
                            new EmptyHolderLookup<>(),
                            new EmptyHolderLookup<>()
                    ),
                    null
            )),
            Collections.emptyList()
    ));
    public static final @NotNull LevelStem DIMENSION = new LevelStem(
            new FullHolder<>(Registries.DIMENSION_TYPE, ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation("test")), DIMENSION_TYPE),
            CHUNK_GENERATOR
    );

    static {
        Registry<LevelStem> levelStems = MinecraftServer.getServer().registryAccess().registryOrThrow(Registries.LEVEL_STEM);
        unfreeze(levelStems);
        register(levelStems, "test", DIMENSION);

        Registry<DimensionType> dimensionTypes = MinecraftServer.getServer().registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);
        unfreeze(dimensionTypes);
        register(dimensionTypes, "test", DIMENSION_TYPE);
    }

    public static void unfreeze(@NotNull Registry<?> registry) {
        ReflectionUtil.setFieldValue(ReflectionUtil.getField("l", MappedRegistry.class), registry, false);
    }

    public static <T> void register(@NonNull Registry<T> registry, @NonNull String name, @NonNull T value) {
        Holder.Reference holder = ((WritableRegistry) registry).register(ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(name)), value, Lifecycle.stable());
        ReflectionUtil.setFieldValue(ReflectionUtil.getField("e", Holder.Reference.class), holder, value);
    }

    public MemoryWorld(MinecraftServer server, Executor executor, boolean tickTime, @NonNull World.Environment environment) throws IOException {
        super(
                server,
                executor,
                CUSTOM_LEVEL_STORAGE.createAccess("world", ResourceKey.create(Registries.LEVEL_STEM, new ResourceLocation("test"))),
                DATA,
                ResourceKey.create(Registries.DIMENSION, new ResourceLocation("test")),
                DIMENSION,
                new EmptyChunkProgressListener(),
                false, //is debug world
                0, //seed
                Collections.emptyList(), //spawns
                tickTime,
                new RandomSequences(0),
                environment,
                new EmptyChunkGenerator(),
                new EmptyBiomeProvider()
        );

        pvpMode = false;
        keepSpawnInMemory = false;

        FileUtils.deleteDirectory(convertable.levelDirectory.path().toFile());
    }

    public static class EmptyChunkGenerator extends ChunkGenerator {

        @Override
        public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
            return super.getDefaultBiomeProvider(worldInfo);
        }
    }

    public static class EmptyBiomeProvider extends BiomeProvider {

        @Override
        public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
            return Biome.PLAINS;
        }

        @Override
        public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
            return List.of(Biome.PLAINS);
        }
    }

    public static class EmptyChunkProgressListener implements ChunkProgressListener {

        @Override
        public void updateSpawnPos(ChunkPos spawnPos) {
        }

        @Override
        public void onStatusChange(ChunkPos pos, @Nullable ChunkStatus status) {
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void setChunkRadius(int radius) {
        }
    }

    public static net.minecraft.world.level.biome.Biome biome(boolean precipitation, float temperature, float downfall, MobSpawnSettings.Builder spawnSettings, BiomeGenerationSettings.Builder generationSettings, @javax.annotation.Nullable Music music) {
        return biome(precipitation, temperature, downfall, 4159204, 329011, null, null, spawnSettings, generationSettings, music);
    }

    private static net.minecraft.world.level.biome.Biome biome(boolean precipitation, float temperature, float downfall, int waterColor, int waterFogColor, @javax.annotation.Nullable Integer grassColor, @javax.annotation.Nullable Integer foliageColor, MobSpawnSettings.Builder spawnSettings, BiomeGenerationSettings.Builder generationSettings, @javax.annotation.Nullable Music music) {
        BiomeSpecialEffects.Builder builder = (new BiomeSpecialEffects.Builder()).waterColor(waterColor).waterFogColor(waterFogColor).fogColor(12638463).skyColor(calculateSkyColor(temperature)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(music);
        if (grassColor != null) {
            builder.grassColorOverride(grassColor);
        }

        if (foliageColor != null) {
            builder.foliageColorOverride(foliageColor);
        }

        return (new net.minecraft.world.level.biome.Biome.BiomeBuilder())
                .hasPrecipitation(precipitation)
                .temperature(temperature)
                .downfall(downfall)
                .specialEffects(builder.build())
                .mobSpawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .build();
    }

    protected static int calculateSkyColor(float temperature) {
        float f = temperature / 3.0F;
        f = Mth.clamp(f, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
    }

    public static class EmptyHolderOwner<T> implements HolderOwner<T> {
    }

    @Override
    public void save(@Nullable ProgressListener progressListener, boolean flush, boolean savingDisabled) {
        chunkSource.close(false);
    }

    @Override
    public void save(@Nullable ProgressListener progressListener, boolean flush, boolean savingDisabled, boolean close) {
        chunkSource.close(false);
    }

    @Override
    public void saveDebugReport(Path path) {
    }

    @Override
    public void saveIncrementally(boolean doFull) {
    }
}
