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

package me.dkim19375.splicechildsafety.manager

import com.comphenix.protocol.*
import com.comphenix.protocol.events.*
import com.comphenix.protocol.wrappers.WrappedSignedProperty
import me.dkim19375.dkimbukkitcore.function.logInfo
import me.dkim19375.splicechildsafety.SpliceChildSafety
import org.bukkit.Bukkit
import java.util.*

class PacketManager(private val plugin: SpliceChildSafety) {
    fun register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
            object : PacketAdapter(plugin, PacketType.Play.Server.PLAYER_INFO) {
                private val oldTextures = mutableMapOf<UUID, Collection<WrappedSignedProperty>>()

                override fun onPacketSending(event: PacketEvent) {
                    val packet = event.packet
                    val player = event.player
/*                    if (packet.playerInfoAction.read(0) != EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
                        return
                    }*/
                    for (data in (packet.playerInfoDataLists.readSafely(0) ?: return)) {
                        val profile = data.profile
                        val uuid = profile.uuid ?: continue
                        val stored = profile.properties.get("textures")?.toList()
                        if (stored != null && stored.isNotEmpty()) {
                            oldTextures[uuid] = stored
                        }
                        if (Bukkit.getPlayer(uuid) == null) {
                            continue
                        }
                        if (player.uniqueId == uuid) {
                            continue
                        }
                        if (this@PacketManager.plugin.dataFile.get().players[player.uniqueId]?.skinRestricted == true) {
                            // restricted
                            val removed = profile.properties.removeAll("textures")?.toList()
                            if (removed != null && removed.isNotEmpty()) {
                                oldTextures[uuid] = removed
                            }
                            continue
                        }
                        val oldTexture = oldTextures[uuid] ?: continue
                        profile.properties.putAll("textures", oldTexture)
                    }
                }
            }
        )
    }
}