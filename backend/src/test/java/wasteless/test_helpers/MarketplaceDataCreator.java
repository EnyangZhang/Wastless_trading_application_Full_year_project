package wasteless.test_helpers;

import wasteless.controller.jsonobjects.MarketplaceCardJson;
import wasteless.model.Address;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * This class contains methods to create test data for various model classes. Essentially the methods are constructors,
 * but only the 'relevant' attributes are inputs (e.g. email and userId) and the rest are set to defaults.
 */
public class MarketplaceDataCreator {

    public static MarketplaceCardJson createCardJson(long creatorId) {
        int[] keywordIds = {};
        return new MarketplaceCardJson(
                creatorId,
                "For Sale",
                "title",
                "description",
                keywordIds
        );
    }

    public static MarketplaceCard createCard(int cardId, User creator) {
        MarketplaceCard marketplaceCard = new MarketplaceCard(
                creator,
                "For Sale",
                "title",
                "description",
                Collections.emptyList()
        );
        marketplaceCard.setCreated(LocalDateTime.now());
        marketplaceCard.setDisplayPeriodEnd(LocalDateTime.now().plusWeeks(3));
        marketplaceCard.setMarketplaceCardId(cardId);
        marketplaceCard.setNotifiedExpiring(false);
        return marketplaceCard;
    }

    public static User createUser(long userId, String email, String role) {
        return new User(
                userId,
                "Test",
                "Test",
                "",
                "a",
                "a",
                email,
                LocalDate.parse("1997-02-01"),
                "",
                new Address("1", "Madeup Road", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"),
                "password123",
                LocalDate.now(),
                role,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new HashSet<>());
    }
}
