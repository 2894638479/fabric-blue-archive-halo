package io.github.u2894638479.cache

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class BeaconCacheMap(
    val map: MutableMap<BeaconCache.Pos, BeaconCache> = mutableMapOf()
): MutableMap<BeaconCache.Pos, BeaconCache> by map {
    companion object {
        operator fun get(key: WorldKey) = BeaconCacheMapMap.instance[key]

    }
}