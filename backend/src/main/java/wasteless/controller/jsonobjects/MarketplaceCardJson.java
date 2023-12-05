package wasteless.controller.jsonobjects;

import lombok.Data;
import lombok.NonNull;

@Data
public class MarketplaceCardJson {

    @NonNull
    private Long creatorId;

    @NonNull
    private String section;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private int[] keywordIds;
}
