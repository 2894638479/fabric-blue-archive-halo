package io.github.u2894638479.bahalo.render

import io.github.u2894638479.bahalo.config.ColorSampler
import io.github.u2894638479.bahalo.config.Config
import io.github.u2894638479.bahalo.math.Vec3D
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import kotlin.collections.forEach
import kotlin.math.sqrt

class RenderableRing(
    val pos: Vec3D,
    val segments:List<ColorSampler.Segment>,
    val combineNum: Int,
    val totalLevel: Int
) {
    context(rp: RenderParam)
    fun renderBeam() {
        stack {
            ms.translate(pos.x,pos.y,pos.z)
            val scale = sqrt(combineNum.toFloat())
            ms.scale(scale, 1f, scale)
            ms.translate(-0.5, 0.0, -0.5)
            var k = 0
            segments.forEachIndexed { index, segment ->
                BeaconBlockEntityRenderer.renderBeam(
                    ms, vc, tickDelta.toFloat(),tick, k,
                    if(index == segments.size - 1) 1024 else segment.height,
                    segment.run { floatArrayOf(color.rFloat,color.gFloat,color.bFloat) }
                )
                k += segment.height
            }
        }
    }
    context(rp: RenderParam)
    fun render() {
        stack {
            ms.translate(pos.x, pos.y, pos.z)
            Config.instance.levels[totalLevel].forEach { info ->
                renderRingAt(info, segments, pos.toVec3d())
            }
        }
    }
}