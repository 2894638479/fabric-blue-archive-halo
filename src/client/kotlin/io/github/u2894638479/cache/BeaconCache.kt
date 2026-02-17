package io.github.u2894638479.cache

import io.github.u2894638479.config.ColorSampler
import kotlinx.serialization.Serializable

@Serializable
data class BeaconCache(
    val segments: List<ColorSampler.Segment>,
    val level: Int
)