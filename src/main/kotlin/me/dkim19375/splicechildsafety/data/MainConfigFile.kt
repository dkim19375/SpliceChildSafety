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

package me.dkim19375.splicechildsafety.data

import me.dkim19375.splicechildsafety.util.MinecraftVersion
import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.*
import me.mattstudios.config.properties.Property

object MainConfigFile : SettingsHolder {
    @Path("gui.picker")
    val PICKER_GUI = Property.create(PickerGUIData())
}

data class PickerGUIData(
    var title: String = "Pick your safety modes!",
    var rows: Int = 1,
    var items: PickerGUIItems = PickerGUIItems(),
    var sound: SoundData = SoundData(
        name = if (MinecraftVersion.CURRENT_VERSION.isAtLeast(MinecraftVersion.V1_17)) {
            "BLOCK_NOTE_BLOCK_PLING"
        } else {
            "NOTE_PLING"
        },
        volume = 0.8f,
        pitch = 7f
    )
)

data class PickerGUIItems(
    @Name("disable-chat")
    var disableChat: ToggleableGUIItem = ToggleableGUIItem(
        row = 1,
        column = 4,
        enabled = ItemStackData(
            material = if (MinecraftVersion.CURRENT_VERSION.isAtLeast(MinecraftVersion.V1_17)) "OAK_SIGN" else "SIGN",
            displayName = "&cChat Disabled",
            lore = listOf(
                " ",
                "&6Click to enable!"
            )
        ),
        disabled = ItemStackData(
            material = if (MinecraftVersion.CURRENT_VERSION.isAtLeast(MinecraftVersion.V1_17)) "OAK_SIGN" else "SIGN",
            displayName = "&aChat Enabled",
            lore = listOf(
                " ",
                "&6Click to disable!"
            )
        )
    ),
    @Name("disable-skins")
    var disableSkins: ToggleableGUIItem = ToggleableGUIItem(
        row = 1,
        column = 6,
        enabled = ItemStackData(
            material = if (MinecraftVersion.CURRENT_VERSION.isAtLeast(MinecraftVersion.V1_17)) {
                "PLAYER_HEAD"
            } else {
                "SKULL_ITEM:3"
            },
            displayName = "&cSkins Disabled",
            lore = listOf(
                " ",
                "&6Click to enable!"
            )
        ),
        disabled = ItemStackData(
            material = if (MinecraftVersion.CURRENT_VERSION.isAtLeast(MinecraftVersion.V1_17)) {
                "PLAYER_HEAD"
            } else {
                "SKULL_ITEM:3"
            },
            displayName = "&aSkins Enabled",
            lore = listOf(
                " ",
                "&6Click to disable!"
            )
        )
    ),
)

data class ToggleableGUIItem(
    var row: Int = 1,
    var column: Int = 1,
    var enabled: ItemStackData = ItemStackData(),
    var disabled: ItemStackData = ItemStackData(),
)