package io.github.u2894638479.bahalo.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.u2894638479.bahalo.Entry;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RenderSetup.class)
public class RenderSetupMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    void bahalo$modifyRenderSetup(RenderPipeline pipeline, Map<String, RenderSetup.TextureBinding> textures, boolean useLightmap, boolean useOverlay, LayeringTransform layeringTransform, OutputTarget outputTarget, TextureTransform textureTransform, RenderSetup.OutlineProperty outlineProperty, boolean affectsCrumbling, boolean sortOnUpload, CallbackInfo ci) {
        Entry.INSTANCE.modifyRenderSetup((RenderSetup)(Object) this,textures);
    }
}
