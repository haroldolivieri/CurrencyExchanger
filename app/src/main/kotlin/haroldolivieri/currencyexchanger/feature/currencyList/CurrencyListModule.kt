package haroldolivieri.currencyexchanger.feature.currencyList

import dagger.Module
import dagger.Provides
import haroldolivieri.currencyexchanger.repository.CurrencyRepository
import haroldolivieri.currencyexchanger.repository.Repository
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

@Module
class CurrencyListModule {
    @Provides
    fun provideView(activity: CurrencyListActivity):
            CurrencyListContract.View = activity

    @Provides
    fun providePresenter(presenter: CurrencyListPresenter):
            CurrencyListContract.Presenter = presenter

    @Provides
    fun provideRepository(currencyRepository: CurrencyRepository) : Repository =
            currencyRepository

    @Provides
    fun provideScheduler() : Scheduler = Schedulers.io()
}