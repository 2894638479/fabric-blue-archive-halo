package name.bluearchivehalo

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import name.bluearchivehalo.screen.MainScreen

class BlueArchiveHaloModMenu : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory(::MainScreen)
}