package io.github.u2894638479.bahalo.render

import io.github.u2894638479.bahalo.config.ColorSampler
import io.github.u2894638479.bahalo.config.RingInfo
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import kotlin.math.PI

fun renderRingAt(
    vertexConsumers: VertexConsumerProvider,
    matrices: MatrixStack,
    ringInfo: RingInfo,
    tick: Long,
    tickDelta: Double,
    segments: List<ColorSampler.Segment>,
    pos: Vec3d,
    yaw:Double = 0.0,
    pitch:Double = 0.0
) {
    val r = ringInfo.radius
    val height = ringInfo.height
    val angleCount = run {
        val cameraPos = MinecraftClient.getInstance().gameRenderer.camera.pos
        val distance = pos.add(0.0, height,0.0).distanceTo(cameraPos)
        when {
            distance > 3000 -> 20
            distance > 1000 -> 80
            distance > 500 -> 100
            else -> 200
        }
    }
    val rotation = rotation(tick, tickDelta, ringInfo.cycle)
    val color = ringInfo.sampler.sample(segments)

    matrices.push()
    matrices.multiply(RotationAxis.POSITIVE_Y.rotation(PI.toFloat()-yaw.toFloat()))
    matrices.multiply(RotationAxis.POSITIVE_X.rotation(-pitch.toFloat()))
    matrices.translate(0.0, height, 0.0)

    matrices.push()
    matrices.multiply(RotationAxis.POSITIVE_Y.rotation(rotation.toFloat()))
    if(rotation < 0) matrices.scale(-1f,1f,1f)
    renderRing(matrices, vertexConsumers, r, ringInfo.width, angleCount) { ringInfo.style.color(it, color) }
    matrices.pop()

    ringInfo.subRings.forEach {
        matrices.push()
        val rotation1 = rotation(tick, tickDelta, it.cycle)
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(rotation1.toFloat()))
        matrices.translate(0.0,0.01,ringInfo.radius)
        renderRingAt(vertexConsumers,matrices,it.ringInfo,tick,tickDelta,segments,pos)
        matrices.pop()
    }

    matrices.pop()
}