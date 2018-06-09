package haroldolivieri.currencyexchanger.feature.currencyList

import dagger.Module
import dagger.Provides

@Module
class CurrencyListModule {
    @Provides
    fun provideView(activity: CurrencyListActivity):
            CurrencyListContract.View = activity

    @Provides
    fun providePresenter(presenter: CurrencyListPresenter):
            CurrencyListContract.Presenter = presenter
}