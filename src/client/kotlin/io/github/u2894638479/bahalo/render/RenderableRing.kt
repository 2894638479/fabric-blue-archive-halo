package io.github.u2894638479.bahalo.render

import io.github.u2894638479.bahalo.config.ColorSampler
import io.github.u2894638479.bahalo.math.Vec3D

class RenderableRing(
    val pos: Vec3D,
    val segments:List<ColorSampler.Segment>,
    val combineNum: Int,
    val totalLevel: Int
)