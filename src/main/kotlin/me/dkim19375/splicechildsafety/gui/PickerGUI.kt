/*
 *     SpliceChildSafety, a spigot plugin for the Splice minecraft server
 *     Copyright (C) 2022  dkim19375
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.dkim19375.splicechildsafety.gui

import dev.triumphteam.gui.guis.*
import me.dkim19375.dkimbukkitcore.function.formatAll
import me.dkim19375.splicechildsafety.SpliceChildSafety
import me.dkim19375.splicechildsafety.data.*
import me.dkim19375.splicechildsafety.util.*
import org.bukkit.entity.Player
import org.bukkit.event.Listener

class PickerGUI(private val player: Player, private val plugin: SpliceChildSafety) : Listener {
    private val config: PickerGUIData
        get() = plugin.mainConfig.get(MainConfigFile.PICKER_GUI)
    private val menu = Gui.gui()
        .rows(config.rows)
        .title(config.title.formatAll(player).toComponent())
        .disableAllInteractions()
        .create()

    private fun reset() {
        for (i in 0 until config.rows * 9) {
            menu.removeItem(i)
        }
    }

    fun showPlayer() {
        setup()
        menu.open(player)
    }

    private fun saveData(action: (PlayerData?) -> PlayerData) {
        plugin.dataFile.save(
            plugin.dataFile.get().let { dataFile ->
                dataFile.copy(
                    players = dataFile.players.let { map ->
                        map + (player.uniqueId to action(map[player.uniqueId]))
                    }
                )
            }
        )
    }

    private fun setup() {
        reset()
        val items = config.items
        val playerData = plugin.dataFile.get().players[player.uniqueId]
        addItem(items.disableChat, playerData?.chatRestricted == true) {
            saveData { data ->
                data?.copy(
                    chatRestricted = it
                ) ?: PlayerData(
                    chatRestricted = it
                )
            }
        }
        addItem(items.disableSkins, playerData?.skinRestricted == true) {
            saveData { data ->
                data?.copy(
                    skinRestricted = it
                ) ?: PlayerData(
                    skinRestricted = it
                )
            }
            player.updateSkinsForOthers()
        }
    }

    private fun addItem(config: ToggleableGUIItem, enabled: Boolean, action: (Boolean) -> Unit) = menu.setItem(
        config.row,
        config.column,
        GuiItem(
            if (enabled) {
                config.enabled
            } else {
                config.disabled
            }.getItemStack(player)
        ) {
            it.isCancelled = true
            action(!enabled)
            plugin.mainConfig.get(MainConfigFile.PICKER_GUI).sound.playSound(player)
            setup()
            menu.update()
        }
    )
}