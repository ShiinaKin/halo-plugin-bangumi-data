package io.sakurasou.halo.bangumi.exception

/**
 * @author Shiina Kin
 * 2025/7/27 21:34
 */
class BangumiUserAccessTokenWrongException(
    message: String = "Bangumi user access token is wrong or expired, please rebind your Bangumi account.",
) : RuntimeException(message)