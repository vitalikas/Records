package lt.vitalijus.records.record.presentation.record 

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class RecordViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(RecordState())
    val state = _state
        .onStart {
            if(!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RecordState()
        )
        
        fun onAction(action: RecordAction) {
            when(action) {
                RecordAction.OnFabClick -> {

                }
                RecordAction.OnFabLongClick -> {

                }
                RecordAction.OnMoodChipClick -> {

                }
                is RecordAction.OnRemoveFilters -> {

                }
                RecordAction.OnTopicChipClick -> {

                }

                RecordAction.OnSettingsClick -> {

                }
            }
        }
}
