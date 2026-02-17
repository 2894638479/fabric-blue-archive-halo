package io.github.u2894638479.bahalo.cache

import io.github.u2894638479.bahalo.math.Vec3L
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class BeaconCacheMap(
    val map: MutableMap<Vec3L, BeaconCache> = mutableMapOf()
): MutableMap<Vec3L, BeaconCache> by map {
    companion object {
        operator fun get(key: WorldKey) = BeaconCacheMapMap.instance[key]
        val current get() = WorldKey.current?.let { get(it) }
    }
}