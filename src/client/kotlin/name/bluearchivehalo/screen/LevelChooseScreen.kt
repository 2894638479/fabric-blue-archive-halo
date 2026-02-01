package name.bluearchivehalo.screen

import name.bluearchivehalo.config.LevelConfig.Companion.ringCount
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.client.gui.widget.SimplePositioningWidget
import net.minecraft.text.Text

class LevelChooseScreen(parent: Screen): MyScreen(Text.translatable("screen.level_choose_screen.title"),parent) {
    override fun init() {
        val gridWidget = GridWidget()
        gridWidget.mainPositioner.marginX(5).marginBottom(4).alignHorizontalCenter()
        val adder = gridWidget.createAdder(2)
        for (level in 1..16){
            val button = ButtonWidget.builder(Text.translatable("screen.level_choose_screen.buttons", level, ringCount(level))){
                client?.setScreen(LevelConfigScreen(this,conf.getLevelConf(level)))
            }.build()
            if(level > 4) button tooltip Text.translatable("screen.level_choose_screen.modded_grades_tooltip")
            adder.add(button)
        }
        adder.add(done,2, adder.copyPositioner().marginTop(6))
        gridWidget.refreshPositions()
        SimplePositioningWidget.setPos(gridWidget, 0, height / 6 - 12,width,height, 0.5f, 0.0f)
        gridWidget.forEachChild(::addDrawableChild)
        super.init()
    }
}