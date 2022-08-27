package com.motionbridge.motionbridge.product.web.mapper;

import com.motionbridge.motionbridge.product.entity.Presentation;
import lombok.Value;

@Value
public class RestPresentation {
    Long id;
    String content;
    String preview;
    String title;

    public static RestPresentation toRestPresentation(Presentation presentation) {
        return new RestPresentation(
                presentation.getId(),
                presentation.getContent(),
                presentation.getPreview(),
                presentation.getTitle()
        );
    }
}
