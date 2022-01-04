

package com.example.mint;


        import static org.junit.Assert.assertTrue;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.util.Log;

        import androidx.test.core.app.ApplicationProvider;
        import androidx.test.platform.app.InstrumentationRegistry;

        import com.example.mint.controller.ProfileActivity;
        import com.example.mint.model.PreferencesTransport;

        import org.junit.After;
        import org.junit.Before;
        import org.junit.Test;

        import java.util.ArrayList;

public class IntrumentedTestPreferencesTransport {
    Context context =   ApplicationProvider.getApplicationContext();
    @Before
    public void setup(){
        PreferencesTransport.clearTransportation(context);
        PreferencesTransport.addTransportation("Transportation",1,"voiture",context);
    }

    @Test
    public void testGetTransportation(){

        assertTrue( PreferencesTransport.getPrefTransportation("Transportation", context).get(1)=="voiture");


    }
    @After
    public void setDown(){
        PreferencesTransport.clearTransportation(context);

    }

}
