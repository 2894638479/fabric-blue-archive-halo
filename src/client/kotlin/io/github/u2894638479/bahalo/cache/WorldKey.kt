package io.github.u2894638479.bahalo.cache

import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.world.level.storage.LevelResource
import kotlin.io.path.relativeToOrNull

@Serializable
data class WorldKey(
    val type: String,
    val dimension: String,
    val location: String
)  {
    companion object {
        val current: WorldKey? get() {
            val client = Minecraft.getInstance()
            val world = client.level ?: return null
            val dimension = world.dimension().identifier().toString()
            val server = client.singleplayerServer
            val type: String
            val location: String
            if(server != null) {
                type = "local"
                val runDirectory = client.gameDirectory.toPath()
                val worldDirectory = server.getWorldPath(LevelResource.ROOT)
                val relative = worldDirectory.relativeToOrNull(runDirectory) ?: worldDirectory
                location = relative.toString()
            } else {
                type = "server"
                location = client.currentServer?.ip ?: "null"
            }
            return WorldKey(type,dimension,location)
        }
    }
}