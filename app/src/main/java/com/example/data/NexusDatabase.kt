package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.BuildConfig
import com.example.model.AuditLogEntry
import com.example.model.VerificationCase
import com.example.model.VerificationPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [VerificationCase::class, VerificationPolicy::class, AuditLogEntry::class],
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
                .addMigrations(MIGRATION_1_2)
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE verification_cases ADD COLUMN executionMode TEXT NOT NULL DEFAULT 'CLAIM_VERIFICATION'"
                )
                db.execSQL(
                    "ALTER TABLE verification_cases ADD COLUMN canonicalRecordJson TEXT NOT NULL DEFAULT '{}'"
                )
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
                if (BuildConfig.DEBUG) {
                    SampleData.getDemoCases().forEach { case ->
                        dao.insertCase(case)
                    }
                    SampleData.getDemoAuditLogs().forEach { log ->
                        dao.insertAuditLog(log)
                    }
                }
                SampleData.getDemoPolicies().forEach { policy ->
                    dao.insertPolicy(policy)
                }
            }
        }
    }
}
