package io.github.u2894638479.bahalo.mixin;

import io.github.u2894638479.bahalo.config.Config;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Redirect(method = "extractCamera",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/FogRenderer;setupFog(Lnet/minecraft/client/Camera;ILnet/minecraft/client/DeltaTracker;FLnet/minecraft/client/multiplayer/ClientLevel;)Lnet/minecraft/client/renderer/fog/FogData;"))
    private static FogData bluearchivehalo$setFogDistance(FogRenderer instance, Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level){
        var result = instance.setupFog(camera, renderDistanceInChunks, deltaTracker, darkenWorldAmount, level);
        var extend = (float) Config.Companion.getInstance().getSpecial().getExtraFarPlane();
        result.renderDistanceEnd += extend;
        result.renderDistanceStart += extend;
        result.cloudEnd += extend;
        result.renderDistanceEnd += extend;
        result.skyEnd += extend;
        result.environmentalEnd += extend;
        return result;
    }
}
