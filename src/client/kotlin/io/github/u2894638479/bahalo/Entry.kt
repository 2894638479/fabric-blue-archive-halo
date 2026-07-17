package io.github.u2894638479.bahalo

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
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer.MultiPhase
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.util.Identifier
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.chunk.ChunkStatus
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.NeoForge.EVENT_BUS
import org.slf4j.LoggerFactory
import java.util.*

@Mod("blue_archive_halo")
object Entry: DslEntryClient, DslEntryGui {
    init {
        if(FMLEnvironment.dist == Dist.CLIENT){
            ModList.get().getModContainerById("blue_archive_halo").ifPresent { container: ModContainer ->
                container.registerExtensionPoint<IConfigScreenFactory>(IConfigScreenFactory::class.java) {
                    IConfigScreenFactory { _, modListScreen ->
                        dslBackend.create(name) { ConfigPage(false) }.screen as Screen
                    }
                }
            }
        }
    }
    override val name = "Blue Archive Halo"
    override val id = "blue-archive-halo"
    override val icon = ImageHolder("$id:icon.png",16.px,16.px)

    context(ctx: DslContext)
    override fun content() { ConfigPage(true) }
    override fun initializeClient() {
        BlockEntityRendererFactories.register(
            BlockEntityType.BEACON,
            ::BeaconHaloRenderer
        )
        var ticks = 0L
        EVENT_BUS.addListener { event: ClientTickEvent.Post ->
            val minecraft = MinecraftClient.getInstance()
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
        EVENT_BUS.addListener { event: RenderLevelStageEvent ->
            if(event.stage != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return@addListener
            val matrix = event.poseStack ?: return@addListener
            matrix.push()
            val camera = event.camera.pos
            matrix.translate(-camera.x,-camera.y,-camera.z)
            val vc = event.levelRenderer.bufferBuilders.entityVertexConsumers
            ClientCacheBeaconsRenderer.render(event.renderTick.toLong(),event.partialTick.getTickDelta(false),matrix,vc)
            matrix.pop()
        }
    }

    val texture = Identifier.of(id, "textures/pure_white.png")
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