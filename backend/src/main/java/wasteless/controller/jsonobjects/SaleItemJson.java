package wasteless.controller.jsonobjects;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class SaleItemJson {

    @NonNull
    private Long inventoryItemId;

    @NonNull
    private Integer quantity;

    @NonNull
    private Double price;

    @NonNull
    private String moreInfo;

    @NonNull
    private LocalDateTime closes;
}