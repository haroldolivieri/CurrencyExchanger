package haroldolivieri.currencyexchanger.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import haroldolivieri.currencyexchanger.feature.CurrencyListActivity
import haroldolivieri.currencyexchanger.feature.CurrencyListModule

@Module
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector(modules = [CurrencyListModule::class])
    internal abstract fun bindMainActivity(): CurrencyListActivity
}