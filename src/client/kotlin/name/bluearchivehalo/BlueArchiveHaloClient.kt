package name.bluearchivehalo

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.Identifier


object BlueArchiveHaloClient : ClientModInitializer {
	val id = "blue-archive-halo"
    var shrinker:((BeaconBlockEntity) -> Int)? = null
        private set
	override fun onInitializeClient() {
		BlockEntityRendererRegistryImpl.register(
			BlockEntityType.BEACON,
			::BeaconHaloRenderer
		)
        val shrinkers = FabricLoader.getInstance().getEntrypoints("blue_archive_halo_beacon_level_shrinker",Function1::class.java)
        shrinkers.firstOrNull()?.let { shrinker = it as ((BeaconBlockEntity) -> Int) }
	}
	val texture = Identifier.of(id,"textures/pure_white.png")
}