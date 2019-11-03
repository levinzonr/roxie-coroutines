package cz.levinzonr.roxiesample.domain

interface BaseAsyncInteractor<O> {
    suspend operator fun invoke(): O
}