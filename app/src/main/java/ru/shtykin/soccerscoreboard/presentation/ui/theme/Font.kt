package ru.shtykin.soccerscoreboard.presentation.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import ru.shtykin.soccer_scoreboard.R



val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val bebasNeueFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Bebas Neue"), fontProvider = provider)
)

val asapFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Asap"), fontProvider = provider)
)

val changaFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Changa"), fontProvider = provider)
)

