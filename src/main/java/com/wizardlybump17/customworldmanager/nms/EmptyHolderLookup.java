package com.wizardlybump17.customworldmanager.nms;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.stream.Stream;

public class EmptyHolderLookup<T> implements HolderLookup<T> {

    @Override
    public Stream<Holder.Reference<T>> listElements() {
        return Stream.empty();
    }

    @Override
    public Stream<HolderSet.Named<T>> listTags() {
        return Stream.empty();
    }

    @Override
    public Optional<Holder.Reference<T>> get(ResourceKey<T> key) {
        return Optional.empty();
    }

    @Override
    public Optional<HolderSet.Named<T>> get(TagKey<T> tag) {
        return Optional.empty();
    }
}
