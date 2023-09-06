package com.example.entraverifiedidwallet.verifiedid

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.microsoft.walletlibrary.VerifiedIdClientBuilder
import com.microsoft.walletlibrary.requests.VerifiedIdIssuanceRequest
import com.microsoft.walletlibrary.requests.VerifiedIdRequest
import com.microsoft.walletlibrary.requests.input.VerifiedIdRequestURL
import com.microsoft.walletlibrary.requests.requirements.GroupRequirement
import com.microsoft.walletlibrary.requests.requirements.IdTokenRequirement
import com.microsoft.walletlibrary.requests.requirements.PinRequirement
import com.microsoft.walletlibrary.requests.requirements.SelfAttestedClaimRequirement
import com.microsoft.walletlibrary.requests.requirements.VerifiedIdRequirement
import com.microsoft.walletlibrary.verifiedid.VerifiedId

class EntraClientViewModel(context: Context) : ViewModel() {

    companion object {
        fun provideViewModel(context: Context) =
            viewModelFactory { initializer { EntraClientViewModel(context) } }
    }


    private val client = VerifiedIdClientBuilder(context).build()

    private val TAG = "EntraVerifiedClient"

    private var request: VerifiedIdIssuanceRequest? = null
    suspend fun createRequest(url: String): Result<VerifiedIdIssuanceRequest> {
        Log.i(TAG, "URL: requestUrl")
        val verifiedIdRequestUrl = VerifiedIdRequestURL(Uri.parse(url))
        val verifiedIdRequestResult: Result<VerifiedIdRequest<*>> =
            client.createRequest(verifiedIdRequestUrl)
        Log.i(TAG, "Client HASH: ${client.hashCode()}")

        return if (verifiedIdRequestResult.isSuccess) {
            val verifiedIdRequest = verifiedIdRequestResult.getOrNull()
            request = verifiedIdRequest?.let {
                verifiedIdRequest as VerifiedIdIssuanceRequest
            }
            if (request != null) {
                Result.success(request!!)
            } else {
                Result.failure(exception = Exception())
            }

        } else {
            // If an exception occurs, its value can be accessed here.
            val exception = verifiedIdRequestResult.exceptionOrNull()
            Result.failure(exception = exception!!)
        }
    }

    suspend fun pinInput(pin: String): Result<VerifiedId> {
        val requirement = request?.requirement
        if (requirement is GroupRequirement) {
            requirement.requirements.forEach {
                when (it) {
                    is PinRequirement -> it.fulfill(pin)
                    is IdTokenRequirement -> Log.i("IdToken", it.clientId)
                    is SelfAttestedClaimRequirement -> Log.i("SelfAttestedClaim", it.claim)
                    is VerifiedIdRequirement -> Log.i("VerifiedIdRequirement", it.purpose)
                    else -> Unit
                }
            }
            val requirementReady = requirement.validate()
            if (requirementReady.isFailure) {
                Result.failure<VerifiedId>(exception = Exception())
            }
        }
        if (requirement is PinRequirement) {
            requirement.fulfill(pin)
            val requirementReady = requirement.validate()
            if (requirementReady.isFailure) {
                return Result.failure(exception = Exception())
            }
        }

        return request!!.complete()
    }
}