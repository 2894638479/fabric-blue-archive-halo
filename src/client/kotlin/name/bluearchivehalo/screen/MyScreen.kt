package name.bluearchivehalo.screen

import name.bluearchivehalo.config.Conf
import name.bluearchivehalo.config.Config
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ScreenTexts
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.SliderWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import kotlin.math.roundToInt
import kotlin.reflect.KClass

open class MyScreen(title: Text, val parent:Screen?): Screen(title) {
    var pause = true
    override fun shouldPause() = pause
    override fun close() { client?.setScreen(parent) }
    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        drawCenteredTextWithShadow(matrices,textRenderer,title.asOrderedText(),width / 2, 15, 16777215)
        renderTooltips(matrices,mouseX,mouseY,delta)
    }
    val pauseButton get() = ButtonWidget(0,0,200,20,Text.of("暂停中")){
        pause = !pause
        it.message = Text.of(if(pause) "暂停中" else "运行中")
    } tooltip "查看动态效果（会运行游戏内时间）"

    val previewButton get() = ButtonWidget(0,0,200,20,Text.of("预览")){
        client?.setScreen(object : MyScreen(Text.of("预览中，请自行调整游戏内视角"),this){
            val rememberHudStatus = MinecraftClient.getInstance().options.hudHidden
            init { MinecraftClient.getInstance().options.hudHidden = true }
            override fun close() {
                MinecraftClient.getInstance().options.hudHidden = rememberHudStatus
                super.close()
            }
            override fun renderBackground(matrices: MatrixStack?) {}
            override fun init() {
                val left = width/2 - 155
                addDrawableChild(pauseButton.also {
                    it.x = left
                    it.y = height - 32
                    it.width = 150
                })
                addDrawableChild(done.also {
                    it.x = left + 160
                    it.y = height - 32
                    it.width = 150
                })
                super.init()
            }
        })
    }.also { it.active = client?.world != null } tooltip "清空界面，便于预览（仅游戏内）"


    val done get() = ButtonWidget(0,0,200,20, ScreenTexts.DONE) {
        close()
    }

    inline fun <reified T> slider(conf: Conf<T>, range:ClosedRange<T>, noinline text:()->Text)
        where T : Number, T:Comparable<T> = slider(conf,range,text,T::class)

    val conf get() = Config.instance
    fun <T> slider(conf: Conf<T>, range:ClosedRange<T>, text:()-> Text, clazz: KClass<T>): SliderWidget where T : Number, T:Comparable<T>{
        return object: SliderWidget(100,100, 150,20, text(),run {
            val start = range.start.toDouble()
            val end = range.endInclusive.toDouble()
            (conf.get.toDouble() - start) / (end - start)
        }){
            val start get() = range.start.toDouble()
            val end get() = range.endInclusive.toDouble()
            override fun updateMessage() { message = text() }
            override fun applyValue() {
                val value = start + (end-start)*value
                when(clazz){
                    Int::class -> conf.field = value.roundToInt() as T
                    Float::class -> conf.field = value.toFloat() as T
                    Double::class -> conf.field = value as T
                    else -> error("unknown slider type:$clazz")
                }
            }
        }
    }

    val tooltipMap = mutableMapOf<ClickableWidget,Text>()
    infix fun ClickableWidget.tooltip(string:String) = apply {
        tooltipMap[this] = Text.of(string)
    }
    fun renderTooltips(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float){
        tooltipMap.forEach { (widget,text)->
            if(widget.isHovered) renderTooltip(matrices,text,mouseX,mouseY)
        }
    }

    override fun clearChildren() {
        tooltipMap.clear()
        super.clearChildren()
    }

    override fun init() {
        column = 0
        row = 0
        super.init()
    }

    var column = 0
    var row = 0
    fun myAdd(widget: ClickableWidget,fillRow:Boolean = false){
        val top = 32
        widget.width = 150
        if(fillRow) {
            if(row != 0) {
                column++
            }
            widget.y = top + 25*column
            widget.x = (width-widget.width)/2
            column++
            row = 0
        } else {
            if(row == 0){
                row++
                widget.x = (width-310)/2
                widget.y = top+column*25
            } else {
                row = 0
                widget.x = (width+10)/2
                widget.y = top+column*25
                column++
            }
        }
        addDrawableChild(widget)
    }
}