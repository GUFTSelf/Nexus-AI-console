package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.AuditLogEntry
import com.example.model.LocalExecutionRecord
import com.example.model.VerificationCase
import com.example.model.VerificationPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [VerificationCase::class, VerificationPolicy::class, AuditLogEntry::class, LocalExecutionRecord::class],
    version = 2,
    exportSchema = false
)
abstract class NexusDatabase : RoomDatabase() {
    abstract fun nexusDao(): NexusDao

    companion object {
        @Volatile
        private var INSTANCE: NexusDatabase? = null

        fun getDatabase(context: Context): NexusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NexusDatabase::class.java,
                    "nexus_ai_vek_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.nexusDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: NexusDao) {
                SampleData.getDemoCases().forEach { case ->
                    dao.insertCase(case)
                }
                SampleData.getDemoPolicies().forEach { policy ->
                    dao.insertPolicy(policy)
                }
                SampleData.getDemoAuditLogs().forEach { log ->
                    dao.insertAuditLog(log)
                }
            }
        }
    }
}
