package com.t.quickapply.presentation.legal

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LegalSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun LegalSectionBody(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 18.sp,
        modifier = Modifier.padding(bottom = 14.dp)
    )
}

@Composable
fun TermsContent() {
    LegalSectionTitle("1. Acceptance")
    LegalSectionBody(
        "By using QuickApply, you agree to these terms. If you do not agree, please do not use the app."
    )

    LegalSectionTitle("2. What we do")
    LegalSectionBody(
        "QuickApply helps you create, manage, and organize job application drafts and send them via email at your discretion."
    )

    LegalSectionTitle("3. User responsibility")
    LegalSectionBody(
        "You are responsible for the content you create and send using QuickApply. The app does not send emails automatically without your action."
    )

    LegalSectionTitle("4. Data usage")
    LegalSectionBody(
        "Your CV drafts and application data are stored securely using Firebase. We do not sell or rent your personal data."
    )

    LegalSectionTitle("5. Google Sign-In")
    LegalSectionBody(
        "We use Google Sign-In (OAuth) for authentication. We access your name, email address, and basic profile information provided by Google. No passwords are collected or stored."
    )

    LegalSectionTitle("6. Prohibited use")
    LegalSectionBody(
        "You may not use QuickApply to send spam, misleading applications, or any content that violates applicable laws."
    )

    LegalSectionTitle("7. Changes")
    LegalSectionBody(
        "We may update these terms from time to time. Continued use of the app after changes means you accept the updated terms."
    )
}

@Composable
fun PrivacyContent() {
    LegalSectionTitle("1. Data we collect")
    LegalSectionBody(
        "We collect your name, email address (via Google Sign-In), and the CV drafts and application content you create."
    )

    LegalSectionTitle("2. How we use it")
    LegalSectionBody(
        "Your data is used solely to provide QuickApply features such as creating, managing, and sending job applications."
    )

    LegalSectionTitle("3. Storage")
    LegalSectionBody(
        "Data is securely stored using Firebase services provided by Google. We do not sell or rent your personal information."
    )

    LegalSectionTitle("4. Third-party services")
    LegalSectionBody(
        "We use Google Sign-In and Firebase for authentication and storage. These services are governed by Google's privacy policies."
    )

    LegalSectionTitle("5. Your rights")
    LegalSectionBody(
        "You may request deletion of your account and all associated data at any time from within the app."
    )

    LegalSectionTitle("6. Data deletion")
    LegalSectionBody(
        "When you request account deletion, your drafts, resumes, and personal data will be permanently removed within a reasonable time."
    )

    LegalSectionTitle("7. Contact")
    LegalSectionBody(
        "For privacy-related concerns, please contact us through the Profile section in the app."
    )
}