package org.controllers.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderResponse {
    private String id;
    private String bookId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime expiresAt;
}
