package com.example.service

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.CustomCredential
import com.example.BuildConfig
import com.example.ui.AppTier
import com.example.ui.UserProfile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class AuthManager(private val context: Context) {

    private val auth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w("AuthManager", "Firebase initialization failed: ${e.message}")
            null
        }
    }

    private val _currentUserProfile = MutableStateFlow<UserProfile?>(null)
    val currentUserProfile: StateFlow<UserProfile?> = _currentUserProfile.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    sealed class AuthState {
        object LoggedOut : AuthState()
        object Loading : AuthState()
        data class LoggedIn(val profile: UserProfile) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    init {
        // Observe current Firebase user on start if Firebase is available
        try {
            auth?.addAuthStateListener { firebaseAuth ->
                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    updateProfileFromFirebaseUser(firebaseUser)
                } else if (_currentUserProfile.value == null) {
                    _currentUserProfile.value = null
                    _authState.value = AuthState.LoggedOut
                }
            }
        } catch (e: Exception) {
            Log.w("AuthManager", "Could not attach AuthStateListener: ${e.message}")
        }
    }

    private fun updateProfileFromFirebaseUser(user: FirebaseUser, tier: AppTier = _currentUserProfile.value?.tier ?: AppTier.PRO) {
        val profile = UserProfile(
            uid = user.uid,
            displayName = user.displayName ?: user.email?.substringBefore("@") ?: "Nexus Sentinel",
            email = user.email.orEmpty(),
            photoUrl = user.photoUrl?.toString(),
            tier = tier,
            isEnrolled = true
        )
        _currentUserProfile.value = profile
        _authState.value = AuthState.LoggedIn(profile)
    }

    // Google Sign-In using Credential Manager & Firebase Auth
    suspend fun signInWithGoogle(webClientId: String = ""): Result<UserProfile> {
        _authState.value = AuthState.Loading
        return try {
            val credentialManager = CredentialManager.create(context)
            
            val clientIdToUse = webClientId.ifBlank { BuildConfig.GOOGLE_WEB_CLIENT_ID }
            if (clientIdToUse.isBlank() || clientIdToUse == "MY_GOOGLE_WEB_CLIENT_ID") {
                throw IllegalStateException("Google OAuth client ID is not configured.")
            }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientIdToUse)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val firebaseAuth = auth
                if (firebaseAuth != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                    val user = authResult.user ?: throw IllegalStateException("Firebase user is null")

                    val profile = UserProfile(
                        uid = user.uid,
                        displayName = user.displayName ?: "Nexus Google User",
                        email = user.email.orEmpty(),
                        photoUrl = user.photoUrl?.toString(),
                        tier = AppTier.PRO,
                        isEnrolled = true
                    )
                    _currentUserProfile.value = profile
                    _authState.value = AuthState.LoggedIn(profile)
                    Result.success(profile)
                } else throw IllegalStateException("Firebase authentication is not configured.")
            } else {
                throw IllegalStateException("Google returned an unsupported credential type.")
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Google sign-in failed: ${e.message}", e)
            _authState.value = AuthState.Error(e.message ?: "Google sign-in failed.")
            Result.failure(e)
        }
    }

    // Firebase Email / Password Sign In or Create Account
    suspend fun signInWithEmail(email: String, pass: String, displayName: String, tier: AppTier): Result<UserProfile> {
        _authState.value = AuthState.Loading
        return try {
            val firebaseAuth = auth
            if (firebaseAuth != null) {
                val authResult = try {
                    firebaseAuth.signInWithEmailAndPassword(email, pass).await()
                } catch (e: Exception) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
                }
                val user = authResult.user ?: throw IllegalStateException("Firebase User Null")
                val profile = UserProfile(
                    uid = user.uid,
                    displayName = displayName.ifEmpty { user.email?.substringBefore("@") ?: "Nexus Member" },
                    email = user.email ?: email,
                    tier = tier,
                    isEnrolled = true
                )
                _currentUserProfile.value = profile
                _authState.value = AuthState.LoggedIn(profile)
                Result.success(profile)
            } else throw IllegalStateException("Firebase authentication is not configured.")
        } catch (e: Exception) {
            Log.e("AuthManager", "Email authentication failed: ${e.message}")
            _authState.value = AuthState.Error(e.message ?: "Email authentication failed.")
            Result.failure(e)
        }
    }

    fun enrollDemoUser(name: String, email: String, tier: AppTier): Result<UserProfile> {
        val profile = UserProfile(
            uid = "NX-GGL-" + System.currentTimeMillis().toString().takeLast(6),
            displayName = name.ifEmpty { "Google Certified User" },
            email = email.ifEmpty { "demo-local" },
            tier = tier,
            isEnrolled = true
        )
        _currentUserProfile.value = profile
        _authState.value = AuthState.LoggedIn(profile)
        return Result.success(profile)
    }

    fun updateUserTier(newTier: AppTier) {
        val current = _currentUserProfile.value
        if (current != null) {
            val updated = current.copy(tier = newTier)
            _currentUserProfile.value = updated
            _authState.value = AuthState.LoggedIn(updated)
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.e("AuthManager", "Sign out error", e)
        }
        _currentUserProfile.value = null
        _authState.value = AuthState.LoggedOut
    }

    suspend fun deleteAccount(): Result<Unit> {
        _authState.value = AuthState.Loading
        return try {
            auth?.currentUser?.delete()?.await()
            _currentUserProfile.value = null
            _authState.value = AuthState.LoggedOut
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthManager", "Account deletion failed", e)
            _authState.value = AuthState.Error(
                "Account deletion requires a recent sign-in. Sign in again and retry."
            )
            Result.failure(e)
        }
    }
}
