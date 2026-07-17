package io.github.u2894638479.bahalo.cache

import io.github.u2894638479.bahalo.config.ColorSampler
import kotlinx.serialization.Serializable

@Serializable
data class BeaconCache(
    val segments: List<ColorSampler.Segment>,
    val level: Int
)