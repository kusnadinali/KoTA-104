package com.jtk.ps.api.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecapitulationParticipantDto {
    
    private Integer idParticipant;

    private String name;

    private String nim;

    private Float total_course;

    private List<RecapitulationComponentDto> component_data;
}
