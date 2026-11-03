package com.example.ime.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyboardThemeColors

@Composable
fun SuggestionsBarView(
    suggestions: List<String>,
    colors: KeyboardThemeColors,
    onSuggestionClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestions.isEmpty()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(colors.background)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        suggestions.forEachIndexed { index, suggestion ->
            val isPrimary = index == 0
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isPrimary) colors.accent.copy(alpha = 0.2f) else colors.keyBackground)
                    .clickable { onSuggestionClicked(suggestion) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = suggestion,
                    color = if (isPrimary) colors.accent else colors.keyText,
                    fontSize = 14.sp,
                    fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
