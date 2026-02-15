package io.github.u2894638479.render

import io.github.u2894638479.BlueArchiveHaloClient
import io.github.u2894638479.kotlinmcui.math.Color
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun renderRing(
    matrices: MatrixStack, consumerProvider: VertexConsumerProvider,
    radius: Double, thickness: Double, segmentCount:Int,
    colorBy0to1: (Double) -> Color
) {
    val radius = radius.toFloat()
    val thickness = thickness.toFloat()
    val consumer = consumerProvider.getBuffer(RenderLayer.getBeaconBeam(BlueArchiveHaloClient.texture,true))
    val modelMatrix = matrices.peek().positionMatrix
    val radiusInner = radius - thickness/2
    val radiusOuter = radius + thickness/2
    val viewHeight = thickness * 2 / 3


    val angles = (0..segmentCount).map {
        2 * PI * it / segmentCount
    }.map {
        val cos = cos(it).toFloat()
        val sin = sin(it).toFloat()
        val color = colorBy0to1(it / (2* PI))
        AngleInfo(cos, sin, color)
    }
    AngleInfo.Scope(consumer,modelMatrix).run {
        angles.firstOrNull()?.vertex2(radiusInner)
        angles.forEach {
            it.vertex(radiusInner)
            it.vertex(radiusOuter)
        }
        angles.forEach {
            it.vertex(radiusOuter)
            it.vertex(radius, viewHeight)
        }
        angles.forEach {
            it.vertex(radius, viewHeight)
            it.vertex(radiusInner)
        }
        angles.lastOrNull()?.vertex2(radiusInner)
    }
}