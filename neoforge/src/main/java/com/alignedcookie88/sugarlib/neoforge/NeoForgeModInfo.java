package com.alignedcookie88.sugarlib.neoforge;

import com.alignedcookie88.sugarlib.ModInfo;
import com.alignedcookie88.sugarlib.config.ui.ConfigChooserScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforgespi.language.IModInfo;

public class NeoForgeModInfo extends ModInfo {

    public final Mod modAnnotation;
    public final ModContainer modContainer;
    public final IModInfo modInfo;

    protected NeoForgeModInfo(Mod modAnnotation, ModContainer modContainer, IModInfo modInfo) {
        super(modAnnotation.value(), Component.literal(modInfo.getDisplayName()));
        this.modAnnotation = modAnnotation;
        this.modContainer = modContainer;
        this.modInfo = modInfo;
    }

    public static NeoForgeModInfo fromModObject(Object modObject) {
        Mod modAnnotation = modObject.getClass().getAnnotation(Mod.class);
        ModContainer container = ModList.get().getModContainerById(modAnnotation.value()).orElseThrow();
        return new NeoForgeModInfo(modAnnotation, container, container.getModInfo());
    }


    @Override
    public void registerConfigScreenIfRequired() {
        // Setup loading context
        ModLoadingContext ctx = ModLoadingContext.get();
        ModContainer lastContainer = ctx.getActiveContainer();
        ctx.setActiveContainer(modContainer);

        // Register config screen
        ModInfo MTHIS = this;
        ctx.registerExtensionPoint(IConfigScreenFactory.class, () -> (modContainer, arg) -> new ConfigChooserScreen(MTHIS, arg));

        // Restore loading context
        ctx.setActiveContainer(lastContainer);
    }
}
