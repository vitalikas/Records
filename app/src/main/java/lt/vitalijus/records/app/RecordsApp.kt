package lt.vitalijus.records.app

import android.app.Application
import lt.vitalijus.records.BuildConfig
import lt.vitalijus.records.core.database.di.databaseModule
import lt.vitalijus.records.record.di.recordsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class RecordsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@RecordsApp)
            modules(
                recordsModule,
                databaseModule
            )
        }
    }
}
