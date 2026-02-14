package name.bluearchivehalo.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import name.bluearchivehalo.BlueArchiveHaloClient
import name.bluearchivehalo.BlueArchiveHaloClient.Companion.logger
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import kotlin.io.path.pathString

@Serializable
class Config {
    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }
        val fileName = "${BlueArchiveHaloClient.id}-config.json"
        val filePath = FabricLoader.getInstance().configDir.resolve(fileName)
        val file get() = File(filePath.pathString)

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
        levels.keys.removeIf { it <= 0 }
        levels.entries.forEach { (level,list) ->
            val max = ringNum(level)
            if(list.size > max) list.subList(max,list.size).clear()
        }
    }

    fun ringNum(level: Int): Int {
        if(special.bonus) return level + 3
        return level + 1
    }

    fun ringRadiusRange(level: Int) = 50.0..(ringNum(level) * 50 + 100.0)

    fun ringHeightRange(level: Int) = 0.0..ringRadiusRange(level).endInclusive

    fun defaultRings(level: Int) = MutableList(ringNum(level)) {
        val range = ringRadiusRange(level)
        val radius = range.start + it * (range.endInclusive - range.start) / ringNum(level)
        RingInfo().also { it.radius = radius }
    }

    fun rings(level:Int) = levels[level] ?: run {
        defaultRings(level).also{
            levels[level] = it
            save()
        }
    }

    val special = Special()
    val levels = mutableMapOf<Int, MutableList<RingInfo>>()
}