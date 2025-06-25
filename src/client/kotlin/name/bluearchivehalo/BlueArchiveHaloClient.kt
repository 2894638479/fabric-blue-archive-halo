package name.bluearchivehalo

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl
import net.minecraft.block.entity.BlockEntityType

object BlueArchiveHaloClient : ClientModInitializer {
	override fun onInitializeClient() {
		BlockEntityRendererRegistryImpl.register(
			BlockEntityType.BEACON,
			::BeaconHaloRenderer
		)
	}
}