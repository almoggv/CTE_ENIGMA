package jsonadapterTest;

import com.google.gson.Gson;
import component.IOWheel;
import component.impl.IOWheelImpl;
import jsonadapter.IOWheelJsonAdapter;
import org.junit.Assert;
import org.junit.Test;

public class IOWheelJsonAdapterTest {


    @Test
    public void testSerialization(){
        IOWheel wheel = new IOWheelImpl("ABC");
        Gson gson = IOWheelJsonAdapter.buildGsonAdapter();
        String serialized = gson.toJson(wheel);
        IOWheel newWheel = gson.fromJson(serialized,IOWheel.class);

        Assert.assertTrue(newWheel.getABC()!=null);
    }
}
