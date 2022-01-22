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

package me.dkim19375.splicechildsafety.util

import dev.triumphteam.gui.builder.item.BaseItemBuilder
import dev.triumphteam.gui.guis.GuiItem
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag

fun BaseItemBuilder<*>.asNewGuiItem(action: (InventoryClickEvent) -> Unit = {}): GuiItem = asGuiItem {
    it.isCancelled = true
    action(it)
}

fun <B : BaseItemBuilder<out B>> BaseItemBuilder<out B>.addAllFlags(): B = ItemFlag.values().toList()
    .minus(setOf(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS))
    .map { flags(it) }
    .first()