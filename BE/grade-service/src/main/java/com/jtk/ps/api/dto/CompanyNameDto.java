package com.jtk.ps.api.dto;

// import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyNameDto {
    private Integer id;

    // @JsonProperty("name")
    private String name;

}
