package wasteless.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wasteless.controller.jsonobjects.MarketplaceCardJson;
import wasteless.exception.ForbiddenException;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.security.AuthUtil;
import wasteless.service.MarketplaceService;

import javax.validation.Valid;
import java.util.Optional;

/** This class allows for the creating, reading, updating and deleting of market place cards */
@RestController
public class MarketplaceController {

  private final MarketplaceService marketplaceService;

  private final AuthUtil authUtil;

  /**
   * Autowired MarketplaceController constructor method to initialize userRepository and
   * searchParamsParser.
   *
   * @param marketplaceService Instance of UserService/.
   */
  @Autowired
  public MarketplaceController(MarketplaceService marketplaceService, AuthUtil authUtil) {
    this.marketplaceService = marketplaceService;
    this.authUtil = authUtil;
  }

  /**
   * Get all marketplace cards for a given section, section page and the number of items to display in the page.
   * @param section String defining marketplace section to retrieve from (For Sale, Wanted, Exchange)
   * @param page Page number of section starts from 0. Optional, default value is 0.
   * @param size Number of cards to display in each section. Optional, default value is 1.
   * @return List of marketplace cards.
   */
  @GetMapping(path = "/cards")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> getCards(@RequestParam String section,
                           @RequestParam(required = false) Optional<Integer> page,
                           @RequestParam(required = false) Optional<Integer> size,
                           @RequestParam(required = false) Optional<String> sort,
                           @RequestParam(required = false) Optional<String> order) {
    int pageNum = page.orElse(0);
    int pageSize = size.orElse(10);
    String sortBy = sort.orElse("created");
    String sortOrder = order.orElse("desc");
    Page<MarketplaceCard> cards = marketplaceService.retrieveCards(section, pageNum, pageSize, sortBy, sortOrder);
    HttpHeaders responseHeader = new HttpHeaders();
    responseHeader.add("Total-Length", String.valueOf(cards.getTotalElements()));
    return ResponseEntity.ok().headers(responseHeader).body(cards.getContent());
  }

  /**
   * Throws a 403 (forbidden) exception if the creatorId does not match who the current user is (unless the current
   * user is a global admin).
   * @param creatorId the ID of the creator of the card - this will be compared to the currently logged-in user.
   */
  public void authoriseCardCrud(long creatorId) {
    User currentUser = authUtil.getCurrentUser();
    boolean currentUserIsCreator = currentUser.getUserId() == creatorId;
    boolean currentUserIsAdmin = currentUser.getRole().equals("global_admin")
            || currentUser.getRole().equals("default_global_admin");
    if (!currentUserIsCreator && !currentUserIsAdmin) {
      throw new ForbiddenException("You cannot create a card for someone else unless you are a GAA");
    }
  }

  /**
   * Endpoint to delete a card
   * Will respond with 403 if current user is not the card creator or an admin
   * Will responds with 400 if any of the fields are invalid
   * Otherwise will return 201 if the card is successfully created
   */
  @PostMapping(path = "/cards")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Object> createdCard(@Valid @RequestBody MarketplaceCardJson requestBody) {
    authoriseCardCrud(requestBody.getCreatorId());
    MarketplaceCard savedCard = marketplaceService.createMarketplaceCard(requestBody);
    return new ResponseEntity<>("{ \"cardId\": " + savedCard.getMarketplaceCardId() + " }", HttpStatus.CREATED);
  }

  /**
   * Endpoint to delete a card
   * Will respond with 403 if user does not own the card and is not an admin
   * Will respond with 406 if the card is not found.
   * Otherwise will return 200 if the card is successfully deleted
   * @param cardId the ID of the card to be deleted
   */
  @DeleteMapping(path = "/cards/{cardId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> deleteCard(@Valid @PathVariable int cardId) {
    MarketplaceCard card = marketplaceService.retrieveCardById(cardId);

    long creatorId = card.getCreator().getUserId();
    authoriseCardCrud(creatorId);
    marketplaceService.deleteCard(card);

    return new ResponseEntity<>("Card successfully deleted", HttpStatus.OK);
  }

  /**
   * Controller to extend the display period for a marketplace card
   * Will respond with 403 if user does not own the card and is not an admin
   * Will respond with 406 if the card is not found.
   * @param id id of the card to have expiry extended for
   * @return response entity of Ok status (200)
   */
  @PutMapping(path = "/cards/{id}/extenddisplayperiod")
  public ResponseEntity<Object> extendDisplayPeriod(@PathVariable int id) {
    marketplaceService.extendDisplayPeriodEnd(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
