package io.github.u2894638479.bahalo.render

import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack

class RenderParam(
    val vc: VertexConsumerProvider,
    val ms: MatrixStack,
    val tick: Long,
    val tickDelta: Double
) {
    constructor(vc: VertexConsumerProvider, ms: MatrixStack, tick: Long, tickDelta: Float)
            :this(vc,ms,tick,tickDelta.toDouble())
}

context(rp: RenderParam)
val vc get() = rp.vc

context(rp: RenderParam)
val ms get() = rp.ms

context(rp: RenderParam)
val tick get() = rp.tick

context(rp: RenderParam)
val tickDelta get() = rp.tickDelta

context(rp: RenderParam)
inline fun stack(block:()-> Unit) = try {
    ms.push()
    block()
} finally {
    ms.pop()
}