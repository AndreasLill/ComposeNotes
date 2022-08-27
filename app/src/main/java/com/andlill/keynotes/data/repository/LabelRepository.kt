package com.andlill.keynotes.data.repository

import android.content.Context
import com.andlill.keynotes.data.database.AppDatabase
import com.andlill.keynotes.model.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

object LabelRepository {

    suspend fun insertLabel(context: Context, label: Label) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).labelDao.insertLabel(label)
    }

    suspend fun updateLabel(context: Context, label: Label) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).labelDao.updateLabel(label)
    }

    suspend fun deleteLabel(context: Context, label: Label) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).labelDao.deleteLabel(label)
    }

    fun getAllLabels(context: Context): Flow<List<Label>> {
        return AppDatabase.get(context).labelDao.getAllLabels().flowOn(Dispatchers.IO)
    }
}