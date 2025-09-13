package io.abc_def.kickstart_fx.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Value;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AppBeaconMessage.FocusRequest.class),
        @JsonSubTypes.Type(value = AppBeaconMessage.OpenRequest.class)
})
public class AppBeaconMessage {

    @Value
    @JsonTypeName("focus")
    public static class FocusRequest extends AppBeaconMessage {

    }

    @Value
    @JsonTypeName("open")
    public static class OpenRequest extends AppBeaconMessage {

        List<String> arguments;

    }
}
