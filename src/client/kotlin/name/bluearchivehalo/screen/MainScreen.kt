package name.bluearchivehalo.screen

import name.bluearchivehalo.config.Config
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text


class MainScreen(parent: Screen): MyScreen(Text.of("光环设置"),parent) {
    override fun close() {
        client?.setScreen(parent)
        Config.save()
    }
    val chooseLevel get() = ButtonWidget(0,0,100,20,Text.of("分等级设置")){
        client?.setScreen(LevelChooseScreen(this))
    } tooltip "不同等级的信标的特定配置"
    val baseAlpha get() = slider(conf.baseAlpha,0f..1f) { Text.of("基本不透明度") }
    val mixWhite get() = slider(conf.mixWhite,0f..1f) { Text.of("色调变白") } tooltip "混入一些白色，使得视觉效果更亮"
    val pulseTail get() = slider(conf.pulseTail,0.1f..1f) { Text.of("脉冲拖尾长度") }
    val spacingMidAlpha get() = slider(conf.spacingMidAlpha,0f..1f) { Text.of("间隔模式不透明度") } tooltip "间隔模式下，高亮部分的不透明度"
    val spacingAlpha get() = slider(conf.spacingAlpha,0f..1f) { Text.of("间隔不透明度") } tooltip "间隔模式下，间隔部分的不透明度"
    val spacingCount get() = slider(conf.spacingCount,4..20) { Text.of("间隔数量:${conf.spacingCount.get}") }



    override fun init() {
        listOf(chooseLevel,baseAlpha,mixWhite,pulseTail,spacingMidAlpha,spacingAlpha,spacingCount)
            .forEach { myAdd(it) }
        myAdd(previewButton,true)
        myAdd(done,true)
        super.init()
    }
}