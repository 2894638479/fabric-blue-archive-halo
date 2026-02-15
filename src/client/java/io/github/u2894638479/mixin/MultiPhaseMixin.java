package io.github.u2894638479.mixin;

import io.github.u2894638479.BlueArchiveHaloClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(RenderLayer.MultiPhase.class)
public class MultiPhaseMixin {
    @Inject(method = "<init>",at = @At("TAIL"))
    void bluearchivehalo$modifyMultiPhase(
            String name,
            VertexFormat vertexFormat,
            VertexFormat.DrawMode drawMode,
            int expectedBufferSize,
            boolean hasCrumbling,
            boolean translucent,
            RenderLayer.MultiPhaseParameters phases,
            CallbackInfo ci
    ){
        if(!Objects.equals(name, "beacon_beam")) return;
        if(!translucent) return;
        RenderLayer.MultiPhase multiPhase = (RenderLayer.MultiPhase)(Object)this;
        BlueArchiveHaloClient.Companion.modifyMultiPhase(multiPhase,name,phases);
    }
}
