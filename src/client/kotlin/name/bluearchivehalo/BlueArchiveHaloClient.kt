package name.bluearchivehalo

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.render.*
import net.minecraft.client.render.RenderLayer.MultiPhase
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.util.Identifier
import java.util.*


object BlueArchiveHaloClient : ClientModInitializer {
	val id = "blue-archive-halo"
	override fun onInitializeClient() {
		BlockEntityRendererRegistryImpl.register(
			BlockEntityType.BEACON,
			::BeaconHaloRenderer
		)
	}
	val texture = Identifier(id,"textures/pure_white.png")
	fun MultiPhase.modifyMultiPhase (
		name: String?,
		vertexFormat: VertexFormat?,
		drawMode: DrawMode?,
		expectedBufferSize: Int,
		hasCrumbling: Boolean,
		translucent: Boolean,
		phases: MultiPhaseParameters
	) {
		if (name != "beacon_beam") return
		if (phases.texture.id.get() != texture) return
		affectedOutline = Optional.empty()
		this.phases = MultiPhaseParameters.Builder()
			.cull(RenderPhase.ENABLE_CULLING)
			.lightmap(RenderPhase.DISABLE_LIGHTMAP)
			.shader(RenderPhase.BEACON_BEAM_SHADER)
			.texture(phases.texture)
			.transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
			.build(false)
		this.vertexFormat = VertexFormats.POSITION_COLOR
		this.drawMode = DrawMode.TRIANGLE_STRIP
		beginAction = Runnable { this.phases.phases.forEach(RenderPhase::startDrawing) }
		endAction = Runnable { this.phases.phases.forEach(RenderPhase::endDrawing) }
	}
}