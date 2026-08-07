package com.example.service

import com.example.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class GatewayHealthResponse(
    val status: String,
    val service: String,
    val version: String,
    val vekKernelReady: Boolean,
    val timestamp: String
)

data class VerifyClaimGatewayRequest(
    val rawInput: String,
    val domain: String,
    val contentType: String,
    val integerOnly: Boolean = false
)

data class VerifyClaimGatewayResponse(
    val status: String,
    val caseId: String,
    val domain: String,
    val contentType: String,
    val rawInput: String,
    val traceId: String,
    val canonicalHash: String,
    val requiresAuthenticatedEvidence: Boolean,
    val verifiedStatus: Boolean,
    val assessmentSummary: String,
    val timestamp: String
)

data class EvidenceRecordGatewayRequest(
    val caseId: String,
    val evidenceTitle: String,
    val publisher: String? = null,
    val sourceUrl: String? = null,
    val isPrimarySource: Boolean = false
)

data class EvidenceRecordGatewayResponse(
    val evidenceId: String,
    val caseId: String,
    val evidenceTitle: String,
    val publisher: String?,
    val isPrimarySource: Boolean,
    val canonicalJson: String,
    val sha256Commitment: String,
    val authenticatedUser: String,
    val timestamp: String
)

data class ReplayGatewayRequest(
    val initialValue: Double,
    val operations: List<MathOp>,
    val replayCount: Int = 2,
    val integerOnly: Boolean = false
)

interface NexusVerificationGatewayService {

    @GET("health")
    suspend fun getHealth(): GatewayHealthResponse

    @POST("v1/verify-claim")
    suspend fun verifyClaim(
        @Header("Authorization") bearerToken: String?,
        @Header("X-Firebase-AppCheck") appCheckToken: String?,
        @Body request: VerifyClaimGatewayRequest
    ): VerifyClaimGatewayResponse

    @POST("v1/evidence-record")
    suspend fun recordEvidence(
        @Header("Authorization") bearerToken: String?,
        @Header("X-Firebase-AppCheck") appCheckToken: String?,
        @Body request: EvidenceRecordGatewayRequest
    ): EvidenceRecordGatewayResponse

    @POST("v1/replay-verification")
    suspend fun replayVerification(
        @Header("Authorization") bearerToken: String?,
        @Header("X-Firebase-AppCheck") appCheckToken: String?,
        @Body request: ReplayGatewayRequest
    ): ReplayComparisonResult
}

object NexusVerificationGatewayClient {
    val serviceOrNull: NexusVerificationGatewayService? by lazy {
        val configuredUrl = BuildConfig.NEXUS_GATEWAY_BASE_URL.trim()
        if (configuredUrl.isBlank() || configuredUrl.equals("DISABLED", ignoreCase = true)) {
            return@lazy null
        }
        val normalizedUrl = if (configuredUrl.endsWith('/')) configuredUrl else "$configuredUrl/"
        if (!BuildConfig.DEBUG && !normalizedUrl.startsWith("https://")) return@lazy null

        Retrofit.Builder()
            .baseUrl(normalizedUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(NexusVerificationGatewayService::class.java)
    }
}
