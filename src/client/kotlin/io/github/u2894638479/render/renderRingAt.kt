package io.github.u2894638479.render

import io.github.u2894638479.config.RingInfo
import io.github.u2894638479.kotlinmcui.math.Color
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import kotlin.math.PI
import kotlin.math.absoluteValue

fun renderRingAt(
    vertexConsumers: VertexConsumerProvider,
    matrices: MatrixStack,
    ringInfo: RingInfo,
    rotation: Double,
    color: Color,
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
    matrices.push()
    matrices.multiply(RotationAxis.POSITIVE_Y.rotation(PI.toFloat()-yaw.toFloat()))
    matrices.multiply(RotationAxis.POSITIVE_X.rotation(-pitch.toFloat()))
    matrices.translate(0.0, height, 0.0)
    matrices.multiply(RotationAxis.POSITIVE_Y.rotation(rotation.toFloat().absoluteValue))
    if(rotation < 0) matrices.scale(-1f,1f,1f)
    renderRing(matrices, vertexConsumers, r, ringInfo.width,angleCount) { ringInfo.style.color(it, color) }
    matrices.pop()
}