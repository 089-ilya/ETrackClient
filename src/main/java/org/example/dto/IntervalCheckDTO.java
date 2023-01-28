package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.App;

import java.time.Instant;
import java.time.ZonedDateTime;


@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class IntervalCheckDTO {

    private String zoneId = App.PROPERTIES.getZoneId();

    private Boolean isOn;

    private Instant time = ZonedDateTime.now().toInstant();

    public IntervalCheckDTO(Boolean isOn) {
        this.isOn = isOn;
    }
}
