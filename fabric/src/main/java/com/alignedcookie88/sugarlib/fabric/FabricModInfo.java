package com.alignedcookie88.sugarlib.fabric;

import com.alignedcookie88.sugarlib.ModInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.network.chat.Component;

public class FabricModInfo extends ModInfo {

    public final ModContainer container;

    public final ModMetadata metadata;

    public FabricModInfo(ModContainer modContainer) {
        this(modContainer, modContainer.getMetadata());
    }

    private FabricModInfo(ModContainer modContainer, ModMetadata modMetadata) {
        super(modMetadata.getId(), Component.literal(modMetadata.getName()));
        this.metadata = modMetadata;
        this.container = modContainer;
    }

    public static FabricModInfo fromId(String id) {
        return new FabricModInfo(FabricLoader.getInstance().getModContainer(id).orElseThrow());
    }

    @Override
    public void registerConfigScreenIfRequired() {

    }
}
