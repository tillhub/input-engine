package de.tillhub.inputengine.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import de.tillhub.inputengine.resources.Inter_VariableFont
import de.tillhub.inputengine.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
internal fun typography() = Typography(
    displayLarge = TextStyle(
        fontSize = 40.sp,
        lineHeight = 40.sp,
        fontFamily = FontFamily(Font(Res.font.Inter_VariableFont, FontWeight.Bold)),
        fontWeight = FontWeight(700),
        textAlign = TextAlign.Center,
    ),
    titleLarge = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontFamily = FontFamily(Font(Res.font.Inter_VariableFont, FontWeight.Bold)),
        fontWeight = FontWeight(600),
    ),

    labelLarge = TextStyle(
        fontSize = 20.sp,
        lineHeight = 24.sp,
        fontFamily = FontFamily(Font(Res.font.Inter_VariableFont, FontWeight.Bold)),
        fontWeight = FontWeight(600),
    ),
    labelMedium = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontFamily = FontFamily(Font(Res.font.Inter_VariableFont, FontWeight.Medium)),
        fontWeight = FontWeight(600),
        letterSpacing = 0.15.sp,
    ),
    labelSmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontFamily = FontFamily(Font(Res.font.Inter_VariableFont)),
        fontWeight = FontWeight(400),
        letterSpacing = 0.04.sp,
    ),
)
