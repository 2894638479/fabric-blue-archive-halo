package name.bluearchivehalo.screen

import name.bluearchivehalo.config.Config
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.client.gui.widget.SimplePositioningWidget
import net.minecraft.text.Text



class MainScreen(parent: Screen): MyScreen(Text.of("光环设置"),parent) {
    override fun close() {
        client?.setScreen(parent)
        Config.save()
    }
    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
    }
    val chooseLevel = ButtonWidget.builder(Text.of("分等级设置")){
        client?.setScreen(LevelChooseScreen(this))
    }.build() tooltip "不同等级的信标的特定配置"
    val baseAlpha = slider(conf.baseAlpha,0f..1f) { Text.of("基本不透明度") }
    val mixWhite = slider(conf.mixWhite,0f..1f) { Text.of("色调变白") } tooltip "混入一些白色，使得视觉效果更亮"
    val pulseTail = slider(conf.pulseTail,0.1f..1f) { Text.of("脉冲拖尾长度") }
    val spacingMidAlpha = slider(conf.spacingMidAlpha,0f..1f) { Text.of("间隔模式不透明度") } tooltip "间隔模式下，高亮部分的不透明度"
    val spacingAlpha = slider(conf.spacingAlpha,0f..1f) { Text.of("间隔不透明度") } tooltip "间隔模式下，间隔部分的不透明度"
    val spacingCount = slider(conf.spacingCount,4..20) { Text.of("间隔数量:${conf.spacingCount.get}") }



    override fun init() {
        val gridWidget = GridWidget()
        gridWidget.mainPositioner.marginX(5).marginBottom(4).alignHorizontalCenter()
        val adder = gridWidget.createAdder(2)
        listOf(chooseLevel,baseAlpha,mixWhite,pulseTail,spacingMidAlpha,spacingAlpha,spacingCount)
            .forEach { adder.add(it) }
        adder.add(previewButton,2,adder.copyPositioner().marginTop(6))
        adder.add(done,2, adder.copyPositioner().marginTop(6))
        gridWidget.refreshPositions()
        SimplePositioningWidget.setPos(gridWidget, 0, height / 6 - 12,width,height, 0.5f, 0.0f)
        gridWidget.forEachChild(::addDrawableChild)

        super.init()
    }
}