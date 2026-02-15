package io.github.u2894638479.render

import io.github.u2894638479.kotlinmcui.math.Color
import net.minecraft.client.render.VertexConsumer
import org.joml.Matrix4f

class AngleInfo(
    val cos:Float,val sin:Float,val color: Color
){
    class Scope(val consumer: VertexConsumer, val modelMatrix: Matrix4f){
        fun AngleInfo.vertex(radius:Float,y:Float = 0f) = vertex(consumer,modelMatrix,radius,y)
        fun AngleInfo.vertex2(radius:Float,y:Float = 0f) = repeat(2) { vertex(consumer, modelMatrix, radius, y) }
    }
    fun vertex(consumer: VertexConsumer, modelMatrix: Matrix4f, radius:Float, y:Float){
        consumer.vertex(modelMatrix,radius*cos,y,radius*sin).color(color.argbInt).next()
    }
}