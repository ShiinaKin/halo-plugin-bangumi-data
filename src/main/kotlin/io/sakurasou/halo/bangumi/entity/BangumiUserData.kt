package io.sakurasou.halo.bangumi.entity

import io.sakurasou.halo.bangumi.model.UserSubjectCollection
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import run.halo.app.extension.AbstractExtension
import run.halo.app.extension.GVK

/**
 * @author Shiina Kin
 * 2025/7/21 15:04
 */
@GVK(
    kind = "BangumiData",
    group = "io.sakurasou.halo.bangumi",
    version = "v1",
    singular = "bangumiData",
    plural = "bangumiData",
)
data class BangumiUserData(
    val spec: BangumiUserDataSpec? = null,
) : AbstractExtension() {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    data class BangumiUserDataSpec(
        val nickname: String? = null,
        val avatar: String? = null,
        val sign: String? = null,
        // cause of jackson object which Halo used doesn't reg kotlinModule
        // val bookCollection: List<UserSubjectCollection>? = null,
        val bookCollectionJson: String? = null,
        // val animeCollection: List<UserSubjectCollection>? = null,
        val animeCollectionJson: String? = null,
        // val musicCollection: List<UserSubjectCollection>? = null,
        val musicCollectionJson: String? = null,
        // val gameCollection: List<UserSubjectCollection>? = null,
        val gameCollectionJson: String? = null,
        // val realCollection: List<UserSubjectCollection>? = null,
        val realCollectionJson: String? = null,
        val lastUpdateTime: String? = null,
    )
}