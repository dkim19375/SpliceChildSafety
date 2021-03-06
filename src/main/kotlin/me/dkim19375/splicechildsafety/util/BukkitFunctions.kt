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

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible

fun Player.updateSkinsForOthers() {
    for (other in world.players) {
        if (other.uniqueId == uniqueId) {
            continue
        }
        if (!canSee(other)) {
            continue
        }
        hidePlayer(other)
        showPlayer(other)
    }
}

fun Permissible.hasPermission(permission: Permissions): Boolean = hasPermission(permission.perm)

fun CommandSender.sendMessage(error: ErrorMessages) = sendMessage(error.message)