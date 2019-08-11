package com.ww.roxiesample.domain

interface BaseAsyncInteractor<O> {
    suspend operator fun invoke(): O
}