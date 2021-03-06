package haroldolivieri.currencyexchanger.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import haroldolivieri.currencyexchanger.feature.currencyList.CurrencyListActivity
import haroldolivieri.currencyexchanger.feature.currencyList.CurrencyListModule
import haroldolivieri.currencyexchanger.repository.local.LocalModule
import haroldolivieri.currencyexchanger.repository.remote.RemoteModule

@Module
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector(modules = [CurrencyListModule::class,
        RemoteModule::class, LocalModule::class])
    internal abstract fun bindMainActivity(): CurrencyListActivity
}