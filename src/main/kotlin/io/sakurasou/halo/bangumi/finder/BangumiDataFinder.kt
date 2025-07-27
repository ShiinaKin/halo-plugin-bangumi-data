package io.sakurasou.halo.bangumi.finder

import io.sakurasou.halo.bangumi.entity.BangumiUserData
import reactor.core.publisher.Mono

/**
 * @author Shiina Kin
 * 2025/7/26 17:20
 */
interface BangumiDataFinder {
    fun findBangumiData(): Mono<BangumiUserData.BangumiUserDataSpec?>
}