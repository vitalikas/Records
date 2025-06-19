package lt.vitalijus.records.record.di

import lt.vitalijus.records.record.data.audio.AndroidAudioPlayer
import lt.vitalijus.records.record.data.record.RoomRecordDataSource
import lt.vitalijus.records.record.data.recording.AndroidVoiceRecorder
import lt.vitalijus.records.record.data.recording.InternalRecordingStorage
import lt.vitalijus.records.record.data.settings.DataStoreSettings
import lt.vitalijus.records.record.domain.audio.AudioPlayer
import lt.vitalijus.records.record.domain.record.RecordDataSource
import lt.vitalijus.records.record.domain.recording.RecordingStorage
import lt.vitalijus.records.record.domain.recording.VoiceRecorder
import lt.vitalijus.records.record.domain.settings.SettingsPreferences
import lt.vitalijus.records.record.presentation.create_record.CreateRecordViewModel
import lt.vitalijus.records.record.presentation.records.RecordsViewModel
import lt.vitalijus.records.record.presentation.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
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
//  or singleOf(::AndroidVoiceRecorder) bind VoiceRecorder::class

    singleOf(::InternalRecordingStorage) bind RecordingStorage::class
    singleOf(::AndroidAudioPlayer) bind AudioPlayer::class
    singleOf(::RoomRecordDataSource) bind RecordDataSource::class
    singleOf(::DataStoreSettings) bind SettingsPreferences::class

    viewModelOf(::RecordsViewModel)
    viewModelOf(::CreateRecordViewModel)
    viewModelOf(::SettingsViewModel)
}
