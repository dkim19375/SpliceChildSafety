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

    @Path("actions")
    val ACTIONS = Property.create(ActionsData())
}

data class ActionsData(
    @Name("disable-chat")
    var disableChat: DisableChatActions = DisableChatActions(),
    @Name("chat-filter")
    var chatFilter: ChatFilterActions = ChatFilterActions()
)

data class ChatFilterActions(
    @Name("regular-filters")
    var regularFilters: Set<String> = setOf("filtered phrase"),
    @Name("regex-filters")
    var regexFilters: Set<String> = setOf("filtered (phrase|word)"),
    var message: String = "&cPlease follow chat rules!"
)

data class DisableChatActions(
    var message: String = "&cYou are currently in Child Safety Mode. " +
            "Use &6/childsafetymode &cto be able to talk in chat again!"
)

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
        enabled = ToggledGUIItem(
            item = ItemStackData(
                material = if (MinecraftVersion.CURRENT_VERSION.isAtLeast(MinecraftVersion.V1_17)) "OAK_SIGN" else "SIGN",
                displayName = "&cChat Disabled",
                lore = listOf(
                    " ",
                    "&6Click to enable!"
                )
            ),
            message = "&aSuccessfully enabled chat!"
        ),
        disabled = ToggledGUIItem(
            item = ItemStackData(
                material = if (MinecraftVersion.CURRENT_VERSION.isAtLeast(MinecraftVersion.V1_17)) "OAK_SIGN" else "SIGN",
                displayName = "&aChat Enabled",
                lore = listOf(
                    " ",
                    "&6Click to disable!"
                )
            ),
            message = "&6Successfully disabled chat!"
        )
    ),
    @Name("disable-skins")
    var disableSkins: ToggleableGUIItem = ToggleableGUIItem(
        row = 1,
        column = 5,
        enabled = ToggledGUIItem(
            item = ItemStackData(
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
            message = "&aSuccessfully enabled skins!"
        ),
        disabled = ToggledGUIItem(
            item = ItemStackData(
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
            ),
            message = "&6Successfully disabled skins!"
        )
    ),
    @Name("chat-filter")
    var chatFilter: ToggleableGUIItem = ToggleableGUIItem(
        row = 1,
        column = 6,
        enabled = ToggledGUIItem(
            item = ItemStackData(
                material = "TNT",
                displayName = "&aChat Filter Enabled",
                lore = listOf(
                    " ",
                    "&6Click to disable!"
                )
            ),
            message = "&6Successfully disabled the chat filter!"
        ),
        disabled = ToggledGUIItem(
            item = ItemStackData(
                material = "TNT",
                displayName = "&cChat Filter Disabled",
                lore = listOf(
                    " ",
                    "&6Click to disable!"
                )
            ),
            message = "&aSuccessfully enabled the chat filter!"
        )
    ),
)

data class ToggleableGUIItem(
    var row: Int = 1,
    var column: Int = 1,
    var enabled: ToggledGUIItem = ToggledGUIItem(),
    var disabled: ToggledGUIItem = ToggledGUIItem(),
)

data class ToggledGUIItem(
    var item: ItemStackData = ItemStackData(),
    var message: String = "Invalid"
)