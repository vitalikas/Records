@file:OptIn(ExperimentalLayoutApi::class)

package lt.vitalijus.records.record.presentation.create_record.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.designsystem.chips.HashtagChip
import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableDropDownOptionsMenu
import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableItem
import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableItem.Companion.asUnselectedItems
import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableOptionExtras
import lt.vitalijus.records.core.presentation.designsystem.text_fields.TransparentHintTextField
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme

@Composable
fun TopicsRow(
    topics: List<String>,
    addTopicText: String,
    showCreateTopicOption: Boolean,
    showTopicSuggestions: Boolean,
    searchResults: List<SelectableItem<String>>,
    onTopicClick: (String) -> Unit,
    onDismissTopicSuggestions: () -> Unit,
    onRemoveTopicClick: (String) -> Unit,
    onAddTopicTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    dropDownMaxHeight: Dp = (LocalConfiguration.current.screenHeightDp * 0.3).dp
) {
    var topicRowHeight by remember {
        mutableIntStateOf(0)
    }

    Row(
        modifier = modifier
            .onSizeChanged {
                topicRowHeight = it.height
            }
    ) {
        Box(
            modifier = Modifier
                .height(32.dp)
                .width(32.dp)
                .align(Alignment.Top),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.hashtag),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant
            )
        }

        FlowRow(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .align(Alignment.CenterVertically),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            topics.forEach { topic ->
                HashtagChip(
                    text = topic,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.remove_topic, topic),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable {
                                    onRemoveTopicClick(topic)
                                }
                        )
                    }
                )
            }

            TransparentHintTextField(
                text = addTopicText,
                onValueChange = onAddTopicTextChange,
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                maxLines = 1,
                hintText = if (topics.isEmpty()) {
                    stringResource(R.string.topic)
                } else {
                    null
                }
            )
        }

        if (showTopicSuggestions) {
            SelectableDropDownOptionsMenu(
                items = searchResults,
                itemDisplayText = {
                    it
                },
                onDismiss = onDismissTopicSuggestions,
                onItemClick = {
                    onTopicClick(it.item)
                },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.hashtag),
                        contentDescription = null,
                        modifier = Modifier
                            .size(14.dp)
                    )
                },
                maxDropDownHeight = dropDownMaxHeight,
                dropDownOffset = IntOffset(
                    x = 0,
                    y = topicRowHeight
                ),
                dropDownExtras = if (showCreateTopicOption) {
                    SelectableOptionExtras(
                        text = addTopicText,
                        onClick = {
                            onTopicClick(addTopicText)
                        }
                    )
                } else {
                    null
                }
            )
        }
    }
}

@Preview
@Composable
private fun TopicRowPreview() {
    RecordsTheme {
        TopicsRow(
            topics = listOf("Love", "Work"),
            addTopicText = "he",
            showCreateTopicOption = true,
            showTopicSuggestions = true,
            searchResults = listOf(
                "hello",
                "helloworld"
            ).asUnselectedItems(),
            onTopicClick = {},
            onDismissTopicSuggestions = {},
            onRemoveTopicClick = {},
            onAddTopicTextChange = {}
        )
    }
}
