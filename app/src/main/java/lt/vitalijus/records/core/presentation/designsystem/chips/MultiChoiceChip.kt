package lt.vitalijus.records.core.presentation.designsystem.chips

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme

@Composable
fun MultiChoiceChip(
    modifier: Modifier = Modifier,
    displayText: String,
    onClick: () -> Unit,
    isClearVisible: Boolean,
    onClearButtonClick: () -> Unit,
    isHighlighted: Boolean,
    isDropDownVisible: Boolean,
    dropDownMenu: @Composable () -> Unit,
    leadingContent: (@Composable () -> Unit)? = null
) {
    val containerColor = if (isHighlighted) {
        MaterialTheme.colorScheme.surface
    } else {
        Color.Transparent
    }

    val boarderColor = if (isHighlighted) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = modifier
            .then(
                other = if (isHighlighted) {
                    Modifier.shadow(
                        elevation = 4.dp,
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape = CircleShape)
            .border(
                width = 0.5.dp,
                color = boarderColor,
                shape = CircleShape
            )
            .background(color = containerColor)
            .clickable(onClick = onClick)
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.Center)
        ) {
            leadingContent?.invoke()

            Text(
                text = displayText,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary
            )

            AnimatedVisibility(
                visible = isClearVisible
            ) {
                IconButton(
                    onClick = onClearButtonClick,
                    modifier = Modifier
                        .size(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear_selections),
                        tint = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }
        }

        if (isDropDownVisible) {
            dropDownMenu()
        }
    }
}

@Preview
@Composable
private fun MultiChoiceChipPreview() {
    RecordsTheme {
        MultiChoiceChip(
            displayText = "All topics",
            onClick = {},
            isClearVisible = true,
            onClearButtonClick = {},
            isHighlighted = true,
            isDropDownVisible = true,
            dropDownMenu = {},
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null
                )
            }
        )
    }
}
