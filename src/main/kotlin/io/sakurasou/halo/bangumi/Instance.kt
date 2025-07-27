package io.sakurasou.halo.bangumi

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import io.sakurasou.halo.bangumi.api.DefaultApi
import io.sakurasou.halo.bangumi.service.BangumiService
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import reactor.core.scheduler.Schedulers

/**
 * @author Shiina Kin
 * 2025/7/21 15:01
 */
@EnableScheduling
@Configuration
open class Instance {
    private val logger = KotlinLogging.logger {}

    @Autowired
    private lateinit var context: ApplicationContext

    @Bean
    open fun json() =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        }

    @Bean
    open fun pluginBangumiApi(json: Json): DefaultApi =
        DefaultApi(
            baseUrl = "https://api.bgm.tv",
            httpClientEngine = CIO.create(),
            httpClientConfig = {
                it.install(ContentNegotiation) {
                    json(json)
                }
                it.install(HttpCache)
                it.install(HttpTimeout)
                it.defaultRequest {
                    header(
                        "User-Agent",
                        "ShiinaKin/halo-bangumi-data (https://github.com/ShiinaKin/halo-plugin-bangumi-data)",
                    )
                }
            },
        )

    @Bean
    open fun scheduledTask() =
        ThreadPoolTaskScheduler().apply {
            poolSize = 2
            setThreadNamePrefix("bangumi-scheduler-")
            initialize()
        }

    @Scheduled(cron = "0 0 0 * * ?")
    open fun dailyTask() {
        val bangumiService = context.getBean(BangumiService::class.java)
        bangumiService
            .updateUserData()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
        logger.info { "Daily task executed: User data updated." }
    }
}