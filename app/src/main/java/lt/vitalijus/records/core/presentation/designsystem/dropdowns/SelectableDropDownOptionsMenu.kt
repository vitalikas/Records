package lt.vitalijus.records.core.presentation.designsystem.dropdowns

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme

@Composable
fun <T : Any> SelectableDropDownOptionsMenu(
    items: List<SelectableItem<T>>,
    itemDisplayText: @Composable (T) -> String,
    onDismiss: () -> Unit,
    onItemClick: (SelectableItem<T>) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable (SelectableItem<T>) -> Unit)? = null,
    dropDownOffset: IntOffset = IntOffset.Zero,
    maxDropDownHeight: Dp = Dp.Unspecified,
    dropDownExtras: SelectableOptionExtras? = null
) {
    Popup(
        onDismissRequest = onDismiss,
        offset = dropDownOffset
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 4.dp,
            modifier = modifier
                .heightIn(max = maxDropDownHeight)
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .animateContentSize()
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = items,
                    key = {
                        it.item
                    }
                ) { selectableItem ->
                    Row(
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                color = if (selectableItem.selected) {
                                    MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.05f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                            .clickable {
                                onItemClick(selectableItem)
                            }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        leadingIcon?.invoke(selectableItem)

                        Text(
                            text = itemDisplayText(selectableItem.item),
                            modifier = Modifier.weight(1f)
                        )

                        if (selectableItem.selected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (dropDownExtras != null && dropDownExtras.text.isNotEmpty()) {
                    item(key = true) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    dropDownExtras.onClick()
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(18.dp)
                            )
                            Text(
                                text = stringResource(
                                    R.string.create_entry,
                                    dropDownExtras.text
                                ),
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SelectableDropDownOptionsMenuPreview_() {
    RecordsTheme {
        SelectableDropDownOptionsMenu(
            items = listOf(
                SelectableItem(
                    "Hello world 1",
                    selected = false
                ),
                SelectableItem(
                    "Hello world 2",
                    selected = true
                ),
                SelectableItem(
                    "Hello world 3",
                    selected = true
                )
            ),
            itemDisplayText = { it },
            onDismiss = {},
            onItemClick = {},
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.hashtag),
                    contentDescription = null
                )
            },
            maxDropDownHeight = 500.dp,
            dropDownExtras = SelectableOptionExtras(
                text = "Add a new hashtag",
                onClick = {}
            )
        )
    }
}
