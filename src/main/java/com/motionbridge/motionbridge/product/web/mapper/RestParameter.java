package com.motionbridge.motionbridge.product.web.mapper;

import com.motionbridge.motionbridge.product.entity.Parameter;
import lombok.Value;

@Value
public class RestParameter {
    Long id;
    String content;
    String image;
    String subtitle;
    String title;

    public static RestParameter toRestParameter(Parameter parameter) {
        return new RestParameter(
                parameter.getId(),
                parameter.getContent(),
                parameter.getImage(),
                parameter.getSubtitle(),
                parameter.getTitle()
        );
    }
}
