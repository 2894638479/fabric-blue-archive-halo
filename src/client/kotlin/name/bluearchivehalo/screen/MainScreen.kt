package name.bluearchivehalo.screen

import name.bluearchivehalo.config.Config
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.client.gui.widget.SimplePositioningWidget
import net.minecraft.text.Text



class MainScreen(parent: Screen): MyScreen(Text.translatable("screen.main_screen.halo_settings"),parent) {
    override fun close() {
        client?.setScreen(parent)
        Config.save()
    }
    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
    }
    val chooseLevel = ButtonWidget.builder(Text.translatable("screen.main_screen.choose_level")){
        client?.setScreen(LevelChooseScreen(this))
    }.build() tooltip_2 Text.translatable("screen.main_screen.choose_level_tooltip")
    val baseAlpha = slider(conf.baseAlpha,0f..1f) { Text.translatable("screen.main_screen.base_alpha") }
    val mixWhite = slider(conf.mixWhite,0f..1f) { Text.translatable("screen.main_screen.mix_white") } tooltip_2 Text.translatable("screen.main_screen.mix_white_tooltip")
    val pulseTail = slider(conf.pulseTail,0.1f..1f) { Text.translatable("screen.main_screen.pulse_tail") }
    val spacingMidAlpha = slider(conf.spacingMidAlpha,0f..1f) { Text.translatable("screen.main_screen.spacing_mid_alpha") } tooltip_2 Text.translatable("screen.main_screen.spacing_mid_alpha_tooltip")
    val spacingAlpha = slider(conf.spacingAlpha,0f..1f) { Text.translatable("screen.main_screen.spacing_alpha") } tooltip_2 Text.translatable("screen.main_screen.spacing_alpha_tooltip")
    val spacingCount = slider(conf.spacingCount,4..20) { Text.translatable("screen.main_screen.spacing_count", conf.spacingCount.get) }



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