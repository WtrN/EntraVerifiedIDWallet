package com.example.entraverifiedidwallet

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestVCUrl(
    onNavigate: (String) -> Unit,
) {
    var url by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(
            horizontal = 16.dp, vertical = 16.dp
        )
    ) {
        val client = entraProvider()
        TextField(value = url, onValueChange = { url = it })
        Button(
            onClick = {
                Log.d("hoge", url)
                scope.launch {
                    val result = client.createRequest(url)
                    result.fold(
                        onSuccess = {
                            val query = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                            onNavigate.invoke(query)
                        },
                        onFailure = {
//                            onNavigate.invoke(url)
                        }
                    )
                }
            },
            interactionSource = remember { MutableInteractionSource() },
        ) {
            Text(text = "Request")
        }
    }
}

@Composable
fun entraProvider(): EntraVerifiedClient {
    val context = LocalContext.current

    return remember(context) {
        EntraVerifiedClient(context = context)
    }
}

data class EntraVerifiedClient(val context: Context) {
    private val client = VerifiedIdClientBuilder(context).build()

    private val TAG = "EntraVerifiedClient"

    private var requestUrl: String = ""

    private var request: VerifiedIdIssuanceRequest? = null

    suspend fun createRequest(url: String): Result<VerifiedIdIssuanceRequest> {
        requestUrl = url
        Log.i(TAG, "URL: requestUrl")
        val verifiedIdRequestUrl = VerifiedIdRequestURL(Uri.parse(requestUrl))
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
                Result.failure<VerifiedId>(exception = Exception())
            }

            return request!!.complete()
        }

        return Result.failure(Exception())
    }
}