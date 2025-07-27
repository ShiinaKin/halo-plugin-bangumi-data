package io.sakurasou.halo.bangumi.exception

/**
 * @author Shiina Kin
 * 2025/7/27 21:36
 */
class BangumiAccessFailedException(
    message: String = "Bangumi access failed, please check your network connection or try again later.",
) : RuntimeException(message)