package lt.vitalijus.records.record.di

import lt.vitalijus.records.record.data.recording.AndroidVoiceRecorder
import lt.vitalijus.records.record.presentation.record.RecordViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val recordsModule = module {
    single {
        AndroidVoiceRecorder(
            context = androidContext(),
            applicationScope = get()
        )
    } bind AndroidVoiceRecorder::class

    viewModelOf(::RecordViewModel)
}
