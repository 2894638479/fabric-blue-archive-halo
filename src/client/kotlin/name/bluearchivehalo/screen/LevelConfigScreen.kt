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

class LevelConfigScreen(parent: Screen, val levelConf: LevelConfig) : MyScreen(
    Text.translatable("text.fabric-blue-archive-halo.title.level_config", levelConf.level, levelConf.size),
    parent
) {
    override fun init() {
        val listWidget = object : ElementListWidget<WidgetEntry>(client, width, height - 67, 32, 25) {
            init {
                centerListVertically = false
            }
            public override fun addEntry(entry: WidgetEntry) = super.addEntry(entry)
            override fun getRowWidth() = 360 // достаточно места
            override fun getScrollbarX() = width / 2 + 195 // смещаем скроллбар
        }

        levelConf.rings.confirm()
        val left = width / 2 - 155

        // Слайдеры (локализованы)
        val colorSpacing = slider(levelConf.colorSpacing, 1..10) {
            Text.translatable("text.fabric-blue-archive-halo.slider.color_spacing", levelConf.colorSpacing.get)
        }.apply {
            width = 145
            setPosition(left, 0)
            tooltip(Text.translatable("text.fabric-blue-archive-halo.tooltip.color_spacing").string)
        }

        val heightSlider = slider(levelConf.height, levelConf.heightRange) {
            Text.translatable("text.fabric-blue-archive-halo.slider.height", levelConf.height.get.toInt())
        }.apply {
            width = 145
            setPosition(left + 150, 0)
        }

        listWidget.addEntry(WidgetEntry(mutableListOf(colorSpacing, heightSlider)))

        // Кольца
        levelConf.rings.get.forEachIndexed { index, ringConfig ->
            // Кнопка стиля с локализацией
            val typeButton = ButtonWidget.builder(
                Text.translatable(ringConfig.style.get.translationKey)
            ) { button ->
                ringConfig.style.field = ringConfig.style.get.next
                button.message = Text.translatable(ringConfig.style.get.translationKey)
                button.tooltip(Text.translatable(ringConfig.style.get.descriptionKey).string)
            }.position(left, 0).size(50, 20).build().apply {
                tooltip(Text.translatable(ringConfig.style.get.descriptionKey).string)
            }

            // Слайдер радиуса
            val radius = slider(ringConfig.radius, 5f..ringConfig.maxRadius) {
                Text.translatable("text.fabric-blue-archive-halo.slider.radius", ringConfig.radius.get.toInt())
            }.apply {
                x = left + 55
                y = 0
                width = 90
            }

            // Слайдер ширины
            val widthSlider = slider(ringConfig.width, 1f..5f) {
                Text.translatable("text.fabric-blue-archive-halo.slider.width", String.format("%.2f", ringConfig.width.get))
            }.apply {
                x = left + 150
                y = 0
                width = 45
            }

            // Слайдер скорости
            fun speed() = ringConfig.rotateCycle.get.let {
                if (it == 0) 0.0 else 400.0 / it
            }
            fun speedText() = Text.translatable("text.fabric-blue-archive-halo.slider.speed", String.format("%.2f", speed()))
            val speedSlider = object : SliderWidget(left + 200, 0, 90, 20, speedText(), speed() / 5 + 0.5) {
                override fun updateMessage() { message = speedText() }
                override fun applyValue() {
                    val speed = (value - 0.5) * 5
                    val cycle = if (speed == 0.0) 0 else (400 / speed).roundToInt()
                    ringConfig.rotateCycle.field = cycle
                }
            }

            // Кнопка добавления кольца
            val addButton = ButtonWidget.builder(
                Text.translatable("text.fabric-blue-archive-halo.button.add_ring")
            ) {
                println("Add ring clicked")
                levelConf.addRing()
                client?.setScreen(LevelConfigScreen(parent, levelConf))
            }.size(20, 20).position(left + 295, 0).build()

            // Кнопка удаления кольца
            val removeButton = ButtonWidget.builder(
                Text.translatable("text.fabric-blue-archive-halo.button.remove_ring")
            ) {
                println("Remove ring clicked for index $index")
                levelConf.removeRing(index)
                client?.setScreen(LevelConfigScreen(parent, levelConf))
            }.size(20, 20).position(left + 320, 0).build()

            listWidget.addEntry(
                WidgetEntry(
                    mutableListOf(
                        typeButton,
                        radius,
                        widthSlider,
                        speedSlider,
                        addButton,
                        removeButton
                    )
                )
            )
        }

        addDrawableChild(listWidget)
        addDrawableChild(previewButton.also {
            it.width = 150
            it.setPosition(left, height - 32)
        })
        addDrawableChild(done.also {
            it.width = 150
            it.setPosition(left + 160, height - 32)
        })

        super.init()
    }

    class WidgetEntry(val widgets: MutableList<ClickableWidget>) : ElementListWidget.Entry<WidgetEntry>() {
        override fun render(context: DrawContext, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
            this.widgets.forEach { widget ->
                widget.y = y
                widget.render(context, mouseX, mouseY, tickDelta)
            }
        }
        override fun children() = this.widgets
        override fun selectableChildren() = this.widgets
    }
}