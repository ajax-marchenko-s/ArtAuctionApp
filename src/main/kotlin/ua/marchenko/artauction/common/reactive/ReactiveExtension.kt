package ua.marchenko.artauction.common.reactive

import reactor.core.publisher.Mono

fun <T> Mono<T>.switchIfEmpty(s: () -> Mono<T>): Mono<T> = this.switchIfEmpty(Mono.defer { s() })
