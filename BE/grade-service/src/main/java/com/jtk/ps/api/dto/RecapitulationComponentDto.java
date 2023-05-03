package com.jtk.ps.api.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecapitulationComponentDto {
    
    @JsonProperty("id_component")
    private Integer idComponent;

    @JsonProperty("name_component")
    private String nameComponent;

    private Float total_component;

    private List<RecapitulationCriteriaDto> criteria_data;
    
}
