package haroldolivieri.currencyexchanger

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import haroldolivieri.currencyexchanger.di.ApplicationComponent
import haroldolivieri.currencyexchanger.di.DaggerApplicationComponent
import uk.co.chrisjenx.calligraphy.CalligraphyConfig


class CurrencyExchangerApp : DaggerApplication() {
    companion object {
        lateinit var applicationComponent: ApplicationComponent
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        applicationComponent = DaggerApplicationComponent.builder()
                .application(this)
                .build()

        applicationComponent.inject(this)
        return applicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        calligraphyConfig()
    }

    private fun calligraphyConfig() {
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.font_montserrat_regular))
                .setFontAttrId(R.attr.fontPath)
                .build())
    }
}