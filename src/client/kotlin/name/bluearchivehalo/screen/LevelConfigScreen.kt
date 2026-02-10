package name.bluearchivehalo.screen

import name.bluearchivehalo.config.LevelConfig
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.gui.widget.SliderWidget
import net.minecraft.text.Text
import kotlin.math.roundToInt


class LevelConfigScreen(parent: Screen,val levelConf: LevelConfig): MyScreen(
    Text.translatable("screen.level_config_screen.level_config_screen", levelConf.level, levelConf.size),parent) {
    override fun init() {
        val listWidget = object: ElementListWidget<WidgetEntry>(client,width,height - 67, 32, height - 35, 25){
            init {
                centerListVertically = false
                setRenderBackground(false)
            }
            public override fun addEntry(entry: WidgetEntry) = super.addEntry(entry)
            override fun getRowWidth() = 310
            override fun getScrollbarPositionX() = width/2 + 140
        }


        levelConf.rings.confirm()
        val left = width/2 - 155
        val colorSpacing = slider(levelConf.colorSpacing,1..10){
            Text.translatable("screen.level_config_screen.color_spacing", levelConf.colorSpacing.get)
        }.apply {
            width = 145
            setPosition(left,0)
            tooltip(Text.translatable("screen.level_config_screen.color_spacing_tooltip"))
        }
        val heightSlider = slider(levelConf.height,levelConf.heightRange){
            Text.translatable("screen.level_config_screen.height_slider", levelConf.height.get.toInt())
        }.apply {
            width = 145
            setPosition(left + 150,0)
        }

        listWidget.addEntry(WidgetEntry(mutableListOf(colorSpacing,heightSlider)))
        levelConf.rings.get.forEach {
            val typeButton = ButtonWidget.builder(it.style.get.text){ button ->
                it.style.field = it.style.get.next
                button.message = it.style.get.text
                button tooltip it.style.get.description
            }.position(left,0).size(50,20).build() tooltip it.style.get.description
            val radius = slider(it.radius,5f..it.maxRadius){Text.translatable("screen.level_config_screen.radius", it.radius.get.toInt())}.apply {
                x = left + 55
                y = 0
                width = 90
            }
            val width = slider(it.width,1f..5f){Text.translatable("screen.level_config_screen.width", String.format("%.2f",it.width.get))}.apply {
                x = left + 150
                y = 0
                width = 45
            }
            fun speed() = it.rotateCycle.get.let {
                if(it == 0) 0.0
                else 400.0/it
            }
            fun speedText() = Text.translatable("screen.level_config_screen.speed", String.format("%.2f",speed()))
            val speed = object: SliderWidget(left + 200,0,90,20,speedText(),speed()/5 + 0.5){
                override fun updateMessage() { message = speedText() }
                override fun applyValue() {
                    val speed = (value - 0.5) * 5
                    val cycle = if(speed == 0.0) 0 else (400/speed).roundToInt()
                    it.rotateCycle.field = cycle
                }
            }
            listWidget.addEntry(WidgetEntry(mutableListOf(
                typeButton,radius,width,speed
            )))
        }
        addDrawableChild(listWidget)
        addDrawableChild(previewButton.also {
            it.width = 150
            it.setPosition(left,height-32)
        })
        addDrawableChild(done.also {
            it.width = 150
            it.setPosition(left + 160,height - 32)
        })
        super.init()
    }

    class WidgetEntry(val widgets: MutableList<ClickableWidget>): ElementListWidget.Entry<WidgetEntry>() {
        override fun render(
            context: DrawContext,
            index: Int,
            y: Int,
            x: Int,
            entryWidth: Int,
            entryHeight: Int,
            mouseX: Int,
            mouseY: Int,
            hovered: Boolean,
            tickDelta: Float
        ) {
            this.widgets.forEach { widget ->
                widget.y = y
                widget.render(context, mouseX, mouseY, tickDelta)
            }
        }
        override fun children() = this.widgets
        override fun selectableChildren() = this.widgets
    }
}
