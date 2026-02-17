package io.github.u2894638479.math

import kotlinx.serialization.Serializable
import net.minecraft.util.math.BlockPos

@Serializable
data class Vec3L(
    val x: Long,
    val y: Long,
    val z: Long
) {
    constructor(blockPos: BlockPos):this(blockPos.x.toLong(),blockPos.y.toLong(),blockPos.z.toLong())
    fun toVec3D() = Vec3D(x.toDouble(),y.toDouble(),z.toDouble())
    fun distanceTo(other:Vec3L) = toVec3D().distanceTo(other.toVec3D())
    fun toBlockPos() = BlockPos(x.toInt(),y.toInt(),z.toInt())
}