package io.github.betterclient.areyousure;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public class AreYouSure extends Screen {
    public static ClientPlayerInteractionManager interactionManager;
    public static ClientPlayerEntity player;
    public static Hand hand;

    public static long canStripUntil = 0;

    public AreYouSure() {
        super(Text.translatable("text.areyousure"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("text.areyousure"), this.width / 2, this.height / 2 - 50, -1);
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.yes"), button -> {
            if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult hitResult)
                interactionManager.interactBlock(player, hand, hitResult);

            quit();
        }).dimensions(width / 2 - 125, height / 2 + 10, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("text.allow1min"), button -> {
            if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult hitResult)
                interactionManager.interactBlock(player, hand, hitResult);

            canStripUntil = System.currentTimeMillis() + (1000 * 60);
            new Thread(() -> {
                while (canStripUntil > System.currentTimeMillis()) {/*wait*/}
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable("text.youcantanymore"));
            }).start();

            quit();
        }).dimensions(width / 2 - 125, height / 2 + 40, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("text.allow5min"), button -> {
            if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult hitResult)
                interactionManager.interactBlock(player, hand, hitResult);

            canStripUntil = System.currentTimeMillis() + (1000 * (60 * 5));
            new Thread(() -> {
                while (canStripUntil > System.currentTimeMillis()) {/*wait*/}
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable("text.youcantanymore"));
            }).start();

            quit();
        }).dimensions(width / 2 - 125, height / 2 + 70, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.no"), button -> quit()).dimensions(width / 2 + 25, height / 2 + 10, 100, 20).build());
    }

    private void quit() {
        MinecraftClient.getInstance().setScreen(null);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
