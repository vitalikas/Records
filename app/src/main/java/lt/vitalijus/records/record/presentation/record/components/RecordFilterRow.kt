@file:OptIn(ExperimentalLayoutApi::class)

package lt.vitalijus.records.record.presentation.record.components

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
import lt.vitalijus.records.core.presentation.util.UiText
import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.record.RecordAction
import lt.vitalijus.records.record.presentation.record.models.MoodChipContent
import lt.vitalijus.records.record.presentation.record.models.RecordFilterChipType

@Composable
fun RecordFilterRow(
    moodChipContent: MoodChipContent,
    hasActiveMoodFilters: Boolean,
    selectedFilterChipType: RecordFilterChipType?,
    moods: List<SelectableItem<MoodUi>>,
    topicChipTitle: UiText,
    hasActiveTopicFilters: Boolean,
    topics: List<SelectableItem<String>>,
    onAction: (RecordAction) -> Unit,
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
        MultiChoiceChip(
            displayText = moodChipContent.title.asString(),
            onClick = {
                onAction(RecordAction.OnMoodChipClick)
            },
            leadingContent = {
                if (moodChipContent.iconsRes.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((-4).dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        moodChipContent.iconsRes.forEach { iconRes ->
                            Image(
                                imageVector = ImageVector.vectorResource(iconRes),
                                contentDescription = moodChipContent.title.asString(),
                                modifier = Modifier
                                    .height(16.dp)
                            )
                        }
                    }
                }
            },
            isClearVisible = hasActiveMoodFilters,
            onClearButtonClick = {
                onAction(RecordAction.OnRemoveFilters(filterType = RecordFilterChipType.MOOD))
            },
            isDropDownVisible = selectedFilterChipType == RecordFilterChipType.MOOD,
            dropDownMenu = {
                SelectableDropDownOptionsMenu(
                    items = moods,
                    itemDisplayText = { moodUi ->
                        moodUi.title.asString(context = context)
                    },
                    onDismiss = {
                        onAction(RecordAction.OnDismissMoodDropDown)
                    },
                    onItemClick = { moodUi ->
                        onAction(RecordAction.OnFilterByMoodClick(moodUi = moodUi.item))
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
            },
            isHighlighted = hasActiveMoodFilters || selectedFilterChipType == RecordFilterChipType.MOOD
        )

        MultiChoiceChip(
            displayText = topicChipTitle.asString(),
            onClick = {
                onAction(RecordAction.OnTopicChipClick)
            },
            isClearVisible = hasActiveTopicFilters,
            onClearButtonClick = {
                onAction(RecordAction.OnRemoveFilters(filterType = RecordFilterChipType.TOPIC))
            },
            isDropDownVisible = selectedFilterChipType == RecordFilterChipType.TOPIC,
            dropDownMenu = {
                if (topics.isEmpty()) {
                    SelectableDropDownOptionsMenu(
                        items = listOf(
                            SelectableItem(
                                item = stringResource(R.string.you_don_t_have_any_topics_yet),
                                selected = false
                            )
                        ),
                        itemDisplayText = { it },
                        onDismiss = {
                            onAction(RecordAction.OnDismissTopicDropDown)
                        },
                        onItemClick = {

                        },
                        dropDownOffset = dropDownOffset,
                        maxDropDownHeight = maxDropDownHeight
                    )
                } else {
                    SelectableDropDownOptionsMenu(
                        items = topics,
                        itemDisplayText = { topic ->
                            topic
                        },
                        onDismiss = {
                            onAction(RecordAction.OnDismissTopicDropDown)
                        },
                        onItemClick = { topic ->
                            onAction(RecordAction.OnFilterByTopicClick(topic = topic.item))
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
            },
            isHighlighted = hasActiveTopicFilters || selectedFilterChipType == RecordFilterChipType.TOPIC
        )
    }
}
