package br.com.universalbuild.feature.home.domain

import kotlinx.coroutines.flow.Flow

data class QrHistoryItem(val id: String, val payload: String, val createdAt: Long)

interface QrHistoryRepository {
    fun observeRecent(): Flow<List<QrHistoryItem>>

    suspend fun save(payload: String)

    suspend fun delete(id: String)
}
