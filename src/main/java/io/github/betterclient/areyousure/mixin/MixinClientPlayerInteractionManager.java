package io.github.betterclient.areyousure.mixin;

import io.github.betterclient.areyousure.AreYouSure;
import net.minecraft.block.PillarBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.AxeItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    public void onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (player.getStackInHand(hand).getItem() instanceof AxeItem &&
            player.getWorld().getBlockState(hitResult.getBlockPos()).getBlock() instanceof PillarBlock pillarBlock) {
            String name = pillarBlock.getTranslationKey();

            if(name.endsWith("_log") && !name.contains("stripped")) {
                if (MinecraftClient.getInstance().currentScreen instanceof AreYouSure)
                    return; //Player clicked yes :(

                if (AreYouSure.canStripUntil >= System.currentTimeMillis())
                    return; //Allow for 1-5 min

                AreYouSure.interactionManager = (ClientPlayerInteractionManager)(Object)this;
                AreYouSure.player = player;
                AreYouSure.hand = hand;

                MinecraftClient.getInstance().setScreen(new AreYouSure());
                cir.setReturnValue(ActionResult.FAIL);
                cir.cancel();
            }
        }
    }
}
