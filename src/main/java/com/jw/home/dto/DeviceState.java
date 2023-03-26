package com.jw.home.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceState {
    Boolean online;

    List<Trait> traits;

    public void updateTrait(Trait newTrait) {
        Trait targetTrait = null;
        for (Trait trait : traits) {
            if (trait.getType().equals(newTrait.getType())) {
                targetTrait = trait;
            }
        }
        if (targetTrait != null) {
            targetTrait.updateState(newTrait.getState());
        } else {
            traits.add(newTrait);
        }
    }
}
