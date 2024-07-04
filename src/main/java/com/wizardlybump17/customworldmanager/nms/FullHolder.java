package com.wizardlybump17.customworldmanager.nms;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public record FullHolder<T>(ResourceKey<? extends Registry<T>> registry, ResourceKey<T> key, T value) implements Holder<T> {

    @Override
    public boolean isBound() {
        return true;
    }

    @Override
    public boolean is(ResourceLocation id) {
        return key.location().equals(id);
    }

    @Override
    public boolean is(ResourceKey<T> key) {
        return this.key.equals(key);
    }

    @Override
    public boolean is(Predicate<ResourceKey<T>> predicate) {
        return predicate.test(key);
    }

    @Override
    public boolean is(TagKey<T> tag) {
        return key.location().equals(tag.location());
    }

    @Override
    public Stream<TagKey<T>> tags() {
        return Stream.of(TagKey.create(registry, key.location()));
    }

    @Override
    public Either<ResourceKey<T>, T> unwrap() {
        return Either.left(key);
    }

    @Override
    public Optional<ResourceKey<T>> unwrapKey() {
        return Optional.of(key);
    }

    @Override
    public Kind kind() {
        return Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> owner) {
        return false;
    }
}
