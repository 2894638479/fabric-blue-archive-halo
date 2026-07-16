package io.github.u2894638479.bahalo.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.u2894638479.bahalo.render.RenderParam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",at = @At("RETURN"))
    void bahalo$renderPlayerRing(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        var level = Minecraft.getInstance().level;
        if(level == null) return;
        var delta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks();
        if (state instanceof AvatarRenderState) {
            io.github.u2894638479.bahalo.render.RenderPlayerRingKt.renderPlayerRing(
                    new RenderParam(submitNodeCollector,poseStack,level.getGameTime(),delta), (AvatarRenderState) state
            );
        }
    }
}
