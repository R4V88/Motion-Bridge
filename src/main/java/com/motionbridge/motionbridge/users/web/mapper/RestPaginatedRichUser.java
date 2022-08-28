package com.motionbridge.motionbridge.users.web.mapper;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RestPaginatedRichUser {
    private List<RichRestUser> users;
    private Long numberOfItems;
    private Integer numberOfPages;
}
