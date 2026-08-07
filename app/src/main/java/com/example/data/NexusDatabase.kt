package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.BuildConfig
import com.example.model.AuditLogEntry
import com.example.model.LocalExecutionRecord
import com.example.model.VerificationCase
import com.example.model.VerificationPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [VerificationCase::class, VerificationPolicy::class, AuditLogEntry::class, LocalExecutionRecord::class],
    version = 3,
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
                .addMigrations(MIGRATION_1_3, MIGRATION_2_3)
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_3 = object : Migration(1, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureExecutionSchema(db)
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureExecutionSchema(db)
            }
        }

        private fun ensureExecutionSchema(db: SupportSQLiteDatabase) {
            if (!hasColumn(db, "verification_cases", "executionMode")) {
                db.execSQL(
                    "ALTER TABLE verification_cases ADD COLUMN executionMode TEXT NOT NULL DEFAULT 'CLAIM_VERIFICATION'"
                )
            }
            if (!hasColumn(db, "verification_cases", "canonicalRecordJson")) {
                db.execSQL(
                    "ALTER TABLE verification_cases ADD COLUMN canonicalRecordJson TEXT NOT NULL DEFAULT '{}'"
                )
            }
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS local_execution_records (
                    id TEXT NOT NULL,
                    timestamp INTEGER NOT NULL,
                    mode TEXT NOT NULL,
                    title TEXT NOT NULL,
                    initialValue REAL NOT NULL,
                    finalValue REAL NOT NULL,
                    canonicalJson TEXT NOT NULL,
                    sha256Hash TEXT NOT NULL,
                    replayCount INTEGER NOT NULL,
                    passStatus INTEGER NOT NULL,
                    rawInput TEXT NOT NULL,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
            )
        }

        private fun hasColumn(
            db: SupportSQLiteDatabase,
            table: String,
            column: String
        ): Boolean = db.query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (nameIndex >= 0 && cursor.getString(nameIndex) == column) return@use true
            }
            false
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
                    SampleData.getDemoPolicies().forEach { policy ->
                        dao.insertPolicy(policy)
                    }
                }
            }
        }
    }
}
