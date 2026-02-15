package io.github.u2894638479.render

import io.github.u2894638479.config.Config
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.PI

fun renderPlayerRing(
    entity: AbstractClientPlayerEntity,
    vc: VertexConsumerProvider,
    ms: MatrixStack,
    tickDelta: Double
) {
    val infos = Config.instance.players[entity.name.string]
    infos.forEach {
        val rotation = rotation(entity.world.time,tickDelta,it.cycle)
        val yaw = entity.yaw * PI / 180
        val pitch = entity.pitch * PI / 180
        ms.push()
        ms.translate(0.0,1.5,0.0)
        renderRingAt(vc,ms,it,rotation,it.sampler.sample(listOf()),entity.pos,yaw,pitch)
        ms.pop()
    }
}