package io.github.u2894638479.render

import io.github.u2894638479.cache.BeaconCache
import io.github.u2894638479.cache.BeaconCacheMap
import io.github.u2894638479.cache.BeaconCacheMapMap
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.config.ColorSampler
import io.github.u2894638479.config.Config
import io.github.u2894638479.math.Vec3L
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d

class BeaconHaloRenderer(ctx: BlockEntityRendererFactory.Context?) : BeaconBlockEntityRenderer(ctx) {
    override fun render(
        entity: BeaconBlockEntity, tickDelta: Float, matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int
    ) {
        val segments = entity.beamSegments.ifEmpty { return }.map {
            ColorSampler.Segment(it.height, Color(it.color[0], it.color[1], it.color[2]))
        }
        if (Config.instance.special.clientCache) {
            val cachePos = Vec3L(entity.pos)
            val map = BeaconCacheMap.current ?: return
            val newCache = BeaconCache(segments, entity.level)
            if (map[cachePos] != newCache) {
                map[cachePos] = newCache
                BeaconCacheMapMap.save()
            }
            return
        }
        val infos = Config.instance.levels[entity.level]
        matrices.push()
        matrices.translate(0.5, 0.0, 0.5)
        infos.forEach {
            renderRingAt(
                vertexConsumers, matrices, it, entity.world?.time ?: return,
                tickDelta.toDouble(), segments, entity.pos.toCenterPos()
            )
        }
        matrices.pop()
        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay)
    }

    override fun getRenderDistance() =  Int.MAX_VALUE
    override fun isInRenderDistance(beaconBlockEntity: BeaconBlockEntity?, vec3d: Vec3d?) =
        beaconBlockEntity?.isRemoved == false
}