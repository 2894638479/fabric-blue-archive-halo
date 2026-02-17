package io.github.u2894638479.render

import io.github.u2894638479.config.ColorSampler
import io.github.u2894638479.math.Vec3D
import net.minecraft.util.math.Vec3d

class RenderableRing(
    val pos: Vec3D,
    val segments:List<ColorSampler.Segment>,
    val combineNum: Int,
    val totalLevel: Int
)