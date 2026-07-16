package io.github.u2894638479.bahalo.render

import io.github.u2894638479.bahalo.Entry
import io.github.u2894638479.kotlinmcui.math.Color
import net.minecraft.client.renderer.rendertype.RenderTypes
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

context(rp: RenderParam)
fun renderRing(
    radius: Double,
    width: Double,
    sides: Int,
    columnSides: Int,
    colorBy0to1: (Double) -> Color
) {
    val sides = max(sides,0)
    val angles = (0..sides).map {
        2 * PI * it / sides
    }.map {
        val cos = cos(it).toFloat()
        val sin = sin(it).toFloat()
        val color = colorBy0to1(it / (2* PI))
        AngleInfo(cos, sin, color)
    }
    vc.submitCustomGeometry(ms,RenderTypes.beaconBeam(Entry.texture,true)) { pose, buffer ->
        AngleInfo.Scope(buffer,pose).run {
            val hScale = 1 / cos(PI / sides)
            (0..columnSides).map {
                2 * PI * it / columnSides
            }.asReversed().zipWithNext { a, b ->
                angles.ring(
                    radius + width * sin(a) * hScale,
                    radius + width * sin(b) * hScale,
                    width * cos(a),
                    width * cos(b),
                )
            }
        }
    }
}