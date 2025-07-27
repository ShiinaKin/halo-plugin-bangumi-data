package io.sakurasou.halo.bangumi.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.halo.bangumi.api.DefaultApi
import io.sakurasou.halo.bangumi.dao.BangumiDAO
import io.sakurasou.halo.bangumi.entity.BangumiUserData
import io.sakurasou.halo.bangumi.model.SubjectType
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import run.halo.app.extension.Metadata
import kotlin.time.Duration.Companion.days

/**
 * @author Shiina Kin
 * 2025/7/21 17:17
 */
@Service
class BangumiService(
    private val json: Json,
    private val bangumiApi: DefaultApi,
    private val bangumiDAO: BangumiDAO,
) {
    private val logger = KotlinLogging.logger { this::class.java }

    fun getUserData(): Mono<BangumiUserData> =
        bangumiDAO.getBindUserInfo().flatMap { (username, accessToken) ->
            bangumiApi.setBearerToken(accessToken)
            bangumiDAO
                .getUserData(username)
                .flatMap {
                    if (Instant.fromEpochSeconds(it.spec!!.lastUpdateTime!!.toLong()).plus(1.days) >=
                        Clock.System.now()
                    ) {
                        logger.debug { "Cached data is up-to-date, returning cached data..." }
                        Mono.just(it)
                    } else {
                        logger.debug { "User data is outdated, updating..." }
                        getAndUpdateUserData(username, it)
                    }
                }.switchIfEmpty(
                    Mono.defer {
                        logger.debug { "User data not found, fetching from API..." }
                        getAndUpdateUserData(username)
                    },
                )
        }

    fun updateUserData(): Mono<Void> =
        bangumiDAO
            .getBindUserInfo()
            .flatMap { (username, accessToken) ->
                bangumiApi.setBearerToken(accessToken)
                bangumiDAO
                    .getUserData(username)
                    .flatMap { oldData ->
                        logger.debug { oldData }
                        getAndUpdateUserData(username, oldData)
                    }.switchIfEmpty(getAndUpdateUserData(username))
                    .flatMap {
                        Mono.empty<Void>()
                    }
            }.doFinally {
                logger.debug { "Update user data manually" }
            }

    private fun getAndUpdateUserData(
        username: String,
        oldData: BangumiUserData? = null,
    ): Mono<BangumiUserData> =
        getUserDataFromApi(username).flatMap { userData ->
            oldData?.let {
                val bangumiUserData =
                    oldData.copy(spec = userData).also {
                        it.metadata = oldData.metadata
                    }
                logger.debug { "update old data" }
                bangumiDAO.updateUserData(bangumiUserData)
            } ?: run {
                val bangumiUserData =
                    BangumiUserData(spec = userData).apply {
                        metadata =
                            Metadata().apply {
                                name = username
                            }
                    }
                logger.debug { "create new data" }
                bangumiDAO.saveUserData(bangumiUserData)
            }
        }

    private fun getUserDataFromApi(username: String): Mono<BangumiUserData.BangumiUserDataSpec> =
        runBlocking {
            val user = bangumiApi.getUserByName(username).body()

            val book =
                bangumiApi
                    .getUserCollectionsByUsername(
                        username = username,
                        subjectType = SubjectType.Book,
                        type = null,
                        limit = 30,
                        offset = 0,
                    ).body()
                    .data ?: throw IllegalStateException("Book collection not found")
            val anime =
                bangumiApi
                    .getUserCollectionsByUsername(
                        username = username,
                        subjectType = SubjectType.Anime,
                        type = null,
                        limit = 30,
                        offset = 0,
                    ).body()
                    .data ?: throw IllegalStateException("Anime collection not found")
            val music =
                bangumiApi
                    .getUserCollectionsByUsername(
                        username = username,
                        subjectType = SubjectType.Music,
                        type = null,
                        limit = 30,
                        offset = 0,
                    ).body()
                    .data ?: throw IllegalStateException("Music collection not found")
            val game =
                bangumiApi
                    .getUserCollectionsByUsername(
                        username = username,
                        subjectType = SubjectType.Game,
                        type = null,
                        limit = 30,
                        offset = 0,
                    ).body()
                    .data ?: throw IllegalStateException("Game collection not found")
            val real =
                bangumiApi
                    .getUserCollectionsByUsername(
                        username = username,
                        subjectType = SubjectType.Real,
                        type = null,
                        limit = 30,
                        offset = 0,
                    ).body()
                    .data ?: throw IllegalStateException("Real collection not found")
            Mono.just(
                BangumiUserData.BangumiUserDataSpec(
                    nickname = user.nickname,
                    avatar = user.avatar.large,
                    sign = user.sign,
                    bookCollectionJson = json.encodeToString(book),
                    animeCollectionJson = json.encodeToString(anime),
                    musicCollectionJson = json.encodeToString(music),
                    gameCollectionJson = json.encodeToString(game),
                    realCollectionJson = json.encodeToString(real),
                    lastUpdateTime =
                        Clock.System
                            .now()
                            .toEpochMilliseconds()
                            .toString(),
                ),
            )
        }
}