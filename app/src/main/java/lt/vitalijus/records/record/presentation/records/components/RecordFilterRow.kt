@file:OptIn(ExperimentalLayoutApi::class)

package lt.vitalijus.records.record.presentation.records.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.designsystem.chips.MultiChoiceChip
import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableDropDownOptionsMenu
import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableItem
import lt.vitalijus.records.record.presentation.records.RecordsAction
import lt.vitalijus.records.record.presentation.records.models.FilterChip
import lt.vitalijus.records.record.presentation.records.models.FilterItem
import lt.vitalijus.records.record.presentation.records.models.RecordFilterChipType

@Composable
fun RecordFilterRow(
    moodFilterChip: FilterChip.MoodFilterChip,
    topicFilterChip: FilterChip.TopicFilterChip,
    onAction: (RecordsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var dropDownOffset by remember {
        mutableStateOf(IntOffset.Zero)
    }
    val configuration = LocalConfiguration.current
    val maxDropDownHeight = (configuration.screenHeightDp * 0.3f).dp

    FlowRow(
        modifier = modifier
            .padding(16.dp)
            .onGloballyPositioned {
                dropDownOffset = IntOffset(
                    x = 0,
                    y = it.size.height
                )
            },
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Mood filter chip
        MultiChoiceChip(
            leadingContent = {
                if (moodFilterChip.content.iconsRes.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((-4).dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        moodFilterChip.content.iconsRes.forEach { iconRes ->
                            Image(
                                imageVector = ImageVector.vectorResource(iconRes),
                                contentDescription = moodFilterChip.content.title.asString(),
                                modifier = Modifier
                                    .height(16.dp)
                            )
                        }
                    }
                }
            },
            displayText = moodFilterChip.content.title.asString(),
            onClick = {
                onAction(RecordsAction.OnFilterChipClick(chipType = RecordFilterChipType.MOOD))
            },
            isClearButtonVisible = moodFilterChip.hasActiveFilters,
            onClearButtonClick = {
                onAction(RecordsAction.OnRemoveFilters(filterType = RecordFilterChipType.MOOD))
            },
            isHighlighted = moodFilterChip.hasActiveFilters,
            isDropDownVisible = moodFilterChip.isDropDownVisible,
            dropDownMenu = {
                SelectableDropDownOptionsMenu(
                    items = moodFilterChip.selectableItems,
                    itemDisplayText = { moodUi ->
                        moodUi.title.asString(context = context)
                    },
                    onDismiss = {
                        onAction(RecordsAction.OnDismissFilterDropDown(filterType = RecordFilterChipType.MOOD))
                    },
                    onItemClick = { moodUi ->
                        onAction(RecordsAction.OnFilterByItem(filterItem = FilterItem.MoodItem(moodUi = moodUi.item)))
                    },
                    dropDownOffset = dropDownOffset,
                    maxDropDownHeight = maxDropDownHeight,
                    leadingIcon = { moodUi ->
                        Image(
                            imageVector = ImageVector.vectorResource(moodUi.item.iconSet.fill),
                            contentDescription = moodUi.item.title.asString()
                        )
                    }
                )
            }
        )

        // Topic filter chip
        MultiChoiceChip(
            displayText = topicFilterChip.content.text.asString(),
            onClick = {
                onAction(RecordsAction.OnFilterChipClick(chipType = RecordFilterChipType.TOPIC))
            },
            isClearButtonVisible = topicFilterChip.hasActiveFilters,
            onClearButtonClick = {
                onAction(RecordsAction.OnRemoveFilters(filterType = RecordFilterChipType.TOPIC))
            },
            isHighlighted = topicFilterChip.hasActiveFilters,
            isDropDownVisible = topicFilterChip.isDropDownVisible,
            dropDownMenu = {
                if (topicFilterChip.content.text.asString().isEmpty()) {
                    SelectableDropDownOptionsMenu(
                        items = listOf(
                            SelectableItem(
                                item = stringResource(R.string.you_don_t_have_any_topics_yet),
                                selected = false
                            )
                        ),
                        itemDisplayText = { it },
                        onDismiss = {
                            onAction(RecordsAction.OnDismissFilterDropDown(filterType = RecordFilterChipType.TOPIC))
                        },
                        onItemClick = {

                        },
                        dropDownOffset = dropDownOffset,
                        maxDropDownHeight = maxDropDownHeight
                    )
                } else {
                    SelectableDropDownOptionsMenu(
                        items = topicFilterChip.selectableItems,
                        itemDisplayText = { topic ->
                            topic
                        },
                        onDismiss = {
                            onAction(RecordsAction.OnDismissFilterDropDown(filterType = RecordFilterChipType.TOPIC))
                        },
                        onItemClick = { topic ->
                            onAction(
                                RecordsAction.OnFilterByItem(
                                    filterItem = FilterItem.TopicItem(
                                        topic = topic.item
                                    )
                                )
                            )
                        },
                        dropDownOffset = dropDownOffset,
                        maxDropDownHeight = maxDropDownHeight,
                        leadingIcon = { topic ->
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.hashtag),
                                contentDescription = topic.item
                            )
                        }
                    )
                }
            }
        )
    }
}
