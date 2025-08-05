package name.bluearchivehalo.screen

import name.bluearchivehalo.config.LevelConfig.Companion.ringCount
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text

class LevelChooseScreen(parent: Screen): MyScreen(Text.of("分等级设置"),parent) {
    override fun init() {
        for (level in 1..16){
            val button = ButtonWidget(0,0,150,20,Text.of("信标等级${level}  环数${ringCount(level)}")){
                client?.setScreen(LevelConfigScreen(this,conf.getLevelConf(level)))
            }
            if(level > 4) button tooltip "原版没有的等级，需要与其他服务端模组联动生效"
            myAdd(button)
        }
        myAdd(done,true)
        super.init()
    }
}