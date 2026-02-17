package io.github.u2894638479.cache

import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.util.WorldSavePath
import kotlin.io.path.relativeToOrNull

@Serializable
data class WorldKey(
    val type: String,
    val dimension: String,
    val location: String
)  {
    companion object {
        val current: WorldKey? get() {
            val client = MinecraftClient.getInstance()
            val world = client.world ?: return null
            val dimension = world.dimensionKey.value.toString()
            val server = client.server
            val type: String
            val location: String
            if(server != null) {
                type = "local"
                val runDirectory = client.runDirectory.toPath()
                val worldDirectory = server.getSavePath(WorldSavePath.ROOT)
                val relative = worldDirectory.relativeToOrNull(runDirectory) ?: worldDirectory
                location = relative.toString()
            } else {
                type = "server"
                location = client.currentServerEntry?.address ?: "null"
            }
            return WorldKey(type,dimension,location)
        }
    }
}