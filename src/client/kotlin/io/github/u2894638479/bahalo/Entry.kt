package io.github.u2894638479.bahalo

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import io.github.u2894638479.bahalo.cache.BeaconCacheMap
import io.github.u2894638479.bahalo.cache.BeaconCacheMapMap
import io.github.u2894638479.bahalo.config.Config
import io.github.u2894638479.bahalo.config.ConfigPage
import io.github.u2894638479.bahalo.render.BeaconHaloRenderer
import io.github.u2894638479.bahalo.render.ClientCacheBeaconsRenderer
import io.github.u2894638479.kotlinmcui.backend.dslBackend
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.entry.DslEntryClient
import io.github.u2894638479.kotlinmcui.entry.DslEntryGui
import io.github.u2894638479.kotlinmcui.image.ImageHolder
import io.github.u2894638479.kotlinmcui.math.px
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl
import net.fabricmc.loader.api.FabricLoader
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
import java.nio.file.Path
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
            BlockEntityType.BEACON,
            ::BeaconHaloRenderer
        )
        var ticks = 0L
        ClientTickEvents.END_CLIENT_TICK.register { minecraft ->
            if(ticks % 20L == 0L) {
                minecraft.world?.let { world ->
                    val modified = BeaconCacheMap.current?.keys?.removeIf {
                        val pos = it.toBlockPos()
                        val x = ChunkSectionPos.getSectionCoord(pos.x)
                        val z = ChunkSectionPos.getSectionCoord(pos.z)
                        val loaded = (x-1..x+1).zip(z-1..z+1).all { (x,z) ->
                            world.chunkManager.getChunk(x,z, ChunkStatus.FULL,false) != null
                        }
                        if(!loaded) return@removeIf false
                        val beacon = world.getBlockEntity(pos) as? BeaconBlockEntity ?: return@removeIf true
                        if(beacon.level == 0) {
                            val level = BeaconBlockEntity.updateLevel(world,beacon.pos.x,beacon.pos.y,beacon.pos.z)
                            beacon.level = level
                            if(level == 0) return@removeIf true
                        }
                        false
                    }
                    if(modified == true) BeaconCacheMapMap.save()
                }
            }
            ticks++
        }
        WorldRenderEvents.AFTER_ENTITIES.register {
            it.matrixStack().push()
            val camera = it.camera().pos
            it.matrixStack().translate(-camera.x,-camera.y,-camera.z)
            ClientCacheBeaconsRenderer.render(it.world().time,it.tickDelta(),it.matrixStack(),it.consumers() ?: return@register)
            it.matrixStack().pop()
        }
    }

    val texture = Identifier(id, "textures/pure_white.png")
    val logger = LoggerFactory.getLogger(id)

    fun MultiPhase.modifyMultiPhase(name: String?, phases: MultiPhaseParameters) {
        if (name != "beacon_beam") return
        if (phases.texture.id.get() != texture) return
        affectedOutline = Optional.empty()
        val config = Config.instance.special
        this.phases = MultiPhaseParameters.Builder()
            .cull(RenderPhase.ENABLE_CULLING)
            .lightmap(RenderPhase.DISABLE_LIGHTMAP)
            .program(RenderPhase.ShaderProgram { GameRenderer.getRenderTypeBeaconBeamProgram() })
            .texture(phases.texture)
            .transparency(if(config.transparency) RenderPhase.TRANSLUCENT_TRANSPARENCY else RenderPhase.GLINT_TRANSPARENCY)
            .writeMaskState(if(config.depthWrite) RenderPhase.WriteMaskState.ALL_MASK else RenderPhase.WriteMaskState.COLOR_MASK)
            .build(false)
        this.vertexFormat = VertexFormats.POSITION_COLOR
        this.drawMode = DrawMode.TRIANGLE_STRIP
        beginAction = Runnable { this.phases.phases.forEach(RenderPhase::startDrawing) }
        endAction = Runnable { this.phases.phases.forEach(RenderPhase::endDrawing) }
    }
}