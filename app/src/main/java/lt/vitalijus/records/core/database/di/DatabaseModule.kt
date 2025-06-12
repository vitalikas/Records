package lt.vitalijus.records.core.database.di

import androidx.room.Room
import lt.vitalijus.records.core.database.RecordDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single<RecordDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            RecordDatabase::class.java,
            "records.db"
        ).build()
    }

    single {
        get<RecordDatabase>().recordDao
    }
}
