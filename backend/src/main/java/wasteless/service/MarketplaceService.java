package wasteless.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import wasteless.controller.jsonobjects.MarketplaceCardJson;
import wasteless.exception.BadRequestException;
import wasteless.exception.ForbiddenException;
import wasteless.model.MarketplaceCard;
import wasteless.model.MarketplaceKeyword;
import wasteless.model.User;
import wasteless.repository.MarketplaceCardRepository;
import wasteless.repository.MarketplaceKeywordRepository;
import wasteless.repository.UserRepository;
import wasteless.security.AuthUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class MarketplaceService {

  private final MarketplaceCardRepository marketplaceCardRepository;
  private final MarketplaceKeywordRepository marketplaceKeywordRepository;
  private final UserRepository userRepository;
  private final AuthUtil authUtil;
  private final NotificationService notificationService;
  
  @Autowired
  public MarketplaceService(
          MarketplaceCardRepository marketplaceCardRepository,
          MarketplaceKeywordRepository marketplaceKeywordRepository,
          UserRepository userRepository, 
          AuthUtil authUtil, 
          NotificationService notificationService) {
    this.marketplaceCardRepository = marketplaceCardRepository;
    this.marketplaceKeywordRepository = marketplaceKeywordRepository;
    this.userRepository = userRepository;
    this.authUtil = authUtil;
    this.notificationService = notificationService;
  }

  /**
   * Creates marketplace card from MarketplaceCardJson request body. With throw exception if request
   * is invalid.
   *
   * @param cardJson Request received to create card with.
   * @return MarketplaceCard if created.
   */
  public MarketplaceCard createMarketplaceCard(MarketplaceCardJson cardJson) {

    //todo this is for current product version it create example keywords whenever
    // user choose keywords from card dialog. Delete this block of code when we come to actual story of keywords.
    for (int keywordId : cardJson.getKeywordIds()) {
      MarketplaceKeyword keyword =
              marketplaceKeywordRepository.findByMarketplaceKeywordId(keywordId).orElse(null);
      if (keyword == null){
        MarketplaceKeyword exampleKeyword = new MarketplaceKeyword();
        if (keywordId == 1) {
          exampleKeyword.setName("Selling");
          exampleKeyword.setMarketplaceKeywordId(keywordId);
          exampleKeyword.setCreated(LocalDateTime.now());
        } else if (keywordId == 2) {
          exampleKeyword.setName("Swapping");
          exampleKeyword.setMarketplaceKeywordId(keywordId);
          exampleKeyword.setCreated(LocalDateTime.now());
        } else if (keywordId == 3) {
          exampleKeyword.setName("Food");
          exampleKeyword.setMarketplaceKeywordId(keywordId);
          exampleKeyword.setCreated(LocalDateTime.now());
        }
        marketplaceKeywordRepository.save(exampleKeyword);
      }
    }

    validateMarketplaceCard(cardJson);
    MarketplaceCard card = convertJsonToMarketplaceCard(cardJson);
    card.setNotifiedExpiring(false);
    return marketplaceCardRepository.save(card);
  }


  /**
   * Converts request body POJO to a MarketplaceCard object, and sets date created to now, and display period end to 3
   * weeks from now.
   * Preconditions: the card POJO contains all valid data
   * @param cardJson POJO request body
   * @return a MarketplaceCard object
   */
  public MarketplaceCard convertJsonToMarketplaceCard(MarketplaceCardJson cardJson) {
    User creator = userRepository.findById(cardJson.getCreatorId()).orElse(null);
    MarketplaceKeyword keyword;
    List<MarketplaceKeyword> keywordList = new java.util.ArrayList<>(Collections.emptyList());
    for (int keywordId : cardJson.getKeywordIds()) {
        keyword = marketplaceKeywordRepository.findByMarketplaceKeywordId(keywordId).orElse(null);
        keywordList.add(keyword);
    }
    MarketplaceCard card = new MarketplaceCard(
            creator,
            cardJson.getSection(),
            cardJson.getTitle(),
            cardJson.getDescription(),
            keywordList);
    card.setCreated(LocalDateTime.now());
    long maxDisplayWeeks = 3;
    card.setDisplayPeriodEnd(LocalDateTime.now().plusWeeks(maxDisplayWeeks));
    return card;
  }

  /**
   * Validates user is of correct form.
   *
   * @param cardJson The JSON body to validate.
   * @throws ResponseStatusException Thrown in case that user is invalid.
   */
  public void validateMarketplaceCard(MarketplaceCardJson cardJson) throws BadRequestException {
    if (isInvalidCreatorId(cardJson.getCreatorId())) {
      throw new BadRequestException("No user exists with the given creator ID");
    }
    if (isInvalidSection(cardJson.getSection())) {
      throw new BadRequestException("Section must be one of: 'For Sale', 'Wanted', 'Exchange'");
    }
    if (titleIsTooLong(cardJson.getTitle())) {
      throw new BadRequestException(
          "Title must be less than 255 (TBC) characters");
    }
    if (descriptionIsTooLong(cardJson.getDescription())) {
      throw new BadRequestException("Description must be less than 255 characters");
    }
    if (isInvalidKeywordList(cardJson.getKeywordIds())) {
      throw new BadRequestException("One or more keyword IDs do not exist");
    }
  }

  public boolean isInvalidCreatorId(Long creatorId) {
    User creator = userRepository.findById(creatorId).orElse(null);
    return creator == null;
  }

  public boolean isInvalidSection(String section) {
    String[] validSections = {"For Sale", "Wanted", "Exchange"};
    return !Arrays.asList(validSections).contains(section);
  }

  public boolean titleIsTooLong(String title) {
    int maxTitleLength = 255;
    return title.length() > maxTitleLength;
  }

  public boolean descriptionIsTooLong(String description) {
    int maxDescriptionLength = 255;
    return description.length() > maxDescriptionLength;
  }

  public boolean isInvalidKeywordList(int[] keywordIds) {
    for (int id : keywordIds) {
      MarketplaceKeyword keyword =
          marketplaceKeywordRepository.findByMarketplaceKeywordId(id).orElse(null);
      if (keyword == null) {
        return true;
      }
    }
    return false;
  }

  /**
   * Retrieves the appropriate marketplace cards from the database using the repository interface.
   * For example, if section is 'For Sale' with page 1 and size 2, the cards from index 1 and 2 are
   * skipped (as being cards from page 1) and cards from index 3 and 4 are returned.
   *
   * @param section Specifies the section the cards must match to be selected from the database. If
   *     section is invalid, then an empty list is returned.
   * @param page Specifies the starting 'page' of data, which defines from which index the cards are
   *     retrieved.
   * @param size Specifies the number of cards to be retrieved per page.
   * @return List of retrieved marketplace cards.
   */
  public Page<MarketplaceCard> retrieveCards(
      String section, int page, int size, String sortBy, String sortOrder) {
    Pageable pageable;
    validatePage(page);
    validateSize(size);
    validateSorting(sortBy);
    validateOrder(sortOrder);

    try {
      if (sortOrder.equals("asc")) {
        pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
      }
      return marketplaceCardRepository.findMarketplaceCardsBySection(section, pageable);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  /**
   * Delete the marketplace card provided by input.
   * If the card is expired, calls notificationService to look for notifications about the card's expiry before
   * deletion, and deletes any that are found.
   * @param card The market card to be deleted.
   */
  public void deleteCard(MarketplaceCard card) {
    if (card.isExpired()) {
      notificationService.readExpiryWarningNotifications(card.getCreator(), card.getMarketplaceCardId());
    }
    marketplaceCardRepository.delete(card);
  }

  /**
   * Retrieve card by its ID, will throw 406 Unacceptable error if the card does not exist.
   * @param cardId The marketplace card id for the card that is to be deleted.
   * @return return Marketplace card object to be deleted.
   */
  public MarketplaceCard retrieveCardById(int cardId) {
    return marketplaceCardRepository.findMarketplaceCardByMarketplaceCardId(cardId).orElseThrow();
  }


  /**
   * Extends the display period of a marketplace card by two weeks.
   * Takes the id of the marketplace card to be extended.
   * If the card doesn't exist throws a 406.
   * If the user does does not own the card or is not an admin throws a 403.
   * If the user is not logged in throws a 401.
   * @param id the id of the card to be extended.
   */
  public void extendDisplayPeriodEnd(int id) {
    User currentUser = authUtil.getCurrentUser();
    MarketplaceCard cardToUpdate = marketplaceCardRepository.findMarketplaceCardByMarketplaceCardId(id).orElse(null);

    if (cardToUpdate != null) {
      if (cardToUpdate.getCreator().getId() == currentUser.getId() || currentUser.getRole().equals("global_admin")
              || currentUser.getRole().equals("default_global_admin")) {
        // Get the current expiry date of the card
        LocalDateTime currentExpiry = cardToUpdate.getDisplayPeriodEnd();
        // Add 2 weeks to the date
        LocalDateTime extendedExpiry = currentExpiry.plusWeeks(2);
        cardToUpdate.setDisplayPeriodEnd(extendedExpiry);
        // update notification status of card
        resetNotifiedExpiring(cardToUpdate);
        // Call Repo to set new date.
        marketplaceCardRepository.extendDisplayPeriodForCard(id, extendedExpiry);
      } else {
        throw new ForbiddenException("Cannot extend the display period for a card you did not create.");
      }
    } else {
      throw new NotAcceptableStatusException("The card you are trying to extend the display period for does not exist.");
    }
  }


  /**
   * Helper method to reset the notification status of a card.
   * This method is to be called from another method.
   * If the cards notification status is true throws a bad request otherwise resets it to false.
   * @param card the marketplace card that the notification status is to be reset for.
   */
  public void resetNotifiedExpiring(MarketplaceCard card) {
    Boolean notified = card.getNotifiedExpiring();
    if (Boolean.TRUE.equals(notified)) {
      card.setNotifiedExpiring(false);
      marketplaceCardRepository.save(card);
    } else {
      throw new BadRequestException("The card has not been notified to the user as expiring");
    }
  }

  /**
   * Validation function for page number. Throws bad request exception if page number is less than
   * 0.
   *
   * @param page Page number.
   */
  private void validatePage(int page) {
    if (page < 0) {
      throw new BadRequestException("Invalid page number: page number less than 0");
    }
  }

  /**
   * Validation function for page size. Throws bad request exception if page size is less than 1.
   *
   * @param size Page size.
   */
  private void validateSize(int size) {
    if (size < 1) {
      throw new BadRequestException("Invalid page size: page size must be at least 1");
    }
  }

  /**
   * Validation function for sorting field. Throws bad request exception if sorting field is not the
   * expected.
   *
   * <p>(could possibly be extracted as an interface to let different services validate for
   * different model classes)
   *
   * @param sorting Sorting field.
   */
  private void validateSorting(String sorting) {
    if (sorting == null
        || !(sorting.equals("created")
        || sorting.equals("title")
        || sorting.equals("creator.homeAddress.suburb")
        || sorting.equals("creator.homeAddress.city"))) {
      throw new BadRequestException("Unrecognized sorting field");
    }
  }

  /**
   * Validation function for ordering field. Throws bad request exception if ordering field is not
   * 'asc or 'desc'
   *
   * @param order Ordering field.
   */
  private void validateOrder(String order) {
    if (!(order.equals("asc") || order.equals("desc"))) {
      throw new BadRequestException("Unrecognized ordering field");
    }
  }
}
