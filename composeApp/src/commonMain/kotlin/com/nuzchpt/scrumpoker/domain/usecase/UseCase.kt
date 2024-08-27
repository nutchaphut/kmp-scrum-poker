package com.nuzchpt.scrumpoker.domain.usecase

import kotlinx.coroutines.flow.Flow

abstract class UseCase<in P, R> {
    abstract suspend fun execute(parameters: P): Flow<R>

}