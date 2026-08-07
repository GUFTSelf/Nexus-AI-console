package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.AuditLogEntry
import com.example.model.LocalExecutionRecord
import com.example.model.VerificationCase
import com.example.model.VerificationPolicy
import kotlinx.coroutines.flow.Flow

@Dao
interface NexusDao {
    @Query("SELECT * FROM verification_cases ORDER BY timestamp DESC")
    fun getAllCases(): Flow<List<VerificationCase>>

    @Query("SELECT * FROM verification_cases WHERE caseId = :caseId")
    suspend fun getCaseById(caseId: String): VerificationCase?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(case: VerificationCase)

    @Update
    suspend fun updateCase(case: VerificationCase)

    @Query("DELETE FROM verification_cases WHERE caseId = :caseId")
    suspend fun deleteCase(caseId: String)

    @Query("SELECT * FROM verification_policies")
    fun getAllPolicies(): Flow<List<VerificationPolicy>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolicy(policy: VerificationPolicy)

    @Update
    suspend fun updatePolicy(policy: VerificationPolicy)

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntry)

    @Query("SELECT * FROM local_execution_records ORDER BY timestamp DESC")
    fun getAllExecutionRecords(): Flow<List<LocalExecutionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExecutionRecord(record: LocalExecutionRecord)

    @Query("DELETE FROM local_execution_records WHERE id = :id")
    suspend fun deleteExecutionRecord(id: String)

    @Query("DELETE FROM local_execution_records")
    suspend fun deleteAllExecutionRecords()

    @Query("DELETE FROM verification_cases")
    suspend fun deleteAllVerificationCases()

    @Query("DELETE FROM audit_logs")
    suspend fun deleteAllAuditLogs()
}
