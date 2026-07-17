package io.github.u2894638479.bahalo.config

import io.github.u2894638479.bahalo.Entry
import io.github.u2894638479.bahalo.Entry.logger
import io.github.u2894638479.kotlinmcui.backend.dslBackend
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.io.path.pathString

@Serializable
class Config {
    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }
        val file get() = File(dslBackend.configDir.resolve("${Entry.id}-config.json").pathString)

        var instance = loadInstance()
            private set

        private fun loadInstance(): Config = try {
            json.decodeFromString<Config>(file.readText()).check()
        } catch (e: Throwable) {
            logger.error("load config failed: $e")
            logger.error("creating empty config")
            Config()
        }

        fun load() { instance = loadInstance() }
        fun save() = file.writeText(json.encodeToString(serializer(),instance.check()))
    }

    fun check() = apply {
        levels.check(special.bonus)
        players.check(special.bonus)
    }

    val special = Special()
    val levels = BeaconLevelRings()
    val players = PlayerRings()
}