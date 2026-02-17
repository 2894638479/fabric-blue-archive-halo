package io.github.u2894638479.cache

import io.github.u2894638479.BlueArchiveHaloClient
import io.github.u2894638479.BlueArchiveHaloClient.Companion.logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import kotlin.io.path.pathString


@Serializable
@JvmInline
value class BeaconCacheMapMap(
    val map: MutableMap<WorldKey, BeaconCacheMap> = mutableMapOf(),
) : Map<WorldKey, BeaconCacheMap> by map {
    override fun get(key: WorldKey) = map[key] ?: BeaconCacheMap().also { map[key] = it }
    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            allowStructuredMapKeys = true
        }
        val fileName = "${BlueArchiveHaloClient.id}-beacon-cache.json"
        val filePath = FabricLoader.getInstance().configDir.resolve(fileName)
        val file get() = File(filePath.pathString)

        var instance = loadInstance()
            private set

        private fun loadInstance(): BeaconCacheMapMap = try {
            json.decodeFromString<BeaconCacheMapMap>(file.readText())
        } catch (e: Throwable) {
            logger.error("load cache failed: $e")
            logger.error("creating empty cache")
            BeaconCacheMapMap()
        }

        fun load() { instance = loadInstance() }
        fun save() = file.writeText(json.encodeToString(serializer(),instance))
    }
}