package org.controllers.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetBookItemResponse {
    private String id;
    private String title;
    private String author;
    private String genre;
    private Boolean available;
}
