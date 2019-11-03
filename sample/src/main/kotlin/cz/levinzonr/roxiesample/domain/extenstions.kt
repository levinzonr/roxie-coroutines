package cz.levinzonr.roxiesample.domain

import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers as Dispatchers1

suspend fun <T> runInteractor(
    interactor: BaseAsyncInteractor<T>,
    coroutineContext: CoroutineContext = Dispatchers1.IO
): T {
    return withContext(coroutineContext) { interactor() }
}

suspend fun <T, R> InteractorResult<T>.isSuccess(block: suspend (T) -> R): InteractorResult<T> {
    if (this is Success) {
        block(this.data)
    }
    return this
}

suspend fun <T, R> InteractorResult<T>.isError(block: suspend (throwable: Throwable) -> R): InteractorResult<T> {
    if (this is Fail) {
        block(this.throwable)
    }
    return this
}

