package io.sakurasou.halo.bangumi

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.halo.bangumi.entity.BangumiUserData
import org.springframework.stereotype.Component
import run.halo.app.extension.SchemeManager
import run.halo.app.plugin.BasePlugin
import run.halo.app.plugin.PluginContext
import kotlin.jvm.java

/**
 * @author Shiina Kin
 * 2025/7/21 14:48
 */
@Component
class Starter(
    playerContext: PluginContext,
    private val schemeManager: SchemeManager,
) : BasePlugin(playerContext) {
    private val logger = KotlinLogging.logger {}

    override fun start() {
        schemeManager.register(BangumiUserData::class.java)
        logger.info { ">= BangumiData Started =<" }
    }

    override fun stop() {
        val bgmUserDataSchema = schemeManager.get(BangumiUserData::class.java)
        schemeManager.unregister(bgmUserDataSchema)
        logger.info { ">= BangumiData Stopped =<" }
    }
}