package io.github.u2894638479.bahalo.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.u2894638479.bahalo.render.ClientCacheBeaconsRenderer;
import io.github.u2894638479.bahalo.render.RenderParam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Inject(method = "submitBlockEntities", at = @At(value = "RETURN"))
    void bahalo$renderBeaconCache(PoseStack poseStack, LevelRenderState levelRenderState, SubmitNodeCollector submitNodeCollector, CallbackInfo ci) {
        Vec3 cam = levelRenderState.cameraRenderState.pos;
        var pose = new PoseStack();
        pose.translate(-cam.x(),-cam.y(),-cam.z());
        ClientCacheBeaconsRenderer.INSTANCE.render(
                new RenderParam(
                        submitNodeCollector,
                        pose,
                        levelRenderState.gameTime,
                        Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false)
                )
        );
    }
}
