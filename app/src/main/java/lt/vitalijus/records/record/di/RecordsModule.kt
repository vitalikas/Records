package lt.vitalijus.records.record.di

import lt.vitalijus.records.record.data.recording.AndroidVoiceRecorder
import lt.vitalijus.records.record.domain.recording.VoiceRecorder
import lt.vitalijus.records.record.presentation.create_record.CreateRecordViewModel
import lt.vitalijus.records.record.presentation.records.RecordViewModel
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
    } bind VoiceRecorder::class

    viewModelOf(::RecordViewModel)

    viewModelOf(::CreateRecordViewModel)
}
