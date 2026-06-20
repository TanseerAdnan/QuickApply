package com.t.quickapply.data.remote.gmail

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Properties
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class GmailSender(private val context: Context) {

    suspend fun sendEmail(
        toEmail: String,
        subject: String,
        body: String,
        cvFileUri: String? = null,
        cvFileName: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {

            if (!isNetworkAvailable()) {
                return@withContext Result.failure(Exception("No internet connection"))
            }
            val account = GoogleSignIn.getLastSignedInAccount(context)
                ?: return@withContext Result.failure(Exception("Not signed in"))

            val credential = GoogleAccountCredential
                .usingOAuth2(context, listOf(GmailScopes.GMAIL_SEND))
                .apply { selectedAccount = account.account }

            if (!GoogleSignIn.hasPermissions(
                    account,
                    Scope(GmailScopes.GMAIL_SEND)
                )
            ) {
                return@withContext Result.failure(
                    Exception("Gmail permission not granted")
                )
            }

            val gmail = Gmail.Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential
            ).setApplicationName("QuickApply").build()

            val message = buildMimeMessage(
                from = account.email ?: "",
                to = toEmail,
                subject = subject,
                body = body,
                cvFileUri = cvFileUri,
                cvFileName = cvFileName
            )

            val encodedEmail = encodeMessage(message)
            gmail.users().messages().send("me", encodedEmail).execute()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildMimeMessage(
        from: String,
        to: String,
        subject: String,
        body: String,
        cvFileUri: String?,
        cvFileName: String?
    ): MimeMessage {
        val props = Properties()
        val session = Session.getInstance(props)
        val email = MimeMessage(session)

        email.setFrom(InternetAddress(from))
        email.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(to))
        email.subject = subject

        if (!cvFileUri.isNullOrBlank() && !cvFileName.isNullOrBlank()) {
            // Multipart with attachment
            val multipart = MimeMultipart()

            // Body part
            val bodyPart = MimeBodyPart()
            bodyPart.setText(body)
            multipart.addBodyPart(bodyPart)

            // CV attachment
            val attachmentPart = MimeBodyPart()
            val uri = Uri.parse(cvFileUri)
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: byteArrayOf()
            inputStream?.close()

            attachmentPart.fileName = cvFileName
            val dataSource = javax.mail.util.ByteArrayDataSource(
                bytes,
                "application/pdf"
            )

            attachmentPart.dataHandler =
                javax.activation.DataHandler(dataSource)

            attachmentPart.fileName = cvFileName
            multipart.addBodyPart(attachmentPart)

            email.setContent(multipart)
        } else {
            email.setText(body)
        }

        return email
    }

    private fun encodeMessage(message: MimeMessage): Message {
        val baos = ByteArrayOutputStream()
        message.writeTo(baos)

        val encodedEmail = Base64.encodeToString(
            baos.toByteArray(),
            Base64.URL_SAFE or Base64.NO_WRAP
        )

        return Message().apply {
            raw = encodedEmail
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as android.net.ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}