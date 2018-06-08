package haroldolivieri.currencyexchanger.repository.local

import android.content.Context
import dagger.Module
import dagger.Provides
import android.content.SharedPreferences



@Module()
class LocalModule {

    @Provides
    fun provideSharedPreferences(context : Context): SharedPreferences {
        return context.getSharedPreferences("LOCAL_STORAGE", Context.MODE_PRIVATE)
    }

    @Provides
    @SharedPreferenceLocalStore
    fun provideSharedPreferenceStore(sharedPreferenceStore: SharedPreferenceStore) :
            LocalStore = sharedPreferenceStore

}