package io.github.u2894638479.bahalo.render

import io.github.u2894638479.bahalo.config.ColorSampler
import io.github.u2894638479.bahalo.config.Config
import io.github.u2894638479.bahalo.config.RingInfo
import net.minecraft.util.math.RotationAxis
import org.joml.Vector4f
import kotlin.math.PI
import kotlin.math.max


context(rp: RenderParam)
fun renderRingInfo(
    ringInfo: RingInfo,
    segments: List<ColorSampler.Segment>,
    yaw:Double = 0.0,
    pitch:Double = 0.0
) {
    val height = ringInfo.height
    stack {
        ms.multiply(RotationAxis.POSITIVE_Y.rotation(PI.toFloat() - yaw.toFloat()))
        ms.multiply(RotationAxis.POSITIVE_X.rotation(-pitch.toFloat()))
        ms.translate(0.0, height, 0.0)

        stack {
            val sides = if(ringInfo.autoSide) run {
                val currentMatrix = ms.peek().positionMatrix
                val origin = Vector4f(0f, 0f, 0f, 1.0f)
                val distance = origin.mul(currentMatrix).run { distance(0f,0f,0f,w) }.toDouble()
                val double = Config.instance.special.lodPrecision * ringInfo.radius / max(distance,ringInfo.radius)
                double.toInt() + 16
            } else ringInfo.sides
            val columnSides = Config.instance.special.columnSides
            val rotation = rotation(tick, tickDelta, ringInfo.cycle)
            val color = ringInfo.sampler.sample(segments)
            ms.multiply(RotationAxis.POSITIVE_Y.rotation(rotation.toFloat()))
            if(rotation < 0) ms.scale(-1f, 1f, 1f)
            renderRing(ringInfo.radius, ringInfo.width, sides,columnSides) { ringInfo.style.color(it, color) }
        }

        ringInfo.subRings.forEach {
            stack {
                val rotation1 = rotation(tick, tickDelta, it.cycle)
                ms.multiply(RotationAxis.POSITIVE_Y.rotation(rotation1.toFloat()))
                ms.translate(0.0, 0.01, ringInfo.radius)
                renderRingInfo(it.ringInfo,segments)
            }
        }
    }
}