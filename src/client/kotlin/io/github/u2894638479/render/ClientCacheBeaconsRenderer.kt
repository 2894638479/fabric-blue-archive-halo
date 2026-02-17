package io.github.u2894638479.render

import io.github.u2894638479.BlueArchiveHaloClient
import io.github.u2894638479.cache.BeaconCache
import io.github.u2894638479.cache.BeaconCacheMap
import io.github.u2894638479.cache.WorldKey
import io.github.u2894638479.config.Config
import io.github.u2894638479.math.Vec3D
import io.github.u2894638479.math.Vec3L
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Frustum
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d
import kotlin.math.sqrt

class ClientCacheBeaconsRenderer(ctx: EntityRendererFactory.Context?) : EntityRenderer<ClientCacheBeacons>(ctx) {
    override fun getTexture(entity: ClientCacheBeacons?) = BlueArchiveHaloClient.texture
    override fun shouldRender(entity: ClientCacheBeacons?, frustum: Frustum?, x: Double, y: Double, z: Double) = true
    override fun render(entity: ClientCacheBeacons, yaw: Float, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int) {
        if(!Config.instance.special.clientCache) return
        matrices.push()
        val pos = entity.getLerpedPos(tickDelta)
        matrices.translate(-pos.x,-pos.y,-pos.z)
        cachedBeacons().forEach {
            matrices.push()
            matrices.translate(it.pos.x,it.pos.y,it.pos.z)
            matrices.push()
            val scale = sqrt(it.combineNum.toFloat())
            matrices.scale(scale,1f,scale)
            matrices.translate(-0.5,0.0,-0.5)
            var k = 0
            it.segments.forEachIndexed { index, segment ->
                BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumers,
                    tickDelta, entity.world.time, k,
                    if(index == it.segments.size - 1) 1024 else segment.height,
                    segment.run { floatArrayOf(color.rFloat,color.gFloat,color.bFloat) }
                )
                k += segment.height
            }
            matrices.pop()
            Config.instance.levels[it.totalLevel].forEach { info ->
                renderRingAt(vertexConsumers,matrices,info,entity.world.time,tickDelta.toDouble(),it.segments,it.pos.toVec3d())
            }
            matrices.pop()
        }
        matrices.pop()
    }

    override fun getPositionOffset(entity: ClientCacheBeacons?, tickDelta: Float) = Vec3d(0.0,0.0,0.0)

    private fun cachedBeacons(): List<RenderableRing> {
        val map = BeaconCacheMap[WorldKey.current ?: return emptyList()]
        if(!Config.instance.special.combineBeacon) return map.map {
            RenderableRing(it.key.toVec3D().plus(0.5,0.0,0.5),it.value.segments,1,it.value.level)
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
                RenderableRing(it.key.toVec3D().plus(0.5,0.0,0.5), it.value.segments,1,it.value.level)
            } else RenderableRing(
                Vec3D(
                    it.map { it.key.x.toDouble() }.average() + 0.5,
                    it.map { it.key.y.toDouble() }.average(),
                    it.map { it.key.z.toDouble() }.average() + 0.5
                ),it.first().value.segments,it.size,it.sumOf { it.value.level }
            )
        }
    }
}