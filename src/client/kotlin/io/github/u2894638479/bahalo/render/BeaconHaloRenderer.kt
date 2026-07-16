package io.github.u2894638479.bahalo.render

import com.mojang.blaze3d.vertex.PoseStack
import io.github.u2894638479.bahalo.cache.BeaconCache
import io.github.u2894638479.bahalo.cache.BeaconCacheMap
import io.github.u2894638479.bahalo.cache.BeaconCacheMapMap
import io.github.u2894638479.bahalo.config.ColorSampler
import io.github.u2894638479.bahalo.config.Config
import io.github.u2894638479.bahalo.math.Vec3L
import io.github.u2894638479.kotlinmcui.math.Color
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BeaconRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.state.BeaconRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BeaconBlockEntity
import net.minecraft.world.phys.Vec3

class BeaconHaloRenderer : BlockEntityRenderer<BeaconBlockEntity, BeaconHaloRenderer.State> {
    val delegate = BeaconRenderer<BeaconBlockEntity>()
    class State: BeaconRenderState() {
        var level: Level? = null
        var levels = 0
        var tick = 0L
        var tickDelta = 0.0
    }
    override fun createRenderState() = State()
    override fun extractRenderState(
        entity: BeaconBlockEntity,
        state: State,
        partialTicks: Float,
        cameraPosition: Vec3,
        breakProgress: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        delegate.extractRenderState(entity,state,partialTicks, cameraPosition, breakProgress)
        state.level = entity.level
        state.levels = entity.levels
        state.tick = entity.level?.gameTime ?: 0L
        state.tickDelta = partialTicks.toDouble()
    }

    override fun submit(
        state: State,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        if(!Config.instance.special.enableBeaconHalos) {
            return delegate.submit(state, poseStack, submitNodeCollector, camera)
        }
        val segments = state.sections.ifEmpty { return }.map {
            ColorSampler.Segment(it.height, Color.ofARGB(it.color))
        }
        if(!shouldRender(state.blockPos,state.levels,segments)) return
        context(RenderParam(submitNodeCollector,poseStack,state.tick,state.tickDelta)) {
            render(state.levels,segments)
        }
        delegate.submit(state, poseStack, submitNodeCollector, camera)
    }

    fun shouldRender(pos: BlockPos, level: Int, segments: List<ColorSampler.Segment>): Boolean {
        if (Config.instance.special.clientCache) {
            val cachePos = Vec3L(pos)
            val map = BeaconCacheMap.current ?: return false
            val newCache = BeaconCache(segments, level)
            if (map[cachePos] != newCache) {
                map[cachePos] = newCache
                BeaconCacheMapMap.save()
            }
            return false
        }
        return true
    }

    context(rp: RenderParam)
    fun render(level: Int,segments:List<ColorSampler.Segment>) {
        stack {
            ms.translate(0.5, 0.0, 0.5)
            val infos = Config.instance.levels[level]
            infos.forEach {
                renderRingInfo(it, segments)
            }
        }
    }

    override fun getViewDistance() = Int.MAX_VALUE
    override fun shouldRender(blockEntity: BeaconBlockEntity, cameraPosition: Vec3): Boolean {
        return !blockEntity.isRemoved
    }
    override fun shouldRenderOffScreen() = true
}