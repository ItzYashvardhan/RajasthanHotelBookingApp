package com.justlime.hotelbooking.ui.common.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingText(rating: Float, modifier: Modifier = Modifier, tint: Color = Color(0xFFFFB300)) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val stars = remember(key1 = rating) {
            List(5) { i ->
                val index = i + 1
                when {
                    rating >= index -> Icons.Filled.Star
                    rating >= index - 0.5f -> Icons.AutoMirrored.Filled.StarHalf
                    else -> Icons.Filled.StarOutline
                }
            }
        }

        stars.forEach { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "$rating",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
