package com.br444n.constructionmaterialtrack.presentation.components.ui

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@Composable
fun LottieLoadingIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.spinner)
    )
    
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier.size(size)
    )
}

@Preview(showBackground = true)
@Composable
private fun LottieLoadingIconPreview() {
    ConstructionMaterialTrackTheme {
        LottieLoadingIcon()
    }
}

@Preview(showBackground = true, name = "Large Size")
@Composable
private fun LottieLoadingIconLargePreview() {
    ConstructionMaterialTrackTheme {
        LottieLoadingIcon(size = 48.dp)
    }
}