package io.github.u2894638479

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import io.github.u2894638479.kotlinmcui.backend.DslEntryService
import io.github.u2894638479.kotlinmcui.backend.createScreen
import io.github.u2894638479.kotlinmcui.dslBackend
import io.github.u2894638479.kotlinmcui.image.ImageHolder
import io.github.u2894638479.kotlinmcui.math.px
import io.github.u2894638479.config.ConfigPage
import io.github.u2894638479.render.BeaconHaloRenderer
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer.MultiPhase
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import java.util.Optional

class BlueArchiveHaloClient: DslEntryService, ModMenuApi {
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
    }

    companion object {
        val id = "blue-archive-halo"
        val texture = Identifier(id, "textures/pure_white.png")
        val logger = LoggerFactory.getLogger(id)

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