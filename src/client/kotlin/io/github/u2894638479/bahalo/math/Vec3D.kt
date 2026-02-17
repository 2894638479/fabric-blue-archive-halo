package io.github.u2894638479.bahalo.math

import kotlinx.serialization.Serializable
import net.minecraft.util.math.Vec3d
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
data class Vec3D(
    val x: Double,
    val y: Double,
    val z: Double
) {
    fun plus(x: Double,y: Double,z: Double) = Vec3D(this.x + x,this.y + y,this.z + z)
    operator fun plus(other: Vec3D) = plus(other.x,other.y,other.z)
    operator fun unaryMinus() = Vec3D(-x,-y,-z)
    operator fun minus(other: Vec3D) = this + -other
    fun distanceTo(other:Vec3D) = sqrt((x - other.x).pow(2) + (y - other.y).pow(2) + (z - other.z).pow(2))
    fun toVec3d() = Vec3d(x,y,z)
}