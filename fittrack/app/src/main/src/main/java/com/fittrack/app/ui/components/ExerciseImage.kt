package com.fittrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fittrack.app.data.remote.WgerImageRepository

@Composable
fun ExerciseImage(
    exerciseName: String,
    imageRepository: WgerImageRepository,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    var imageUrl by remember(exerciseName) { mutableStateOf<String?>(null) }
    var loaded by remember(exerciseName) { mutableStateOf(false) }

    LaunchedEffect(exerciseName) {
        imageUrl = imageRepository.getImageUrl(exerciseName)
        loaded = true
    }

    if (imageUrl != null) {
        AsyncImage(
            model = imageUrl,
            contentDescription = exerciseName,
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = if (loaded) 0.4f else 0.15f
                ),
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}
