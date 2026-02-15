package io.github.u2894638479.mixin;

import io.github.u2894638479.config.Config;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getFarPlaneDistance",at = @At("RETURN"),cancellable = true)
    private void bluearchivehalo$extendFarPlane(CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(cir.getReturnValue() + (float)Config.Companion.getInstance().getSpecial().getExtraFarPlane());
    }
}
