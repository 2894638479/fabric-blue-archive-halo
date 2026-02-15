package io.github.u2894638479.cache

import io.github.u2894638479.config.ColorSampler
import kotlinx.serialization.Serializable

@Serializable
class BeaconCache(
    val segment: List<ColorSampler.Segment>,
    val level: Int
) {
    @Serializable
    class Pos(val x: Long,val y: Long,val z: Long)

}