package com.jw.home.kafka.topic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jw.home.dto.Trait;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceEvent {
    TriggerType trigger;

    Boolean online;

    List<Trait> traits;
}
