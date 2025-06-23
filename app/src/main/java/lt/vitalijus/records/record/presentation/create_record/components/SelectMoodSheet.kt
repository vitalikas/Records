@file:OptIn(ExperimentalMaterial3Api::class)

package lt.vitalijus.records.record.presentation.create_record.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.designsystem.buttons.PrimaryButton
import lt.vitalijus.records.core.presentation.designsystem.buttons.SecondaryButton
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme
import lt.vitalijus.records.record.presentation.components.MoodSelectorRow
import lt.vitalijus.records.record.presentation.models.MoodUi

@Composable
fun SelectMoodSheet(
    selectedMood: MoodUi,
    onMoodClick: (mood: MoodUi) -> Unit,
    onDismiss: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        SelectMoodSheetContent(
            modifier,
            selectedMood,
            onMoodClick,
            onDismiss,
            onConfirmClick
        )
    }
}

@Composable
private fun SelectMoodSheetContent(
    modifier: Modifier,
    selectedMood: MoodUi,
    onMoodClick: (mood: MoodUi) -> Unit,
    onDismiss: () -> Unit,
    onConfirmClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.how_are_you_doing),
            style = MaterialTheme.typography.titleMedium
        )

        MoodSelectorRow(
            selectedMood = selectedMood,
            onMoodClick = onMoodClick
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryButton(
                text = stringResource(R.string.cancel),
                onClick = onDismiss
            )

            PrimaryButton(
                text = stringResource(R.string.confirm),
                onClick = onConfirmClick,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.confirm)
                    )
                },
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Preview
@Composable
private fun SelectMoodSheetContentPreview() {
    RecordsTheme {
        SelectMoodSheetContent(
            selectedMood = MoodUi.EXCITED,
            onMoodClick = {},
            onDismiss = {},
            onConfirmClick = {},
            modifier = Modifier
        )
    }
}
