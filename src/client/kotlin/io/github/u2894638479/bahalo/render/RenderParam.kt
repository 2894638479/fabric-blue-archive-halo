package io.github.u2894638479.bahalo.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.SubmitNodeCollector

class RenderParam(
    val vc: SubmitNodeCollector,
    val ms: PoseStack,
    val tick: Long,
    val tickDelta: Double
) {
    constructor(vc: SubmitNodeCollector, ms: PoseStack, tick: Long, tickDelta: Float)
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
    ms.pushPose()
    block()
} finally {
    ms.popPose()
}