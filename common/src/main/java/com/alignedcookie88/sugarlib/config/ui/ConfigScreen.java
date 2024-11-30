package com.alignedcookie88.sugarlib.config.ui;

import com.alignedcookie88.sugarlib.ModInfo;
import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.Config;
import com.alignedcookie88.sugarlib.config.client_view.ClientConfigView;
import com.alignedcookie88.sugarlib.config.ui.optionuiprovider.OptionUIProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {


    public static @Nullable Screen create(Config config, Screen parent) {
        ClientConfigView<?> view = config.getClientView();
        if (view == null)
            return null;
        return new ConfigLoadingScreen(view, parent);
    }


    private final ClientConfigView<?> view;

    private final Screen parent;

    private Button saveButton;

    private int page;

    public ConfigScreen(ClientConfigView<?> view, Screen parent, int page) {
        super(createTitle(view));
        this.view = view;
        this.parent = parent;
        this.page = page;
    }

    private static Component createTitle(ClientConfigView<?> view) {
        ModInfo mod = view.getMod();
        Component name = view.getName();

        return view.isWritable()
                ? Component.translatable("sugarlib.config.config_title", mod.getName(), name)
                : Component.translatable("sugarlib.config.config_title.read_only", mod.getName(), name);
    }


    private List<OptionUIProvider.OptionRenderer<?>> optionRenderers = new ArrayList<>();

    private List<List<ClientConfigView.Option<?, ?>>> pages = new ArrayList<>();

    @Override
    protected void init() {
        optionRenderers.clear();

        pages = getPages();

        if (pages.isEmpty()) {
            return;
        }

        if (page > pages.size()-1) {
            SugarLib.getClient().setScreen(new ConfigScreen(view, parent, pages.size()-1));
            return;
        }

        addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, button -> {
            this.onClose();
        }).bounds((width/2)-152, height-30, 150, 20).build());

        saveButton = Button.builder(Component.translatable("sugarlib.config.save_and_exit"), button -> {
            view.save();
            this.onClose();
        }).bounds((width/2)+1, height-30, 150, 20).build();
        addRenderableWidget(saveButton);


        if (page > 0) {
            addRenderableWidget(Button.builder(Component.literal("◀"), button -> {
                SugarLib.getClient().setScreen(new ConfigScreen(view, parent, page-1));
            }).bounds((width/2)-175, height-30, 20, 20).build());
        }

        if (page < pages.size()-1) {
            addRenderableWidget(Button.builder(Component.literal("▶"), button -> {
                SugarLib.getClient().setScreen(new ConfigScreen(view, parent, page+1));
            }).bounds((width/2)+154, height-30, 20, 20).build());
        }

        int y = 45;
        int x = (width/2)+10;
        int fullWidth = (width/2)-20;
        int optWidth = fullWidth-75-75-3-3;

        if (optWidth < 100) {
            optWidth += 75;
            x -= 75;
        }

        for (ClientConfigView.Option<?, ?> option : pages.get(page)) {
            OptionUIProvider<?> provider = option.getUIProvider();
            if (provider == null) {
                SugarLib.LOGGER.warn("Wtf..."); // This should NEVER run. (It probably will somehow)
                continue;
            }
            Object value = option.get();
            OptionUIProvider.UIInfo info = new OptionUIProvider.UIInfo(x, y, optWidth, this, this.font);
            Button resetBeforeEditButton = Button.builder(Component.translatable("sugarlib.config.reset.before_edit"), button -> {
                option.setIfTypeCorrect(value);
                provider.notifyUpdateIfTypeCorrect(value);
            }).bounds(x+optWidth+3, y, 75, provider.getRequiredHeight(info)).tooltip(Tooltip.create(Component.translatable("sugarlib.config.reset.before_edit.hover"))).build();
            resetBeforeEditButton.active = false;
            addRenderableWidget(resetBeforeEditButton);
            Button resetOriginalButton = Button.builder(Component.translatable("sugarlib.config.reset.original"), button -> {
                option.reset();
                provider.notifyUpdateIfTypeCorrect(option.get());
            }).bounds(x+optWidth+3+75+3, y, 75, provider.getRequiredHeight(info)).tooltip(Tooltip.create(Component.translatable("sugarlib.config.reset.original.hover"))).build();
            addRenderableWidget(resetOriginalButton);
            OptionUIProvider.OptionRenderer<?> optionRenderer = new OptionUIProvider.OptionRenderer(provider, info, option, resetBeforeEditButton, resetOriginalButton);
            optionRenderer.setup();
            y += optionRenderer.getRequiredHeight() + 4;
            optionRenderers.add(optionRenderer);
        }

    }

    private List<List<ClientConfigView.Option<?, ?>>> getPages() {
        int fullWidth = (width/2)-20;
        int optWidth = fullWidth-75-75-3-3;
        OptionUIProvider.UIInfo uiInfo = new OptionUIProvider.UIInfo(0, 0, optWidth, this, this.font);

        List<List<ClientConfigView.Option<?, ?>>> pages = new ArrayList<>();
        int max_height = height - 90;


        List<ClientConfigView.Option<?, ?>> page = new ArrayList<>();
        int current_height = 0;

        for (ClientConfigView.Option<?, ?> option : view.getOptions()) {
            OptionUIProvider<?> provider = option.getUIProvider();
            if (provider == null) {
                SugarLib.LOGGER.warn("Couldn't get UI provider for config option {}. Have you tried registering one for the type of the option?", option.getFullId());
                continue;
            }

            int opt_height = provider.getRequiredHeight(uiInfo);
            int soon_height = current_height+opt_height;

            if (soon_height > max_height) {
                if (current_height == 0) {
                    SugarLib.LOGGER.warn("Couldn't fit UI provider for config option {} on-screen.", option.getFullId());
                    continue;
                }
                pages.add(page);
                page = new ArrayList<>();
                page.add(option);
                current_height = opt_height;
            } else {
                current_height = soon_height;
                page.add(option);
            }

        }

        if (!page.isEmpty())
            pages.add(page);

        return pages;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        boolean anyInvalid = false;

        saveButton.active = view.isWritable() && view.haveAnyValuesChanged();
        if (!view.isWritable()) {
            saveButton.setTooltip(Tooltip.create(Component.translatable("sugarlib.config.save_and_exit.read_only")));
        } else {
            saveButton.setTooltip(null);
        }

        List<Runnable> runnables = new ArrayList<>();

        for (OptionUIProvider.OptionRenderer<?> optionRenderer : optionRenderers) {
            int text_x = 10;
            int max_text_width = optionRenderer.info().x()-text_x;
            int text_y = optionRenderer.info().y() + (optionRenderer.getRequiredHeight() / 2) - 5;

            MutableComponent text = optionRenderer.option().getName().copy().withStyle(style -> style.applyFormat(ChatFormatting.GRAY));

            if (font.width(text) > max_text_width) {
                text = Component.literal(font.substrByWidth(text, max_text_width-6).getString()+"...").withStyle(style -> style.applyFormat(ChatFormatting.GRAY));
            }

            optionRenderer.resetBeforeEditButton().active = false;
            if (optionRenderer.option().hasChanged()) {
                text = text.withStyle(style -> style.applyFormats(ChatFormatting.ITALIC, ChatFormatting.WHITE));
                optionRenderer.resetBeforeEditButton().active = true;
            }

            optionRenderer.resetOriginalButton().active = false;
            if (optionRenderer.option().isResettable()) {
                optionRenderer.resetOriginalButton().active = true;
            }

            if (optionRenderer.option().isSelfValid() != null || optionRenderer.provider().isValid() != null) {
                text = text.withStyle(style -> style.applyFormat(ChatFormatting.RED));
                text_y = text_y-5;
                Component uiErrorText = optionRenderer.provider().isValid();
                Component errorText = uiErrorText == null ? optionRenderer.option().isSelfValid() : uiErrorText;
                int finalText_y = text_y;
                runnables.add(() -> guiGraphics.drawString(font, errorText, text_x, finalText_y+10, 0xFFFFFF, true));
                anyInvalid = true;
            }

            MutableComponent finalText = text;
            int finalText_y1 = text_y;
            runnables.add(() -> guiGraphics.drawString(font, finalText, text_x, finalText_y1, 0xFFFFFF, true));

            int text_empty_space = max_text_width - font.width(finalText);

            if (text_empty_space > 5) {
                int dots = (text_empty_space - 10) / 2;
                if (dots > 0) {
                    String dotString = ".".repeat(dots);
                    int loc = text_x+font.width(finalText)+5;
                    loc = loc - (loc % 2);
                    int finalLoc = loc;
                    runnables.add(() -> guiGraphics.drawString(font, dotString, finalLoc, finalText_y1, 0x343434, false));
                }
            }

            optionRenderer.render();
        }

        if (anyInvalid) {
            saveButton.active = false;
        }

        super.render(guiGraphics, i, j, f);

        int text_width = this.font.width(this.title);
        guiGraphics.drawString(this.font, this.title, (width - text_width)/2, 17, 0xFFFFFF, true);

        for (Runnable runnable : runnables) {
            runnable.run();
        }
    }

    @Override
    public void onClose() {
        SugarLib.getClient().setScreen(parent);
    }

    @Override
    @ApiStatus.Internal
    public  <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T guiEventListener) {
        return super.addRenderableWidget(guiEventListener);
    }
}
