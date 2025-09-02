package name.bluearchivehalo.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import name.bluearchivehalo.SerializerWrapper
import name.bluearchivehalo.config.RingStyle.Companion.PULSE
import net.minecraftforge.fml.loading.FMLPaths
import java.io.File
import kotlin.io.path.pathString
import kotlin.properties.ReadWriteProperty
import kotlin.random.Random
import kotlin.reflect.KProperty
import kotlin.text.set

@Serializable(with = Config.Serializer::class)
class Config {
    companion object {
        val fileName = "blue-archive-halo-config.json"
        val filePath = FMLPaths.CONFIGDIR.get().resolve(fileName)
        val file get() = File(filePath.pathString)
        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }
        fun load() = json.decodeFromString<Config>(try{ file.readText() } catch (_: Throwable){"{}"})
        val instance by lazy { load() }
        fun save() = file.writeText(json.encodeToString(Serializer(),instance))
    }

    val levels = Conf(mutableMapOf<Int,LevelConfig>()){
        it.filter { it.key == it.value.level }.toMutableMap()
    }
    fun getLevelConf(level:Int): LevelConfig{
        levels.confirm()
        levels.get[level]?.let { return it }
        LevelConfig(level).let {
            levels.get[level] = it
            return it
        }
    }
    val baseAlpha = Conf(0.33f,rangeConstraint(0f..1f))
    val spacingMidAlpha = Conf(0.8f,rangeConstraint(0f..1f))
    val spacingAlpha = Conf(0f,rangeConstraint(0f..1f))
    val spacingCount = Conf(8,rangeConstraint(4..20))
    val mixWhite = Conf(0.3f,rangeConstraint(0f..1f))
    val pulseTail = Conf(0.25f,rangeConstraint(0f..1f))
    class Serializer: SerializerWrapper<Config, Serializer.Desc>("Config",Desc()){
        class Desc: Descriptor<Config>() {
            val levels = "levels" from {levels.field}
            val baseAlpha = "baseAlpha" from {baseAlpha.field}
            val spacingAlpha = "spacingAlpha" from {spacingAlpha.field}
            val spacingCount = "spacingCount" from {spacingCount.field}
            val mixWhite = "mixWhite" from {mixWhite.field}
            val pulseTail = "pulseTail" from {pulseTail.field}
        }
        override fun Desc.generate() = Config().also {
            it.levels set levels
            it.baseAlpha set baseAlpha
            it.spacingAlpha set spacingAlpha
            it.spacingCount set spacingCount
            it.mixWhite set mixWhite
            it.pulseTail set pulseTail
        }
    }
}

class Conf<T : Any>(
    val defaultValue:T,
    val constraint:(T)-> T? //if return null, fallback to defaultValue
): ReadWriteProperty<Any?,T?>{
    var field: T? = null
        set(value) {
            if(value != null) constraint(value).let { field = it }
        }
    val get get() = field ?: defaultValue
    fun confirm() { if(field == null) field = defaultValue }
    override fun getValue(thisRef: Any?, property: KProperty<*>) = field
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {field = value}
    infix fun set(value:T?) { field = value }
    infix fun set(item: SerializerWrapper.Descriptor.Item<*,T>) { field = item.nullable }
}
fun <T: Comparable<T>> rangeConstraint(range: ClosedRange<T>):(T)->T = {
    if(it < range.start) range.start
    else if(it > range.endInclusive) range.endInclusive
    else it
}
fun <T: Comparable<T>> rangeConstraint(range:()-> ClosedRange<T>):(T)->T = {
    val range = range()
    if(it < range.start) range.start
    else if(it > range.endInclusive) range.endInclusive
    else it
}

@Serializable(with = LevelConfig.Serializer::class)
class LevelConfig(val level:Int){
    companion object {
        fun ringCount(level:Int) = if(level <= 0) 0 else
            when(level){
                1,2,3,4,5 -> level
                6,7 -> 6
                8,9,10 -> 7
                11,12,13,14,15 -> 8
                else -> 9
            }
    }
    val size:Int get() = ringCount(level)
    val maxRadius get() = level*50 + 100f
    fun maxRadius(index:Int) = if(size == 0) maxRadius else maxRadius/size*(index+1)

    val rings = Conf(MutableList(size){ RingConfig(it,maxRadius(it)) }){
        it.forEach { it.maxRadius = maxRadius(it.ringIndex) }
        if(it.size != size || (it.filterIndexed { index,it-> it.ringIndex != index }.isNotEmpty()))
            MutableList(size){ index ->
                it.firstOrNull { it.ringIndex == index } ?: RingConfig(index,maxRadius(index))
            }
        else it
    }

    val heightRange get() = 150+level*10f..200+level*10f

    val height = Conf(175+level*10f,rangeConstraint(heightRange))

    val colorSpacing = Conf(1,rangeConstraint(1..10))

    class Serializer: SerializerWrapper<LevelConfig, Serializer.Desc>("LevelConfig",Desc()){
        class Desc: Descriptor<LevelConfig>(){
            val rings = "rings" from {rings.field}
            val height = "henght" from {height.field}
            val colorSpacing = "colorSpacing" from {colorSpacing.field}
            val level = "level" from {level}
        }
        override fun Desc.generate() = LevelConfig(level.orElse(0)).also {
            it.rings set rings
            it.height set height
            it.colorSpacing set colorSpacing
        }
    }
}

@Serializable(with = RingConfig.Serializer::class)
class RingConfig(val ringIndex:Int,var maxRadius: Float){

    val radius = Conf(if(ringIndex%2 == 0) 95 + ringIndex*50f else 55 + ringIndex*50f,
        rangeConstraint{5f..maxRadius})

    val rotateCycle = Conf(Random(ringIndex).nextInt(300,400),
        rangeConstraint(-10000..10000))

    val width = Conf(2f,rangeConstraint(1f..5f))

    val style = Conf(PULSE){ it.takeIf { it.isValid } }

    class Serializer: SerializerWrapper<RingConfig, Serializer.Desc>("RingConfig",Desc()){
        class Desc: Descriptor<RingConfig>() {
            val index = "index" from {ringIndex}
            val maxRadius = "maxRadius" from {maxRadius}
            val radius = "r" from {radius.field}
            val rotateCycle = "cycle" from {rotateCycle.field}
            val width = "width" from {width.field}
            val style = "style" from {style.field?.value}
        }
        override fun Desc.generate() = RingConfig(index.orElse(0),maxRadius.orElse(1000f)).also {
            it.radius set radius
            it.rotateCycle set rotateCycle
            it.width set width
            it.style set style.nullable?.let { RingStyle(it) }
        }
    }
}

@JvmInline
value class RingStyle(val value:Int){
    companion object {
        val PULSE = RingStyle(0)
        val SPACING = RingStyle(1)
        val FLAT = RingStyle(2)
        val STATIC = RingStyle(3)
    }
    val isValid get() = value in 0..3
    val next get() = RingStyle((value+1) % 4)
    val text get() = when(this){
        PULSE -> "脉冲"
        SPACING -> "间隔"
        FLAT -> "平凡"
        STATIC -> "不透明"
        else -> "未知"
    }
    val description get() = when(this){
        PULSE -> "脉冲旋转效果。高亮部分由不透明度控制。脉冲最尖端不透明度为1"
        SPACING -> "像虚线一样，间隔亮灭"
        FLAT -> "只有半透明底色，无其他效果"
        STATIC -> "只有不透明底色，无其他效果"
        else -> "未知效果"
    }
}