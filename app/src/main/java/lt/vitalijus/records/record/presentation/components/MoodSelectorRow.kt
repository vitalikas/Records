package lt.vitalijus.records.record.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme
import lt.vitalijus.records.record.presentation.models.MoodUi

@Composable
fun MoodSelectorRow(
    selectedMood: MoodUi?,
    onMoodClick: (mood: MoodUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MoodUi.entries.forEach { moodUi ->
            MoodItem(
                selected = moodUi == selectedMood,
                mood = moodUi,
                onClick = {
                    onMoodClick(moodUi)
                }
            )
        }
    }
}

@Preview
@Composable
private fun MoodSelectorRowPreview() {
    RecordsTheme {
        MoodSelectorRow(
            selectedMood = MoodUi.EXCITED,
            onMoodClick = {}
        )
    }
}
