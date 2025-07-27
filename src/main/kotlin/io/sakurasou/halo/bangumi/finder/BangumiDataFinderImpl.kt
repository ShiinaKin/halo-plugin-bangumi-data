package io.sakurasou.halo.bangumi.finder

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.halo.bangumi.entity.BangumiUserData
import io.sakurasou.halo.bangumi.service.BangumiService
import reactor.core.publisher.Mono
import run.halo.app.theme.finders.Finder

/**
 * @author Shiina Kin
 * 2025/7/26 17:20
 */
@Finder("bangumiDataFinder")
class BangumiDataFinderImpl(
    private val bangumiService: BangumiService,
) : BangumiDataFinder {
    private val logger = KotlinLogging.logger {}

    override fun findBangumiData(): Mono<BangumiUserData.BangumiUserDataSpec?> =
        bangumiService
            .getUserData()
            .map { it?.spec }
            .switchIfEmpty(Mono.empty())
}