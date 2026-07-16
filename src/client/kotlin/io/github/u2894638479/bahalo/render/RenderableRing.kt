package io.github.u2894638479.bahalo.render

import io.github.u2894638479.bahalo.config.ColorSampler
import io.github.u2894638479.bahalo.config.Config
import io.github.u2894638479.bahalo.math.Vec3D
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.blockentity.BeaconRenderer
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
            val mc = Minecraft.getInstance()
            val ani = Math.floorMod(mc.level?.gameTime ?: return@stack, 40) +
                    mc.deltaTracker.getGameTimeDeltaPartialTick(false)
            segments.forEachIndexed { index, segment ->
                BeaconRenderer.submitBeaconBeam(
                    ms, vc, 1f,ani, k,
                    if(index == segments.size - 1) 1024 else segment.height,
                    segment.color.argbInt
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
                renderRingInfo(info, segments)
            }
        }
    }
}