package io.github.u2894638479.render

import io.github.u2894638479.BlueArchiveHaloClient
import net.minecraft.client.render.Frustum
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack

class ClientCacheBeaconsRenderer(ctx: EntityRendererFactory.Context?) : EntityRenderer<ClientCacheBeacons>(ctx) {
    override fun getTexture(entity: ClientCacheBeacons?) = BlueArchiveHaloClient.texture
    override fun shouldRender(entity: ClientCacheBeacons?, frustum: Frustum?, x: Double, y: Double, z: Double) = true
    override fun render(entity: ClientCacheBeacons?, yaw: Float, tickDelta: Float, matrices: MatrixStack?, vertexConsumers: VertexConsumerProvider?, light: Int) {

    }
}