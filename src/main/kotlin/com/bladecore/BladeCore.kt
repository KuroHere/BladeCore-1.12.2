package com.bladecore

import com.bladecore.client.Client
import com.bladecore.client.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lwjgl.opengl.Display

@Suppress("UNUSED_PARAMETER")
@Mod(
    modid = Client.ID,
    name = Client.NAME,
    version = Client.VERSION,
    dependencies = BladeCore.DEPENDENCIES
)
class BladeCore {
    companion object {
        const val DEPENDENCIES = "required-after:forge@[14.23.5.2860,);"
        const val DIR = "BladeCore"

        val LOG: Logger = LogManager.getLogger(Client.NAME)
        var instance: BladeCore? = null
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        LOG.info("Pre init started")
        val t = System.currentTimeMillis()

        Display.setTitle("Loading " + Client.displayName)
        instance = this
        Loader.onPreLoad()

        LOG.info("Pre init completed, took: ${(System.currentTimeMillis() - t)}ms")
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        LOG.info("Init started")
        val t = System.currentTimeMillis()

        Loader.onLoad()

        LOG.info("Init completed, took: ${(System.currentTimeMillis() - t)}ms")
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        LOG.info("Post init started")
        val t = System.currentTimeMillis()

        Loader.onPostLoad()
        Display.setTitle(Client.displayName)

        LOG.info("Post init completed, took: ${( System.currentTimeMillis() - t)}ms")
    }
}
