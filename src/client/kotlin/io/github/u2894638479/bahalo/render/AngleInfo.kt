package io.github.u2894638479.bahalo.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import io.github.u2894638479.kotlinmcui.math.Color

class AngleInfo(
    val cos:Float,val sin:Float,val color: Color
){
    class Scope(val consumer: VertexConsumer, val pose: PoseStack.Pose){
        fun AngleInfo.vertex(radius:Float,y:Float = 0f) = vertex(consumer,pose,radius,y)
        fun AngleInfo.vertex2(radius:Float,y:Float = 0f) = repeat(2) { vertex(consumer, pose, radius, y) }
        fun List<AngleInfo>.ring(r1: Double,r2: Double,y1: Double,y2: Double) = ring(r1.toFloat(),r2.toFloat(),y1.toFloat(),y2.toFloat())
        fun List<AngleInfo>.ring(r1: Float,r2: Float,y1: Float,y2: Float) {
            first().vertex2(r1,y1)
            forEach {
                it.vertex(r1,y1)
                it.vertex(r2,y2)
            }
            last().vertex2(r2,y2)
        }
    }
    fun vertex(consumer: VertexConsumer, pose: PoseStack.Pose, radius:Float, y:Float){
        consumer.addVertex(pose,radius*cos,y,radius*sin).setColor(color.argbInt)
    }
}