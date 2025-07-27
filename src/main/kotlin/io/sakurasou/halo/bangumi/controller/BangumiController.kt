package io.sakurasou.halo.bangumi.controller

import io.sakurasou.halo.bangumi.entity.BangumiUserData
import io.sakurasou.halo.bangumi.service.BangumiService
import io.sakurasou.halo.bangumi.vo.Result
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import run.halo.app.plugin.ApiVersion

/**
 * @author Shiina Kin
 * 2025/7/21 17:20
 */
@ApiVersion("io.sakurasou.halo.bangumi/v1")
@RestController
class BangumiController(
    private val bangumiService: BangumiService,
) {
    @PutMapping("/userData")
    fun manualUpdateUserData(): Mono<Result> = bangumiService.updateUserData()
}