package name.bluearchivehalo

import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.util.Identifier


object BlueArchiveHaloClient {
	val id = "blue-archive-halo"
    var shrinker:((BeaconBlockEntity) -> Int)? = null
        private set
	fun onInitializeClient() {
        BlockEntityRendererFactories.register(
			BlockEntityType.BEACON,
			::BeaconHaloRenderer
		)
	}
	val texture = Identifier.of(id,"textures/pure_white.png")
}