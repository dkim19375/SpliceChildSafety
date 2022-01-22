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

import org.bukkit.Bukkit

enum class MinecraftVersion(private val id: Int) {
    V1_8(8),
    V1_17(17),
    V1_18(18);

    val versionString = "1.$id"

    fun isAtLeast(version: MinecraftVersion): Boolean = id >= version.id

    companion object {
        val CURRENT_VERSION: MinecraftVersion by lazy {
            // #getVersion: git-Paper-408 (MC: 1.17.1)
            // ex output: 1.17.1-R0.1-SNAPSHOT
            val str = Bukkit.getServer().bukkitVersion.removeSuffix("-R0.1-SNAPSHOT")
            // 1.17.1
            for (ver in values()) {
                val new = str.removePrefix(ver.versionString)
                // .1
                if (new.isNotBlank() && !new.startsWith('.')) {
                    continue
                }
                return@lazy ver
            }
            throw IllegalStateException("Invalid version found: $str, " +
                    "compatible versions: ${values().joinToString(transform = MinecraftVersion::versionString)}")
        }
    }
}