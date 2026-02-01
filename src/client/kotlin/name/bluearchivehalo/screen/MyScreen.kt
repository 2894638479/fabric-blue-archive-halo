package name.bluearchivehalo.screen

import name.bluearchivehalo.config.Conf
import name.bluearchivehalo.config.Config
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.SliderWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import kotlin.math.roundToInt
import kotlin.reflect.KClass

open class MyScreen(title: Text, val parent:Screen): Screen(title) {
    var pause = true
    override fun shouldPause() = pause
    override fun close() { client?.setScreen(parent) }
    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        super.render(context, mouseX, mouseY, delta)
        context.drawCenteredTextWithShadow(textRenderer,title,width / 2, 15, 16777215)
    }
    val pauseButton get() = ButtonWidget.builder(Text.translatable("screen.my_screen.pause_button")){
        pause = !pause
        it.message = if(pause) Text.translatable("screen.my_screen.pause_button") else Text.translatable("screen.my_screen.running")
    }.width(200).build() tooltip Text.translatable("screen.my_screen.view_dynamic_effects")

    val previewButton get() = ButtonWidget.builder(Text.translatable("screen.my_screen.preview_button")){
        client?.setScreen(object : MyScreen(Text.translatable("screen.my_screen.view_angle_tip"),this){
            val rememberHudStatus = MinecraftClient.getInstance().options.hudHidden
            init { MinecraftClient.getInstance().options.hudHidden = true }
            override fun close() {
                MinecraftClient.getInstance().options.hudHidden = rememberHudStatus
                super.close()
            }
            override fun renderBackground(context: DrawContext) {}
            override fun init() {
                val left = width/2 - 155
                addDrawableChild(pauseButton.also {
                    it.setPosition(left,height - 32)
                    it.width = 150
                })
                addDrawableChild(done.also {
                    it.setPosition(left + 160,height - 32)
                    it.width = 150
                })
                super.init()
            }
        })
    }.width(200).build().also { it.active = client?.world != null } tooltip Text.translatable("screen.my_screen.preview_button_tooltip")


    val done get() = ButtonWidget.builder(ScreenTexts.DONE) {
        close()
    }.width(200).build()

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
    infix fun ClickableWidget.tooltip(string: MutableText?) = apply { tooltip = Tooltip.of(string) }
}