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

import com.comphenix.protocol.*
import com.comphenix.protocol.events.*
import com.mojang.brigadier.context.StringRange
import com.mojang.brigadier.suggestion.*
import me.dkim19375.splicechildsafety.SpliceChildSafety
import me.dkim19375.splicechildsafety.util.MinecraftVersion
import org.bukkit.Bukkit
import org.bukkit.event.*
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import java.util.*
import kotlin.random.*
import kotlin.random.Random

class PlayerCommandListener(private val plugin: SpliceChildSafety) : Listener {
    private val commands = setOf("gamemode childsafety", "gm childsafety")

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private fun PlayerCommandPreprocessEvent.onCommand() {
        if (message.lowercase().removePrefix("/") !in commands) {
            return
        }
        isCancelled = true
        player.chat("/childsafetymode")
    }

    init {
        if (MinecraftVersion.CURRENT_VERSION.isAtLeast(MinecraftVersion.V1_17)) {
            ProtocolLibrary.getProtocolManager().addPacketListener(
                object :
                    PacketAdapter(plugin, PacketType.Play.Server.TAB_COMPLETE, PacketType.Play.Client.TAB_COMPLETE) {
                    private val transactionIDs = mutableMapOf<UUID, Int>()
                    private var subCommands: Set<String>? = null

                    override fun onPacketReceiving(event: PacketEvent) {
                        val player = event.player
                        val packet = event.packet
                        val id = packet.integers.read(0)
                        val command = packet.strings.read(0)
                        if (commands.none { "/$it".startsWith(command, true) }) {
                            transactionIDs.remove(player.uniqueId)
                            return
                        }
                        transactionIDs[player.uniqueId] = id
                        Bukkit.getScheduler().runTask(plugin) {
                            if (!transactionIDs.remove(player.uniqueId, id)) {
                                return@runTask
                            }
                            val newPacket = PacketContainer(PacketType.Play.Server.TAB_COMPLETE)
                            newPacket.integers.write(0, id)
                            val sub = subCommands?.let { (it + "childsafety").sorted() } ?: return@runTask
                            val split = command.removePrefix("/").split(' ')
                            val start = split[0].length + 2
                            val end = start + (split.getOrNull(1)?.length ?: 0)
                            val range = StringRange(start, end)
                            val suggestions = Suggestions(
                                range,
                                sub.filter { arg ->
                                    val arg2 = split.getOrNull(1) ?: return@filter true
                                    arg.startsWith(arg2, true)
                                }.map { str -> Suggestion(range, str) }
                            )
                            newPacket.getSpecificModifier(Suggestions::class.java).write(0, suggestions)
                            ProtocolLibrary.getProtocolManager().sendServerPacket(player, newPacket, false)
                        }
                    }

                    override fun onPacketSending(event: PacketEvent) {
                        val player = event.player
                        val packet = event.packet
                        val id = packet.integers.read(0)
                        if (transactionIDs[player.uniqueId] != id) {
                            return
                        }
                        transactionIDs.remove(player.uniqueId)
                        val suggestions = packet.getSpecificModifier(Suggestions::class.java).read(0)
                        if (subCommands == null) {
                            subCommands = suggestions.list.map(Suggestion::getText).toSet()
                        }
                        val newList = suggestions.list.toMutableSet()
                        newList.add(Suggestion(suggestions.range, "childsafety"))
                        suggestions.list.clear()
                        suggestions.list.addAll(newList.sorted())
                    }
                }
            )
        } else {
            ProtocolLibrary.getProtocolManager().addPacketListener(
                object : PacketAdapter(plugin, PacketType.Play.Client.TAB_COMPLETE, PacketType.Play.Server.TAB_COMPLETE) {
                    private val tabCompletionPlayers = mutableMapOf<UUID, Int>()
                    private var subCommands: Set<String>? = null

                    override fun onPacketReceiving(event: PacketEvent) {
                        val player = event.player
                        val packet = event.packet
                        val id = Random.nextInt(Int.MIN_VALUE..Int.MAX_VALUE)
                        val command = packet.strings.read(0)
                        if (commands.none { "/$it".startsWith(command, true) }) {
                            tabCompletionPlayers.remove(player.uniqueId)
                            return
                        }
                        tabCompletionPlayers[player.uniqueId] = id
                        Bukkit.getScheduler().runTask(plugin) {
                            if (!tabCompletionPlayers.remove(player.uniqueId, id)) {
                                return@runTask
                            }
                            val newPacket = PacketContainer(PacketType.Play.Server.TAB_COMPLETE)
                            val new = ((subCommands ?: return@runTask) + "childsafety").sorted().filter { str ->
                                str.startsWith(command.removePrefix("/"))
                            }
                            newPacket.stringArrays.write(0, new.toTypedArray())
                            ProtocolLibrary.getProtocolManager().sendServerPacket(player, newPacket, false)
                        }
                    }

                    override fun onPacketSending(event: PacketEvent) {
                        val player = event.player
                        val packet = event.packet
                        val matches = packet.stringArrays.read(0).toMutableSet()
                        if (player.uniqueId !in tabCompletionPlayers) {
                            return
                        }
                        tabCompletionPlayers.remove(player.uniqueId)
                        if (subCommands == null) {
                            subCommands = matches.toSet()
                        }
                        matches.add("childsafety")
                        packet.stringArrays.write(0, matches.sorted().toTypedArray())
                    }
                }
            )
        }
    }
}