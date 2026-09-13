package dhn.intern.smart_ai_caculator_app.initializers

import android.app.Application
import dhn.intern.smart_ai_caculator_app.injection.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SmartCalculatorApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SmartCalculatorApp)
            modules(appModule)
        }
    }
}