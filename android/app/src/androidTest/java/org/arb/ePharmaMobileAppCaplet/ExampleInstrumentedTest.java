package org.arb.ePharmaMobileAppCaplet;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented upload_data_delete_sqlite_data_test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under upload_data_delete_sqlite_data_test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("org.arb.ePharmaMobileApp", appContext.getPackageName());
    }
}
