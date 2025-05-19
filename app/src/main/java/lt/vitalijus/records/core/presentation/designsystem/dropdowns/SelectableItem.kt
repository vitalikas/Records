package lt.vitalijus.records.core.presentation.designsystem.dropdowns

data class SelectableItem<T>(
    val item: T,
    val selected: Boolean,
    val displayText: String? = null
) {
    companion object {
        fun <T> List<T>.asUnselectedItems(): List<SelectableItem<T>> {
            return map {
                SelectableItem(
                    item = it,
                    selected = false
                )
            }
        }
    }
}
