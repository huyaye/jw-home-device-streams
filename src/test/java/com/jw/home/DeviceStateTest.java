package com.jw.home;

import com.jw.home.dto.DeviceState;
import com.jw.home.dto.Trait;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeviceStateTest {
    @Test
    void updateDeviceState() {
        List<Trait> traits = new ArrayList<>();
        traits.add(Trait.builder().type("OnOff").state(new HashMap<>() {{
            put("on", true);
        }}).build());
        traits.add(Trait.builder().type("Brightness").state(new HashMap<>() {{
            put("brightness", 11);
        }}).build());

        DeviceState state = DeviceState.builder().online(true).traits(traits).build();
        state.updateTrait(Trait.builder().type("OnOff").state(new HashMap<>() {{
            put("on", false);
        }}).build());

        assertEquals(state.getOnline(), true);
        assertEquals(state.getTraits().size(), 2);


        assertEquals(state.getTraits().get(1).getState().get("brightness"), 11);
    }
}
