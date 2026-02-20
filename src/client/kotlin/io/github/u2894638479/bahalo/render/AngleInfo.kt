package io.github.u2894638479.bahalo.render

import io.github.u2894638479.kotlinmcui.math.Color
import net.minecraft.client.render.VertexConsumer
import org.joml.Matrix4f

class AngleInfo(
    val cos:Float,val sin:Float,val color: Color
){
    class Scope(val consumer: VertexConsumer, val modelMatrix: Matrix4f){
        fun AngleInfo.vertex(radius:Float,y:Float = 0f) = vertex(consumer,modelMatrix,radius,y)
        fun AngleInfo.vertex2(radius:Float,y:Float = 0f) = repeat(2) { vertex(consumer, modelMatrix, radius, y) }
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
    fun vertex(consumer: VertexConsumer, modelMatrix: Matrix4f, radius:Float, y:Float){
        consumer.vertex(modelMatrix,radius*cos,y,radius*sin).color(color.argbInt).next()
    }
}