package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.NexusDatabase
import com.example.data.SampleData
import com.example.model.*
import com.example.service.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppDestination(val title: String) {
    DETERMINISTIC_EXECUTION("Deterministic Execution"),
    REPLAY_COMPARISON("Replay Comparison"),
    CLAIM_VERIFICATION("Claim Verification"),
    LOCAL_HISTORY("Local History"),
    LANDING("Overview"),
    CONSOLE("AI Console"),
    RESULTS("Verification Report"),
    CONSUMER_DASHBOARD("Consumer Vault"),
    ENTERPRISE_WORKSPACE("Enterprise Workspaces"),
    POLICY_MANAGER("Policy & Rules"),
    EVIDENCE_LIBRARY("Evidence Library"),
    AUDIT_LOG("Audit Log"),
    QVEK_QUANTUM("QVEK Quantum"),
    LICENSING("VEK Licensing"),
    ABOUT("About GUTS Tech")
}

enum class ConsoleMode(val displayName: String, val subtitle: String) {
    VEK_KERNEL("VEK Verifiable Kernel", "Claim Verification, Canonical Execution & Replay"),
    GENERAL_CHAT("General AI Studio", "Conversational Reasoning Powered by Gemini"),
    STRUCTURED_JSON("Structured Output", "JSON Schema Generation & Data Extraction"),
    CODE_ANALYSIS("Code & Technical Studio", "Program Synthesis, Bug Detection & Logic Audit")
}

data class ConsoleHyperparameters(
    val selectedModel: String = "gemini-3.5-flash",
    val temperature: Float = 0.7f,
    val topP: Float = 0.95f,
    val topK: Int = 40,
    val systemInstruction: String = "You are Nexus AI, an advanced general artificial intelligence system equipped with a Verifiable Execution Kernel (VEK). Provide clear, direct, insightful, and accurate responses."
)

data class ConsoleChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String, // "USER" or "NEXUS_AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isVekVerified: Boolean = false,
    val modelUsed: String = "gemini-3.5-flash",
    val latencyMs: Long = 0L,
    val formattedJson: String? = null
)

data class ConsoleInputState(
    val rawInput: String = "",
    val domain: String = "General Consumer",
    val contentType: String = "Text / Article",
    val uploadedFileName: String? = null,
    val selectedDemoId: String? = null
)

data class VerificationProcessState(
    val isVerifying: Boolean = false,
    val stepNumber: Int = 0,
    val stepMessage: String = ""
)

class NexusViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NexusDatabase.getDatabase(application)
    private val dao = db.nexusDao()
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    // Navigation & UI Mode
    private val _currentDestination = MutableStateFlow(AppDestination.LANDING)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val _isEnterpriseMode = MutableStateFlow(false)
    val isEnterpriseMode: StateFlow<Boolean> = _isEnterpriseMode.asStateFlow()

    // General AI Console States
    private val _consoleMode = MutableStateFlow(ConsoleMode.VEK_KERNEL)
    val consoleMode: StateFlow<ConsoleMode> = _consoleMode.asStateFlow()

    private val _hyperparameters = MutableStateFlow(ConsoleHyperparameters())
    val hyperparameters: StateFlow<ConsoleHyperparameters> = _hyperparameters.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ConsoleChatMessage>>(
        listOf(
            ConsoleChatMessage(
                sender = "NEXUS_AI",
                text = "Welcome to Nexus AI Console. I am ready to process queries, generate code or structured output, or run deterministic VEK verifications. How can I assist you today?",
                isVekVerified = false,
                modelUsed = "gemini-3.5-flash"
            )
        )
    )
    val chatMessages: StateFlow<List<ConsoleChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatGenerating = MutableStateFlow(false)
    val isChatGenerating: StateFlow<Boolean> = _isChatGenerating.asStateFlow()

    // Structured JSON Studio state
    private val _structuredSchema = MutableStateFlow(
        """{
  "title": "string",
  "summary": "string",
  "keyFacts": ["string"],
  "trustScore": "number"
}"""
    )
    val structuredSchema: StateFlow<String> = _structuredSchema.asStateFlow()

    private val _structuredResult = MutableStateFlow<String?>(null)
    val structuredResult: StateFlow<String?> = _structuredResult.asStateFlow()

    // VEK Console Input State
    private val _inputState = MutableStateFlow(ConsoleInputState())
    val inputState: StateFlow<ConsoleInputState> = _inputState.asStateFlow()

    // Processing Progress
    private val _processState = MutableStateFlow(VerificationProcessState())
    val processState: StateFlow<VerificationProcessState> = _processState.asStateFlow()

    // Database Flows
    val allCases: StateFlow<List<VerificationCase>> = dao.getAllCases()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPolicies: StateFlow<List<VerificationPolicy>> = dao.getAllPolicies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AuditLogEntry>> = dao.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExecutionRecords: StateFlow<List<LocalExecutionRecord>> = dao.getAllExecutionRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Offline Mode States (Deterministic Execution & Replay)
    private val _deterministicResult = MutableStateFlow<DeterministicExecutionResult?>(null)
    val deterministicResult: StateFlow<DeterministicExecutionResult?> = _deterministicResult.asStateFlow()

    private val _replayResult = MutableStateFlow<ReplayComparisonResult?>(null)
    val replayResult: StateFlow<ReplayComparisonResult?> = _replayResult.asStateFlow()

    fun executeDeterministic(initialValue: Double, operations: List<MathOp>) {
        viewModelScope.launch {
            val result = CanonicalExecutionEngine.execute(initialValue, operations)
            _deterministicResult.value = result

            // Save execution record
            val record = LocalExecutionRecord(
                mode = "DETERMINISTIC_EXECUTION",
                title = "Deterministic Execution (Start: $initialValue, ${operations.size} ops)",
                initialValue = initialValue,
                finalValue = result.finalValue,
                canonicalJson = result.canonicalJson,
                sha256Hash = result.sha256Hash,
                replayCount = 1,
                passStatus = true,
                rawInput = "Initial: $initialValue, Ops: ${operations.joinToString { "${it.op} ${it.value}" }}"
            )
            dao.insertExecutionRecord(record)
        }
    }

    fun executeReplay(initialValue: Double, operations: List<MathOp>, replayCount: Int) {
        viewModelScope.launch {
            val result = CanonicalExecutionEngine.replay(initialValue, operations, replayCount)
            _replayResult.value = result

            val record = LocalExecutionRecord(
                mode = "REPLAY_COMPARISON",
                title = "Replay Comparison ($replayCount Runs, ${if (result.pass) "PASS" else "FAIL"})",
                initialValue = initialValue,
                finalValue = result.run1Final,
                canonicalJson = result.runs.firstOrNull()?.canonicalJson ?: "",
                sha256Hash = result.run1Hash,
                replayCount = replayCount,
                passStatus = result.pass,
                rawInput = "Replays: $replayCount, Operations: ${operations.joinToString { "${it.op} ${it.value}" }}"
            )
            dao.insertExecutionRecord(record)
        }
    }

    fun deleteExecutionRecord(id: String) {
        viewModelScope.launch {
            dao.deleteExecutionRecord(id)
        }
    }

    fun deleteAllLocalData() {
        viewModelScope.launch {
            dao.deleteAllExecutionRecords()
            dao.deleteAllVerificationCases()
            dao.deleteAllAuditLogs()
            _selectedCase.value = null
            _deterministicResult.value = null
            _replayResult.value = null
        }
    }


    // Active Selected Case
    private val _selectedCase = MutableStateFlow<VerificationCase?>(null)
    val selectedCase: StateFlow<VerificationCase?> = _selectedCase.asStateFlow()

    // Workspace Filter State
    private val _selectedWorkspaceDomain = MutableStateFlow("Healthcare")
    val selectedWorkspaceDomain: StateFlow<String> = _selectedWorkspaceDomain.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow<VerificationStatus?>(null)
    val statusFilter: StateFlow<VerificationStatus?> = _statusFilter.asStateFlow()

    // Auth & Tier Enrollment State
    val authManager = AuthManager(application)
    val currentUserProfile: StateFlow<UserProfile?> = authManager.currentUserProfile

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    init {
        // Initialize default enrolled profile if none logged in
        if (BuildConfig.DEBUG && currentUserProfile.value == null) {
            authManager.enrollDemoUser("Nexus Demo User", "demo-local", AppTier.PRO)
        }

        // Ensure initial demo cases exist if database is empty
        viewModelScope.launch {
            if (BuildConfig.DEBUG && dao.getCaseById("NX-2026-DEMO-01") == null) {
                SampleData.getDemoCases().forEach { dao.insertCase(it) }
                SampleData.getDemoPolicies().forEach { dao.insertPolicy(it) }
                SampleData.getDemoAuditLogs().forEach { dao.insertAuditLog(it) }
            }
        }
    }

    fun toggleAuthDialog(show: Boolean) {
        _showAuthDialog.value = show
    }

    suspend fun signInWithGoogle(): Result<UserProfile> {
        return authManager.signInWithGoogle()
    }

    suspend fun signInWithEmail(email: String, pass: String, displayName: String, tier: AppTier): Result<UserProfile> {
        return authManager.signInWithEmail(email, pass, displayName, tier)
    }

    fun enrollDemoUser(name: String, email: String, tier: AppTier) {
        authManager.enrollDemoUser(name, email, tier)
    }

    fun updateUserTier(tier: AppTier) {
        if (BuildConfig.DEBUG) authManager.updateUserTier(tier)
    }

    fun signOut() {
        authManager.signOut()
    }

    suspend fun deleteAccountAndLocalData(): Result<Unit> {
        val deletion = authManager.deleteAccount()
        if (deletion.isSuccess) {
            dao.deleteAllCases()
            dao.deleteAllAuditLogs()
            _selectedCase.value = null
            _inputState.value = ConsoleInputState()
            _currentDestination.value = AppDestination.LANDING
        }
        return deletion
    }

    fun navigateTo(destination: AppDestination) {
        _currentDestination.value = if (!BuildConfig.DEBUG && destination == AppDestination.LICENSING) {
            AppDestination.CONSOLE
        } else {
            destination
        }
    }

    fun toggleEnterpriseMode(enabled: Boolean) {
        _isEnterpriseMode.value = enabled
    }

    fun setConsoleMode(mode: ConsoleMode) {
        _consoleMode.value = mode
    }

    fun updateHyperparameters(
        model: String = hyperparameters.value.selectedModel,
        temp: Float = hyperparameters.value.temperature,
        topP: Float = hyperparameters.value.topP,
        sysInstruction: String = hyperparameters.value.systemInstruction
    ) {
        _hyperparameters.value = ConsoleHyperparameters(
            selectedModel = model,
            temperature = temp,
            topP = topP,
            systemInstruction = sysInstruction
        )
    }

    fun updateStructuredSchema(schema: String) {
        _structuredSchema.value = schema
    }

    // General AI Chat messaging with Gemini API
    fun sendChatMessage(userText: String) {
        val prompt = userText.trim()
        if (prompt.isEmpty() || _isChatGenerating.value) return

        val userMessage = ConsoleChatMessage(
            sender = "USER",
            text = prompt
        )
        _chatMessages.value = _chatMessages.value + userMessage
        _isChatGenerating.value = true

        val startTime = System.currentTimeMillis()
        val currentParams = _hyperparameters.value

        viewModelScope.launch {
            val replyText = try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (BuildConfig.DEBUG && apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
                    val request = GenerateContentRequest(
                        contents = listOf(
                            Content(role = "user", parts = listOf(Part(text = prompt)))
                        ),
                        systemInstruction = Content(parts = listOf(Part(text = currentParams.systemInstruction))),
                        generationConfig = GenerationConfig(
                            temperature = currentParams.temperature,
                            topP = currentParams.topP
                        )
                    )
                    val response = GeminiRetrofitClient.service.generateContent(
                        model = currentParams.selectedModel,
                        apiKey = apiKey,
                        request = request
                    )
                    response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        ?: "No text generated."
                } else {
                    generateLocalAiResponse(prompt, currentParams.selectedModel)
                }
            } catch (e: Exception) {
                generateLocalAiResponse(prompt, currentParams.selectedModel)
            }

            val latency = System.currentTimeMillis() - startTime
            val aiMessage = ConsoleChatMessage(
                sender = "NEXUS_AI",
                text = replyText,
                isVekVerified = false,
                modelUsed = currentParams.selectedModel,
                latencyMs = latency
            )
            _chatMessages.value = _chatMessages.value + aiMessage
            _isChatGenerating.value = false
        }
    }

    // Generate Structured JSON
    fun runStructuredGeneration(prompt: String) {
        if (prompt.trim().isEmpty() || _isChatGenerating.value) return
        _isChatGenerating.value = true
        _structuredResult.value = null

        val currentParams = _hyperparameters.value
        val schema = _structuredSchema.value

        viewModelScope.launch {
            val jsonResult = try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (BuildConfig.DEBUG && apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
                    val combinedPrompt = "Prompt: $prompt\n\nEnsure response strictly adheres to this JSON schema:\n$schema"
                    val request = GenerateContentRequest(
                        contents = listOf(Content(role = "user", parts = listOf(Part(text = combinedPrompt)))),
                        systemInstruction = Content(parts = listOf(Part(text = "You are a JSON generator. Respond ONLY with valid JSON."))),
                        generationConfig = GenerationConfig(
                            responseMimeType = "application/json",
                            temperature = currentParams.temperature
                        )
                    )
                    val response = GeminiRetrofitClient.service.generateContent(
                        model = currentParams.selectedModel,
                        apiKey = apiKey,
                        request = request
                    )
                    response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "{}"
                } else {
                    """
                    {
                      "status": "AI_SERVICE_NOT_CONFIGURED",
                      "message": "Structured AI generation is unavailable in this build. Deterministic VEK execution remains available offline."
                    }
                    """.trimIndent()
                }
            } catch (e: Exception) {
                """
                {
                  "error": "Failed to connect to API: ${e.message}",
                  "status": "FALLBACK_SCHEMA_GENERATED",
                  "prompt": "$prompt"
                }
                """.trimIndent()
            }

            _structuredResult.value = jsonResult
            _isChatGenerating.value = false
        }
    }

    // Verify Chat Message in VEK Kernel
    fun verifyChatMessageWithVek(messageText: String) {
        _inputState.value = ConsoleInputState(
            rawInput = messageText,
            domain = "General Consumer",
            contentType = "AI Output Claim"
        )
        _consoleMode.value = ConsoleMode.VEK_KERNEL
        runVerification()
    }

    private fun generateLocalAiResponse(prompt: String, model: String): String =
        "AI conversation is unavailable in this build because a protected server-side AI gateway is not configured. " +
            "No model response or verification claim was generated. Use VEK Kernel mode for offline deterministic execution and replay."

    fun updateRawInput(text: String) {
        _inputState.value = _inputState.value.copy(rawInput = text, selectedDemoId = null)
    }

    fun updateDomain(domain: String) {
        _inputState.value = _inputState.value.copy(domain = domain)
    }

    fun updateContentType(type: String) {
        _inputState.value = _inputState.value.copy(contentType = type)
    }

    fun simulateFileUpload(fileName: String) {
        _inputState.value = _inputState.value.copy(
            uploadedFileName = fileName,
            rawInput = "Extracted content from uploaded file ($fileName):\n" + (_inputState.value.rawInput.ifEmpty { "Verified system certification compliance log for enterprise audit." })
        )
    }

    fun clearInput() {
        _inputState.value = ConsoleInputState()
    }

    fun loadDemoCase(demoCaseId: String) {
        viewModelScope.launch {
            val demoCase = dao.getCaseById(demoCaseId)
            if (demoCase != null) {
                _inputState.value = ConsoleInputState(
                    rawInput = demoCase.rawInput,
                    domain = demoCase.domain,
                    contentType = demoCase.contentType,
                    selectedDemoId = demoCaseId
                )
                _selectedCase.value = demoCase
            }
        }
    }

    fun runVerification() {
        val input = _inputState.value.rawInput.trim()
        if (input.isEmpty()) return

        viewModelScope.launch {
            _processState.value = VerificationProcessState(isVerifying = true, stepNumber = 1, stepMessage = "Initializing VEK Kernel Engine...")

            val policies = allPolicies.value
            val resultCase = VekEngine.verifyClaim(
                rawInput = input,
                domain = _inputState.value.domain,
                contentType = _inputState.value.contentType,
                policies = policies,
                onProgressUpdate = { step, msg ->
                    _processState.value = VerificationProcessState(isVerifying = true, stepNumber = step, stepMessage = msg)
                }
            )

            dao.insertCase(resultCase)

            // Insert audit log
            val auditLog = AuditLogEntry(
                timestamp = System.currentTimeMillis(),
                caseId = resultCase.caseId,
                action = if (resultCase.executionMode == NexusIntent.CLAIM_VERIFICATION.name) {
                    "VEK Claim Verification Executed"
                } else {
                    "VEK Canonical Execution Completed"
                },
                userRole = if (_isEnterpriseMode.value) "Enterprise Auditor" else "Consumer Verifier",
                department = resultCase.domain,
                details = if (resultCase.executionMode == NexusIntent.CLAIM_VERIFICATION.name) {
                    "Executed VEK claim pipeline. Result: ${resultCase.status.displayName}. Trust Score: ${parseScores(resultCase.scoresJson).overallTrust}%"
                } else {
                    "Executed ${resultCase.executionMode}. Result: ${resultCase.status.displayName}. Canonical SHA-256: ${resultCase.demonstrationTraceHash}"
                },
                traceHash = resultCase.demonstrationTraceHash
            )
            dao.insertAuditLog(auditLog)

            _selectedCase.value = resultCase
            _processState.value = VerificationProcessState(isVerifying = false, stepNumber = 10, stepMessage = "Done")
            _currentDestination.value = AppDestination.RESULTS
        }
    }

    fun selectCaseForDetail(caseItem: VerificationCase) {
        _selectedCase.value = caseItem
        _currentDestination.value = AppDestination.RESULTS
    }

    fun updateHumanReviewStatus(caseId: String, newStatus: String, notes: String) {
        viewModelScope.launch {
            val caseItem = dao.getCaseById(caseId) ?: return@launch
            val updatedCase = caseItem.copy(
                reviewerStatus = newStatus,
                reviewerNotes = notes
            )
            dao.updateCase(updatedCase)
            _selectedCase.value = updatedCase

            val auditLog = AuditLogEntry(
                timestamp = System.currentTimeMillis(),
                caseId = caseId,
                action = "Human Reviewer Decided: $newStatus",
                userRole = "Compliance Officer",
                department = caseItem.domain,
                details = "Reviewer Notes: $notes",
                traceHash = caseItem.demonstrationTraceHash
            )
            dao.insertAuditLog(auditLog)
        }
    }

    fun deleteCase(caseId: String) {
        viewModelScope.launch {
            dao.deleteCase(caseId)
            if (_selectedCase.value?.caseId == caseId) {
                _selectedCase.value = null
                _currentDestination.value = AppDestination.CONSUMER_DASHBOARD
            }
        }
    }

    fun togglePolicy(policyId: String, enabled: Boolean) {
        viewModelScope.launch {
            val policy = allPolicies.value.find { it.id == policyId } ?: return@launch
            val updated = policy.copy(enabled = enabled)
            dao.updatePolicy(updated)
        }
    }

    fun setWorkspaceDomain(domain: String) {
        _selectedWorkspaceDomain.value = domain
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: VerificationStatus?) {
        _statusFilter.value = status
    }

    // Helper functions for UI JSON parsing
    fun parseClaims(claimsJson: String): List<DecomposedClaim> {
        return try {
            val type = Types.newParameterizedType(List::class.java, DecomposedClaim::class.java)
            moshi.adapter<List<DecomposedClaim>>(type).fromJson(claimsJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseScores(scoresJson: String): TrustScores {
        return try {
            moshi.adapter(TrustScores::class.java).fromJson(scoresJson) ?: TrustScores(50, 50, 50, 50, 50, 50)
        } catch (e: Exception) {
            TrustScores(50, 50, 50, 50, 50, 50)
        }
    }

    fun parseRules(rulesJson: String): List<RuleResult> {
        return try {
            val type = Types.newParameterizedType(List::class.java, RuleResult::class.java)
            moshi.adapter<List<RuleResult>>(type).fromJson(rulesJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseStringList(jsonString: String): List<String> {
        return try {
            val type = Types.newParameterizedType(List::class.java, String::class.java)
            moshi.adapter<List<String>>(type).fromJson(jsonString) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
