package lt.vitalijus.records.core.presentation.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import lt.vitalijus.records.core.presentation.util.UiText.Dynamic
import lt.vitalijus.records.core.presentation.util.UiText.StringResource

@Stable
sealed interface UiText {

    @Stable
    data class Dynamic(
        val value: String
    ) : UiText

    @Stable
    data class StringResource(
        @StringRes val id: Int,
        val args: List<Any> = emptyList()
    ) : UiText
}

@Composable
fun UiText.asString(): String = when (this) {
    is Dynamic -> value
    is StringResource -> stringResource(id, *args.toTypedArray())
}
