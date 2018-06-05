package haroldolivieri.currencyexchanger.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import haroldolivieri.currencyexchanger.CurrencyExchangerApp
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidSupportInjectionModule::class),
    (ApplicationModule::class),
    (ActivityBuilderModule::class)])
interface ApplicationComponent : AndroidInjector<CurrencyExchangerApp> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ApplicationComponent
    }
}