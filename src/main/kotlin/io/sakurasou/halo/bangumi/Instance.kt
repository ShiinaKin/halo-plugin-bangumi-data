package io.sakurasou.halo.bangumi

import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import io.sakurasou.halo.bangumi.api.DefaultApi
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Shiina Kin
 * 2025/7/21 15:01
 */
@Configuration
open class Instance {
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
                it.install(Logging)
                it.defaultRequest {
                    header(
                        "User-Agent",
                        "ShiinaKin/halo-bangumi-data (https://github.com/ShiinaKin/halo-plugin-bangumi-data)",
                    )
                }
            },
        )
}