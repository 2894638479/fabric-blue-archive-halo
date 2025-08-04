package name.bluearchivehalo.screen

import name.bluearchivehalo.config.LevelConfig.Companion.ringCount
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.client.gui.widget.SimplePositioningWidget
import net.minecraft.text.Text

class LevelChooseScreen(parent: Screen): MyScreen(Text.of("分等级设置"),parent) {
    override fun init() {
        val gridWidget = GridWidget()
        gridWidget.mainPositioner.marginX(5).marginBottom(4).alignHorizontalCenter()
        val adder = gridWidget.createAdder(2)
        for (level in 1..16){
            val button = ButtonWidget.builder(Text.of("信标等级${level}  环数${ringCount(level)}")){
                client?.setScreen(LevelConfigScreen(this,conf.getLevelConf(level)))
            }.build()
            if(level > 4) button tooltip "原版没有的等级，需要与其他服务端模组联动生效"
            adder.add(button)
        }
        adder.add(done,2, adder.copyPositioner().marginTop(6))
        gridWidget.refreshPositions()
        SimplePositioningWidget.setPos(gridWidget, 0, height / 6 - 12,width,height, 0.5f, 0.0f)
        gridWidget.forEachChild(::addDrawableChild)
        super.init()
    }
}