package haroldolivieri.currencyexchanger

import org.junit.Test


class RepositoryTest {

    @Rule
    var rule = MockitoJUnit.rule()

    @Mock
    var currencyRatingService: CurrencyRatingService = null

    @Mock
    var localStore: SharedPreferenceLocalStore = null

    @InjectMocks
    var userService: UserService? = null

    @Test
    fun test() {

    }
}