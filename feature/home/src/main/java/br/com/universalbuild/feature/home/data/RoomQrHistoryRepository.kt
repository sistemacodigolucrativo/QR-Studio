package br.com.universalbuild.feature.home.data

import br.com.universalbuild.core.database.SavedItem
import br.com.universalbuild.core.database.SavedItemDao
import br.com.universalbuild.feature.home.domain.QrHistoryItem
import br.com.universalbuild.feature.home.domain.QrHistoryRepository
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Reuses the template's v1 Room table without a destructive schema change. */
class RoomQrHistoryRepository(private val dao: SavedItemDao) : QrHistoryRepository {
    override fun observeRecent(): Flow<List<QrHistoryItem>> = dao.observeAll().map { items ->
        items.map { item -> QrHistoryItem(item.id, item.title, item.createdAt) }
    }

    override suspend fun save(payload: String) {
        dao.save(SavedItem(UUID.randomUUID().toString(), payload, System.currentTimeMillis()))
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
    }
}
