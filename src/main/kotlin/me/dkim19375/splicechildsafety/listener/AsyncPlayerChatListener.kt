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

package me.dkim19375.splicechildsafety.listener

import me.dkim19375.splicechildsafety.SpliceChildSafety
import me.dkim19375.splicechildsafety.data.PlayerData
import org.bukkit.event.*
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*

class AsyncPlayerChatListener(private val plugin: SpliceChildSafety) : Listener {
    private val playerData: Map<UUID, PlayerData>
        get() = plugin.dataFile.get().players

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private fun AsyncPlayerChatEvent.onChat() {
        if (playerData[player.uniqueId]?.chatRestricted == true) {
            isCancelled = true
            return
        }
        for (recipient in recipients.toSet()) {
            if (playerData[recipient.uniqueId]?.chatRestricted == true) {
                recipients.remove(recipient)
            }
        }
    }
}