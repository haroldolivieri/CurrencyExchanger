package haroldolivieri.currencyexchanger.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import haroldolivieri.currencyexchanger.feature.MainActivity

@Module
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector(modules = [])
    internal abstract fun bindMainActivity(): MainActivity
}