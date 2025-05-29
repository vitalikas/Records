package lt.vitalijus.records.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import lt.vitalijus.records.BuildConfig
import lt.vitalijus.records.app.di.appModule
import lt.vitalijus.records.record.di.recordsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class RecordsApp : Application() {

    val applicationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@RecordsApp)
            modules(
                appModule,
                recordsModule
            )
        }
    }
}
