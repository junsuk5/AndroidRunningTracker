package com.survivalcoding.runningtracker.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.runningtracker.domain.model.SortType
import com.survivalcoding.runningtracker.presentation.designsystem.AppTheme
import com.survivalcoding.runningtracker.presentation.designsystem.RunningTrackerTheme

@Composable
fun SortTypeSelector(
    selectedSortType: SortType,
    onSortTypeChange: (SortType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)
    ) {
        SortType.entries.forEach { sortType ->
            val isSelected = sortType == selectedSortType
            Surface(
                onClick = { onSortTypeChange(sortType) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.surface,
                contentColor = if (isSelected) AppTheme.colors.onPrimary else AppTheme.colors.onSurface,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = sortType.name.replace("_", " "),
                    style = AppTheme.typography.caption,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SortTypeSelectorPreview() {
    RunningTrackerTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SortTypeSelector(
                selectedSortType = SortType.DATE,
                onSortTypeChange = {}
            )
        }
    }
}
