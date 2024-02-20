package com.swiftwheelshub.requestvalidator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SwaggerFolder {

    private String id;
    private String swaggerContent;

}
