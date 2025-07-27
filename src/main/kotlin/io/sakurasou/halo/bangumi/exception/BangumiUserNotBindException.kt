package io.sakurasou.halo.bangumi.exception

/**
 * @author Shiina Kin
 * 2025/7/27 21:34
 */
class BangumiUserNotBindException(
    message: String = "Bangumi user is not bind, please bind your Bangumi account first.",
) : RuntimeException(message)