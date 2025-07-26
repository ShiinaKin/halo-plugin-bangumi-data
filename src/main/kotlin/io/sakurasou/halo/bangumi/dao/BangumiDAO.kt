package io.sakurasou.halo.bangumi.dao

import io.sakurasou.halo.bangumi.entity.BangumiUserData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import run.halo.app.extension.ReactiveExtensionClient
import run.halo.app.plugin.ReactiveSettingFetcher
import kotlin.jvm.java

/**
 * @author Shiina Kin
 * 2025/7/21 16:14
 */
@Component
open class BangumiDAO(
    private val settingFetcher: ReactiveSettingFetcher,
    private val extensionClient: ReactiveExtensionClient,
) {
    fun getBindUserInfo(): Mono<Pair<String, String>> =
        settingFetcher
            .fetch("default", Map::class.java)
            .map {
                val username =
                    (it["username"] as String).ifBlank {
                        throw IllegalStateException("username is blank")
                    }
                val accessToken =
                    (it["accessToken"] as String).ifBlank {
                        throw IllegalStateException("accessToken is blank")
                    }
                username to accessToken
            }

    fun getUserData(username: String): Mono<BangumiUserData> =
        extensionClient
            .fetch(BangumiUserData::class.java, username)

    fun saveUserData(bangumiUserData: BangumiUserData): Mono<BangumiUserData> = extensionClient.create(bangumiUserData)

    fun updateUserData(bangumiUserData: BangumiUserData): Mono<BangumiUserData> =
        extensionClient.update(bangumiUserData)
}