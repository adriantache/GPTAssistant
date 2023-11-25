package com.adriantache.gptassistant.presentation.auth

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

private const val GCP_ID = "663313759833-8th7381j19pjoitd0rgk66mpoph8iki7.apps.googleusercontent.com"

@Composable
fun FirebaseAuthButton(
    modifier: Modifier = Modifier,
    onGotId: () -> Unit,
) {
    val context = LocalContext.current

    val mAuth = remember { FirebaseAuth.getInstance() }

    val startForResult = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(
                context = context,
                idToken = account.idToken,
                mAuth = mAuth,
                onGotId = onGotId,
            )
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
        }
    }

    val mGoogleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(GCP_ID)
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, gso)
    }

    Button(
        modifier = modifier.requiredHeight(48.dp),
        onClick = {
            val signInIntent = mGoogleSignInClient.signInIntent
            startForResult.launch(signInIntent)
        },
    ) {
        Text("Log in with Google")
    }
}

private fun firebaseAuthWithGoogle(
    context: Context,
    idToken: String?,
    mAuth: FirebaseAuth,
    onGotId: () -> Unit,
) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = mAuth.currentUser

                if (user?.uid != null) {
                    onGotId()
                } else {
                    Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
            }
        }
}
