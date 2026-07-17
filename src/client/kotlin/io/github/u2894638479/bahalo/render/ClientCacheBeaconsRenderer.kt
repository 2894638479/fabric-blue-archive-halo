package io.github.u2894638479.bahalo.render

import io.github.u2894638479.bahalo.cache.BeaconCache
import io.github.u2894638479.bahalo.cache.BeaconCacheMap
import io.github.u2894638479.bahalo.cache.WorldKey
import io.github.u2894638479.bahalo.config.Config
import io.github.u2894638479.bahalo.math.Vec3D
import io.github.u2894638479.bahalo.math.Vec3L
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack

object ClientCacheBeaconsRenderer {
    fun render(ticks: Long, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider)
    = context(RenderParam(vertexConsumers,matrices,ticks,tickDelta)){ render() }

    context(rp: RenderParam)
    fun render() {
        if(!Config.instance.special.clientCache) return
        if(!Config.instance.special.enableBeaconHalos) return
        stack {
            cachedBeacons().forEach {
                it.render()
                it.renderBeam()
            }
        }
    }

    private fun cachedBeacons(): List<RenderableRing> {
        val map = BeaconCacheMap[WorldKey.current ?: return emptyList()]
        if(!Config.instance.special.combineBeacon) return map.map {
            RenderableRing(it.key.toVec3D().plus(0.5, 0.0, 0.5), it.value.segments, 1, it.value.level)
        }
        fun Map.Entry<Vec3L, BeaconCache>.shouldCombine(other:Map.Entry<Vec3L, BeaconCache>) =
            value.segments == other.value.segments &&
                    key.distanceTo(other.key) <= Config.instance.special.combineRadius + 0.001
        val lists = mutableListOf<MutableList<Map.Entry<Vec3L, BeaconCache>>>()
        for (entry in map.entries) {
            var entryList: MutableList<Map.Entry<Vec3L, BeaconCache>>? = null
            lists.removeIf { list ->
                if(list.find { it.shouldCombine(entry) } != null) {
                    entryList?.let {
                        it += list
                        return@removeIf true
                    }
                    list += entry
                    entryList = list
                }
                false
            }
            if(entryList == null) {
                lists += mutableListOf(entry)
            }
        }
        return lists.map {
            if(it.size == 1) it.first().let {
                RenderableRing(it.key.toVec3D().plus(0.5, 0.0, 0.5), it.value.segments, 1, it.value.level)
            } else RenderableRing(
                Vec3D(
                    it.map { it.key.x.toDouble() }.average() + 0.5,
                    it.map { it.key.y.toDouble() }.average(),
                    it.map { it.key.z.toDouble() }.average() + 0.5
                ), it.first().value.segments, it.size, it.sumOf { it.value.level }
            )
        }
    }
}