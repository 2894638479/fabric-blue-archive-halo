package io.github.u2894638479.bahalo.render

import io.github.u2894638479.bahalo.config.Config
import net.minecraft.client.network.AbstractClientPlayerEntity
import kotlin.math.PI

context(rp: RenderParam)
fun renderPlayerRing(entity: AbstractClientPlayerEntity) {
    stack {
        ms.translate(0.0,1.5,0.0)
        Config.instance.players[entity.name.string].forEach {
            val yaw = entity.yaw * PI / 180
            val pitch = entity.pitch * PI / 180
            renderRingInfo(it, listOf(), yaw, pitch)
        }
    }
}