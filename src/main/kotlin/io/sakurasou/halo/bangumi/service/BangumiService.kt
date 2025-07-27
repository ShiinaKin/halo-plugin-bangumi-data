package io.sakurasou.halo.bangumi.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.sakurasou.halo.bangumi.api.DefaultApi
import io.sakurasou.halo.bangumi.dao.BangumiDAO
import io.sakurasou.halo.bangumi.entity.BangumiUserData
import io.sakurasou.halo.bangumi.exception.BangumiUserAccessTokenWrongException
import io.sakurasou.halo.bangumi.model.SubjectType
import io.sakurasou.halo.bangumi.vo.Result
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import org.springframework.data.util.TypeUtils.type
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
    private val logger = KotlinLogging.logger {}

    fun getUserData(): Mono<BangumiUserData?> =
        bangumiDAO
            .getBindUserInfo()
            .flatMap { (username, accessToken) ->
                bangumiApi.setBearerToken(accessToken)
                bangumiDAO
                    .getUserData(username)
                    .flatMap {
                        if (Instant.fromEpochSeconds(it.spec!!.lastUpdateTime!!.toLong()).plus(1.days) >=
                            Clock.System.now()
                        ) {
                            logger.info { "Cached data is up-to-date, returning cached data..." }
                            Mono.just(it)
                        } else {
                            logger.info { "User data is outdated, updating..." }
                            getAndUpdateUserData(username, it)
                        }
                    }.switchIfEmpty(
                        Mono.defer {
                            logger.info { "User data not found, fetching from API..." }
                            getAndUpdateUserData(username)
                            Mono.empty()
                        },
                    ).onErrorResume { throw it }
            }.onErrorResume {
                logger.warn { it.message }
                Mono.empty()
            }

    fun updateUserData(): Mono<Result> =
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
                    .flatMap { Mono.just(Result("更新数据成功")) }
            }.onErrorResume {
                logger.warn(it) { "更新用户数据失败" }
                Mono.just(Result("更新用户数据失败: ${it.message ?: "未知错误"}", false))
            }.doFinally {
                logger.info { "Update user data manually" }
            }

    private fun getAndUpdateUserData(
        username: String,
        oldData: BangumiUserData? = null,
    ): Mono<BangumiUserData> =
        getUserDataFromApi(username)
            .flatMap { userData ->
                oldData?.let {
                    val bangumiUserData =
                        oldData.copy(spec = userData).also {
                            it.metadata = oldData.metadata
                        }
                    bangumiDAO.updateUserData(bangumiUserData)
                } ?: run {
                    val bangumiUserData =
                        BangumiUserData(spec = userData).apply {
                            metadata =
                                Metadata().apply {
                                    name = username
                                }
                        }
                    bangumiDAO.saveUserData(bangumiUserData)
                }
            }.onErrorResume { throw it }

    private fun getUserDataFromApi(username: String): Mono<BangumiUserData.BangumiUserDataSpec> {
        val userMono = Mono.fromCallable { runBlocking { bangumiApi.getUserByName(username).body() } }

        fun getCollectionMono(subjectType: SubjectType) =
            Mono
                .fromCallable {
                    runBlocking {
                        val response =
                            bangumiApi
                                .getUserCollectionsByUsername(
                                    username = username,
                                    subjectType = subjectType,
                                    type = null,
                                    limit = 30,
                                    offset = 0,
                                )
                        if (response.success) {
                            response.body().data
                        } else if (response.status == HttpStatusCode.Unauthorized.value) {
                            throw BangumiUserAccessTokenWrongException()
                        } else {
                            throw BangumiUserAccessTokenWrongException()
                        }
                    }
                }.onErrorResume {
                    logger.error(it) { "获取 $subjectType 收藏数据失败" }
                    throw it
                }

        val bookMono = getCollectionMono(SubjectType.Book)
        val animeMono = getCollectionMono(SubjectType.Anime)
        val musicMono = getCollectionMono(SubjectType.Music)
        val gameMono = getCollectionMono(SubjectType.Game)
        val realMono = getCollectionMono(SubjectType.Real)

        return Mono
            .zip(userMono, bookMono, animeMono, musicMono, gameMono, realMono)
            .map { tuple ->
                val user = tuple.t1
                val book = tuple.t2
                val anime = tuple.t3
                val music = tuple.t4
                val game = tuple.t5
                val real = tuple.t6
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
                )
            }.onErrorResume { throw it }
    }
}