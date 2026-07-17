package io.github.u2894638479.bahalo.mixin;

import io.github.u2894638479.bahalo.render.RenderParam;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(at = @At("RETURN"),method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    void bluearchivehalo$renderPlayerHalo(
            AbstractClientPlayerEntity abstractClientPlayerEntity,
            float f,
            float g,
            MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider,
            int i,
            CallbackInfo ci
    ) {
        io.github.u2894638479.bahalo.render.RenderPlayerRingKt.renderPlayerRing(
                new RenderParam(
                        vertexConsumerProvider,
                        matrixStack,
                        abstractClientPlayerEntity.getWorld().getTime(),
                        g
                ),
                abstractClientPlayerEntity
        );
    }
}
