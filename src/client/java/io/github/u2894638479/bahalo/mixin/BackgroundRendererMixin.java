package io.github.u2894638479.bahalo.mixin;

import io.github.u2894638479.bahalo.config.Config;
import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @ModifyVariable(at = @At("HEAD"), method = "applyFog", ordinal = 0, argsOnly = true)
    private static float bluearchivehalo$setFogDistance(float value){
        return value + (float) Config.Companion.getInstance().getSpecial().getExtraFarPlane();
    }
}
