package com.jw.home.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trait {
    private String type;
    private Map<String, Object> state;

    public void updateState(Map<String, Object> newState) {
        if (newState == null) {
            return;
        }
        state.putAll(newState);
    }
}
