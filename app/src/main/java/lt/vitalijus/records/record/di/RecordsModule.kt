package lt.vitalijus.records.record.di

import kotlinx.coroutines.CoroutineScope
import lt.vitalijus.records.record.data.audio.AndroidAudioPlayer
import lt.vitalijus.records.record.data.record.RoomRecordDataSource
import lt.vitalijus.records.record.data.recording.AndroidVoiceRecorder
import lt.vitalijus.records.record.data.recording.InternalRecordingStorage
import lt.vitalijus.records.record.data.settings.DataStoreSettings
import lt.vitalijus.records.record.domain.audio.AudioPlayer
import lt.vitalijus.records.record.domain.audio.AudioPlayerFactory
import lt.vitalijus.records.record.domain.record.RecordDataSource
import lt.vitalijus.records.record.domain.recording.RecordingStorage
import lt.vitalijus.records.record.domain.recording.VoiceRecorder
import lt.vitalijus.records.record.domain.recording.VoiceRecorderFactory
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
    factory<VoiceRecorderFactory> {
        object : VoiceRecorderFactory {
            override fun create(scope: CoroutineScope): VoiceRecorder {
                return AndroidVoiceRecorder(
                    context = androidContext(),
                    coroutineScope = scope
                )
            }
        }
    }
    factory<AudioPlayerFactory> {
        object : AudioPlayerFactory {
            override fun create(scope: CoroutineScope): AudioPlayer {
                return AndroidAudioPlayer(
                    coroutineScope = scope
                )
            }
        }
    }

    singleOf(::InternalRecordingStorage) bind RecordingStorage::class
    singleOf(::AndroidAudioPlayer) bind AudioPlayer::class
    singleOf(::RoomRecordDataSource) bind RecordDataSource::class
    singleOf(::DataStoreSettings) bind SettingsPreferences::class

    viewModelOf(::CreateRecordViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::RecordsViewModel)
}
