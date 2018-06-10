package haroldolivieri.currencyexchanger;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented item_currency_new, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under item_currency_new.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("haroldolivieri.currencyexchanger", appContext.getPackageName());
    }
}
