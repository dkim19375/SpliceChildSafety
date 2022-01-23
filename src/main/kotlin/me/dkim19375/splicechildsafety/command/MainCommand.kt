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

package me.dkim19375.splicechildsafety.command

import me.dkim19375.splicechildsafety.SpliceChildSafety
import me.dkim19375.splicechildsafety.gui.PickerGUI
import me.dkim19375.splicechildsafety.util.*
import org.bukkit.ChatColor
import org.bukkit.command.*
import org.bukkit.entity.Player

class MainCommand(private val plugin: SpliceChildSafety) : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (!sender.hasPermission(Permissions.COMMAND)) {
            sender.sendMessage(ErrorMessages.NO_PERMISSION)
            return true
        }
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}You must be a player!")
            return true
        }
        if (args.isEmpty()) {
            PickerGUI(sender, plugin).showPlayer()
            return true
        }
        when (args[0].lowercase()) {
            "reload" -> {
                if (!sender.hasPermission(Permissions.RELOAD)) {
                    sender.sendMessage(ErrorMessages.NO_PERMISSION)
                    return true
                }
                plugin.reloadConfig()
                return true
            }
            else -> {
                sender.sendMessage(ErrorMessages.INVALID_ARG)
                return true
            }
        }
    }
}