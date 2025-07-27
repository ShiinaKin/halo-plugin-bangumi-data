package io.sakurasou.halo.bangumi

import io.sakurasou.halo.bangumi.finder.BangumiDataFinder
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import run.halo.app.theme.TemplateNameResolver
import run.halo.app.theme.router.ModelConst

/**
 * @author Shiina Kin
 * 2025/7/26 20:10
 */
@Component
class BangumiRoute(
    private val bangumiDataFinder: BangumiDataFinder,
    private val templateNameResolver: TemplateNameResolver,
) {
    @Bean
    fun bangumiDataRouterFunction() =
        RouterFunctions
            .route(RequestPredicates.GET("/bangumi")) { request ->
                templateNameResolver
                    .resolveTemplateNameOrDefault(request.exchange(), "bangumi")
                    .flatMap { templateName ->
                        ServerResponse.ok().render(
                            templateName,
                            mapOf(
                                "bangumiData" to bangumiDataFinder.findBangumiData().map { it.spec },
                                ModelConst.TEMPLATE_ID to "bangumi",
                            ),
                        )
                    }
            }
}