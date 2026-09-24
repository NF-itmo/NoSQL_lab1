package org.controllers.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetBookResponse {
    private String id;
    private String title;
    private String author;
    private String genre;
    private String description;
    private Boolean available;
}
