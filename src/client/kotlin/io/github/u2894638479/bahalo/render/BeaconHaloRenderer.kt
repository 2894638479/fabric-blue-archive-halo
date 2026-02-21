package io.github.u2894638479.bahalo.render

import io.github.u2894638479.bahalo.cache.BeaconCache
import io.github.u2894638479.bahalo.cache.BeaconCacheMap
import io.github.u2894638479.bahalo.cache.BeaconCacheMapMap
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.bahalo.config.ColorSampler
import io.github.u2894638479.bahalo.config.Config
import io.github.u2894638479.bahalo.math.Vec3L
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
        if(!shouldRender(entity,segments)) return
        context(RenderParam(vertexConsumers,matrices,entity.world?.time ?: return,tickDelta)) {
            render(entity,segments)
        }
        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay)
    }

    fun shouldRender(entity: BeaconBlockEntity,segments: List<ColorSampler.Segment>): Boolean {
        if (Config.instance.special.clientCache) {
            val cachePos = Vec3L(entity.pos)
            val map = BeaconCacheMap.current ?: return false
            val newCache = BeaconCache(segments, entity.level)
            if (map[cachePos] != newCache) {
                map[cachePos] = newCache
                BeaconCacheMapMap.save()
            }
            return false
        }
        return true
    }

    context(rp: RenderParam)
    fun render(entity: BeaconBlockEntity,segments:List<ColorSampler.Segment>) {
        stack {
            ms.translate(0.5, 0.0, 0.5)
            val infos = Config.instance.levels[entity.level]
            infos.forEach {
                renderRingInfo(it, segments)
            }
        }
    }

    override fun getRenderDistance() =  Int.MAX_VALUE
    override fun isInRenderDistance(beaconBlockEntity: BeaconBlockEntity?, vec3d: Vec3d?) =
        beaconBlockEntity?.isRemoved == false
}