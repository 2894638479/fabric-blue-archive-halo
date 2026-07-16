package io.github.u2894638479.bahalo

import com.mojang.blaze3d.GpuFormat
import com.mojang.blaze3d.PrimitiveTopology
import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.ColorTargetState
import com.mojang.blaze3d.pipeline.DepthStencilState
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.CompareOp
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import io.github.u2894638479.bahalo.cache.BeaconCacheMap
import io.github.u2894638479.bahalo.cache.BeaconCacheMapMap
import io.github.u2894638479.bahalo.config.Config
import io.github.u2894638479.bahalo.config.ConfigPage
import io.github.u2894638479.bahalo.render.BeaconHaloRenderer
import io.github.u2894638479.kotlinmcui.backend.dslBackend
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.entry.DslEntryClient
import io.github.u2894638479.kotlinmcui.entry.DslEntryGui
import io.github.u2894638479.kotlinmcui.image.ImageHolder
import io.github.u2894638479.kotlinmcui.math.px
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.core.SectionPos
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.entity.BeaconBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTypes
import net.minecraft.world.level.chunk.status.ChunkStatus
import org.slf4j.LoggerFactory
import java.util.*

object Entry: DslEntryClient, DslEntryGui, ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory {
        dslBackend.create(name) { ConfigPage(false) }.screen as Screen
    }
    override val name = "Blue Archive Halo"
    override val id = "blue-archive-halo"
    override val icon = ImageHolder("$id:icon.png",16.px,16.px)

    context(ctx: DslContext)
    override fun content() { ConfigPage(true) }
    override fun initializeClient() {
        BlockEntityRendererRegistryImpl.register(
            BlockEntityTypes.BEACON,
            { BeaconHaloRenderer() }
        )
        var ticks = 0L
        ClientTickEvents.END_CLIENT_TICK.register { minecraft ->
            if(ticks % 20L == 0L) {
                minecraft.level?.let { world ->
                    val modified = BeaconCacheMap.current?.keys?.removeIf {
                        val pos = it.toBlockPos()
                        val x = SectionPos.blockToSectionCoord(pos.x)
                        val z = SectionPos.blockToSectionCoord(pos.z)
                        val loaded = (x-1..x+1).zip(z-1..z+1).all { (x,z) ->
                            world.chunkSource.getChunk(x,z, ChunkStatus.FULL,false) != null
                        }
                        if(!loaded) return@removeIf false
                        val beacon = world.getBlockEntity(pos) as? BeaconBlockEntity ?: return@removeIf true
                        if(beacon.levels == 0) {
                            val level = BeaconBlockEntity.updateBase(world,beacon.blockPos.x,beacon.blockPos.y,beacon.blockPos.z)
                            beacon.levels = level
                            if(level == 0) return@removeIf true
                        }
                        false
                    }
                    if(modified == true) BeaconCacheMapMap.save()
                }
            }
            ticks++
        }
    }

    val texture = Identifier.tryBuild(id, "textures/pure_white.png")!!
    val logger = LoggerFactory.getLogger(id)

    fun RenderSetup.modifyRenderSetup(textures: Map<String, RenderSetup.TextureBinding>) {
        if (textures["Sampler0"]?.location != texture) return
        pipeline = RenderPipeline.builder(RenderPipelines.BEACON_BEAM_SNIPPET)
            .withLocation("pipeline/bahalo")
            .withDepthStencilState(DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
            .build()
        pipeline.vertexFormatPerBuffer[0] = DefaultVertexFormat.POSITION_COLOR
        pipeline.primitiveTopology = PrimitiveTopology.TRIANGLE_STRIP
        pipeline.cull = true
        useLightmap = false
        sortOnUpload = false
        val config = Config.instance.special
        val biFunction = if(config.transparency) BlendFunction.TRANSLUCENT else BlendFunction.GLINT
        val colorMask = if(config.depthWrite) ColorTargetState.WRITE_ALL else ColorTargetState.WRITE_COLOR
        pipeline.colorTargetStates = arrayOf(ColorTargetState(Optional.of(biFunction), GpuFormat.RGBA8_UNORM,colorMask))
    }
}