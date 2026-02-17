package io.github.u2894638479.bahalo

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import io.github.u2894638479.bahalo.cache.BeaconCacheMap
import io.github.u2894638479.bahalo.cache.BeaconCacheMapMap
import io.github.u2894638479.kotlinmcui.backend.DslEntryService
import io.github.u2894638479.kotlinmcui.backend.createScreen
import io.github.u2894638479.kotlinmcui.dslBackend
import io.github.u2894638479.kotlinmcui.image.ImageHolder
import io.github.u2894638479.kotlinmcui.math.px
import io.github.u2894638479.bahalo.config.ConfigPage
import io.github.u2894638479.bahalo.render.BeaconHaloRenderer
import io.github.u2894638479.bahalo.render.ClientCacheBeacons
import io.github.u2894638479.bahalo.render.ClientCacheBeaconsRenderer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl
import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer.MultiPhase
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.chunk.ChunkStatus
import org.slf4j.LoggerFactory
import java.util.Optional

class Entry: DslEntryService, ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory {
        dslBackend.createScreen { ConfigPage(false) }.screen as Screen
    }
    override val name = "Blue Archive Halo"
    override val id = Companion.id
    override val icon = ImageHolder("$id:icon.png",16.px,16.px)
    override fun createScreen() = dslBackend.createScreen { ConfigPage(true) }
    override fun initialize() {
        BlockEntityRendererRegistryImpl.register(
            BlockEntityType.BEACON,
            ::BeaconHaloRenderer
        )
        val entityType = ClientCacheBeacons.register()
        var ticks = 0L
        EntityRendererRegistryImpl.register(entityType,::ClientCacheBeaconsRenderer)
        ClientTickEvents.END_CLIENT_TICK.register { minecraft ->
            if(entity?.let { it.world != minecraft.world || it.isRemoved } ?: true) {
                minecraft.world?.let {
                    entity = ClientCacheBeacons(entityType, it).apply { it.addEntity(id,this) }
                } ?: run { entity = null }
            }
            minecraft.player?.let {
                entity?.setPosition(it.pos)
            }
            if(ticks % 20L == 0L) {
                minecraft.world?.let { world ->
                    val modified = BeaconCacheMap.current?.keys?.removeIf {
                        val pos = it.toBlockPos()
                        world.chunkManager.getChunk(
                            ChunkSectionPos.getSectionCoord(pos.x),
                            ChunkSectionPos.getSectionCoord(pos.z),
                            ChunkStatus.FULL,false
                        ) != null && world.getBlockEntity(pos) !is BeaconBlockEntity
                    }
                    if(modified == true) BeaconCacheMapMap.save()
                }
            }
            ticks++
        }
    }

    companion object {
        val id = "blue-archive-halo"
        val texture = Identifier(id, "textures/pure_white.png")
        val logger = LoggerFactory.getLogger(id)
        var entity: ClientCacheBeacons? = null

        fun MultiPhase.modifyMultiPhase(name: String?, phases: MultiPhaseParameters) {
            if (name != "beacon_beam") return
            if (phases.texture.id.get() != texture) return
            affectedOutline = Optional.empty()
            this.phases = MultiPhaseParameters.Builder()
                .cull(RenderPhase.ENABLE_CULLING)
                .lightmap(RenderPhase.DISABLE_LIGHTMAP)
                .program(RenderPhase.ShaderProgram { GameRenderer.getRenderTypeBeaconBeamProgram() })
                .texture(phases.texture)
                .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                .build(false)
            this.vertexFormat = VertexFormats.POSITION_COLOR
            this.drawMode = DrawMode.TRIANGLE_STRIP
            beginAction = Runnable { this.phases.phases.forEach(RenderPhase::startDrawing) }
            endAction = Runnable { this.phases.phases.forEach(RenderPhase::endDrawing) }
        }
    }
}