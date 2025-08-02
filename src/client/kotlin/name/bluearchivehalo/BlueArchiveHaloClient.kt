package name.bluearchivehalo

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.Identifier


object BlueArchiveHaloClient : ClientModInitializer {
	val id = "blue-archive-halo"
	override fun onInitializeClient() {
		BlockEntityRendererRegistryImpl.register(
			BlockEntityType.BEACON,
			::BeaconHaloRenderer
		)
	}
	val texture = Identifier.of(id,"textures/pure_white.png")
}