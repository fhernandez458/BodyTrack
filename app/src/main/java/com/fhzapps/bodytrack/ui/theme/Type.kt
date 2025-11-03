package com.fhzapps.bodytrack.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fhzapps.bodytrack.R


// Set of Material typography styles to start with

val quantico = FontFamily(
    Font(R.font.quantico, FontWeight.Normal),
    Font(R.font.quantico_italic, FontWeight.SemiBold),
    Font(R.font.quantico_bold_italic, FontWeight.Bold)

)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = quantico,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = white1
    ),
    titleLarge = TextStyle(
        fontFamily = quantico,
        fontWeight = FontWeight.Bold,
        fontSize = 38.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        color = white1
    ),
    bodySmall = TextStyle(
        fontFamily = quantico,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 20.sp,
        color = white1
    ),
    bodyMedium = TextStyle(
        fontFamily = quantico,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        color = white1
    ),
    labelSmall = TextStyle(
        fontFamily = quantico,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = lightGray
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)