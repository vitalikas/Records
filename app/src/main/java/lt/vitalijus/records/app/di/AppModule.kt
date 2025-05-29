package lt.vitalijus.records.app.di

import kotlinx.coroutines.CoroutineScope
import lt.vitalijus.records.app.RecordsApp
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single<CoroutineScope> {
        (androidApplication() as RecordsApp).applicationScope
    }
}
