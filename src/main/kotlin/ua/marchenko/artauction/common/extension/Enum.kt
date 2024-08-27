package ua.marchenko.artauction.common.extension

inline fun <reified T : Enum<T>> valueOfOrNull(name: String): T? = runCatching { enumValueOf<T>(name) }.getOrNull()
