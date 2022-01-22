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

package me.dkim19375.splicechildsafety

import me.dkim19375.dkimbukkitcore.function.logInfo
import me.dkim19375.dkimbukkitcore.javaplugin.CoreJavaPlugin
import me.dkim19375.dkimcore.file.*
import me.dkim19375.splicechildsafety.command.MainCommand
import me.dkim19375.splicechildsafety.data.*
import me.dkim19375.splicechildsafety.listener.*
import me.dkim19375.splicechildsafety.manager.PacketManager
import me.dkim19375.splicechildsafety.util.MinecraftVersion
import java.io.File
import kotlin.system.measureTimeMillis

class SpliceChildSafety : CoreJavaPlugin() {
    override val defaultConfig = false
    val mainConfig by lazy { YamlFile(MainConfigFile, File(dataFolder, "config.yml")) }
    val dataFile by lazy { JsonFile(MainDataFile::class, File(dataFolder, "data.json"), prettyPrinting = false) }
    private val packetManager by lazy { PacketManager(this) }

    override fun onEnable() {
        logInfo("Successfully enabled ${description.name} in ${
            measureTimeMillis {
                val version = MinecraftVersion.CURRENT_VERSION
                logInfo("Compatible version detected: ${version.versionString}!")
                registerConfig(mainConfig)
                registerConfig(dataFile)
                packetManager.register()
                registerListener(AsyncPlayerChatListener(this), PlayerCommandListener(this))
                registerCommand("childsafetymode", MainCommand(this))
            }
        }ms!")
    }
}