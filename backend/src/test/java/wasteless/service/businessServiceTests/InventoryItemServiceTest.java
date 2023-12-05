package wasteless.service.businessServiceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import wasteless.exception.ForbiddenException;
import wasteless.exception.InsufficientInventoryException;
import wasteless.model.Business;
import wasteless.model.InventoryItem;
import wasteless.model.User;
import wasteless.repository.BusinessRepository;
import wasteless.repository.InventoryItemRepository;
import wasteless.security.AuthUtil;
import wasteless.service.BusinessService;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class InventoryItemServiceTest {

  @MockBean private AuthUtil authUtil;

  @MockBean private BusinessRepository businessRepository;

  @MockBean private InventoryItemRepository inventoryItemRepository;

  @Autowired private BusinessService businessService;

  private Business business1;
  private Business business2;

  private InventoryItem item1;
  private InventoryItem item2;
  private InventoryItem item3;

  private User user;
  private User admin;
  private User defaultAdmin;

  @BeforeEach
  void setup() {
    item1 = new InventoryItem();
    item1.setQuantity(10);
    item2 = new InventoryItem();
    item3 = new InventoryItem();

    business1 = new Business();
    business1.setBusinessId(1L);
    business1.setAdmins(new ArrayList<>());
    business1.setInventoryItems(Collections.singletonList(item1));

    business2 = new Business();
    business2.setBusinessId(2L);
    business2.setAdmins(new ArrayList<>());
    business2.setInventoryItems(Arrays.asList(item2, item3));

    user = new User();
    user.setUserId(1L);
    user.setRole("user");
    user.setBusinessesAdministered(new ArrayList<>());

    admin = new User();
    admin.setUserId(2L);
    admin.setRole("global_admin");

    defaultAdmin = new User();
    defaultAdmin.setUserId(3L);
    defaultAdmin.setRole("default_global_admin");


    Mockito.when(businessRepository.findByBusinessId(1L)).thenReturn(java.util.Optional.of(business1));
  }

  /*
   * Invalid auth token cases throws an unauthorized. Verify findById is not called.
   * */
  @Test
  void retrieve_inventory_of_business_1_invalid_auth_token_throws_correct_exception() {
    Mockito.when(authUtil.getCurrentUser())
        .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    Assertions.assertThrows(
        ResponseStatusException.class, () -> businessService.retrieveInventory(1L));
  }

  @Test
  void retrieve_inventory_of_business_1_invalid_auth_token_no_call_to_business_repository() {
    Mockito.when(authUtil.getCurrentUser())
        .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    Mockito.verify(businessRepository, Mockito.times(0)).findByBusinessId(business1.getBusinessId());
  }

  /*
  No business is found in database will throw NoSuchElementException
   */
  @Test
  void retrieve_inventory_of_business_1_business_not_found_throws_correct_exception() {
    Mockito.when(businessRepository.findByBusinessId(2L)).thenThrow(new NoSuchElementException(""));
    Assertions.assertThrows(
        NoSuchElementException.class, () -> businessService.retrieveInventory(2L));
  }

  /*
  User is not authorized throws ForbiddenException
   */
  @Test
  void retrieve_inventory_of_business_1_user_not_admin_throws_correct_exception() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(user);
    Assertions.assertThrows(ForbiddenException.class, () -> businessService.retrieveInventory(1L));
  }

  /*
  If logged in user is authorized, the method returns a valid list of InventoryItems
   */
  @Test
  void retrieve_inventory_of_business_1_user_is_business_admin_returns_not_null() {
    user.addBusinessAdministered(business1);
    business1.addAdministrator(user);
    Mockito.when(authUtil.getCurrentUser()).thenReturn(user);

    List<InventoryItem> inventoryItems = businessService.retrieveInventory(1L);
    Assertions.assertNotNull(inventoryItems);
  }

  @Test
  void retrieve_inventory_of_business_1_user_is_global_admin_returns_not_null() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(admin);

    List<InventoryItem> inventoryItems = businessService.retrieveInventory(1L);
    Assertions.assertNotNull(inventoryItems);
  }

  @Test
  void retrieve_inventory_of_business_1_user_is_default_global_admin_returns_not_null() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);

    List<InventoryItem> inventoryItems = businessService.retrieveInventory(1L);
    Assertions.assertNotNull(inventoryItems);
  }

  /*
  If user is authorized, different businesses return correct corresponding InventoryItems
   */
  @Test
  void retrieve_inventory_of_business_1_user_is_authorized_returns_correct_inventory() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);

    List<InventoryItem> inventoryItems = businessService.retrieveInventory(1L);
    Assertions.assertTrue(inventoryItems.contains(item1));
  }

  @Test
  void retrieve_inventory_of_business_2_user_is_authorized_returns_correct_inventory() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);
    Mockito.when(businessRepository.findByBusinessId(2L)).thenReturn(Optional.ofNullable(business2));

    List<InventoryItem> inventoryItems = businessService.retrieveInventory(2L);
    List<InventoryItem> expectedItems = Arrays.asList(item2, item3);
    Assertions.assertEquals(inventoryItems, expectedItems);
  }

  @Test
  void decrementInventoryItemQuantity_inventoryItemDoesNotExist_throwsNoSuchElementException() {
    Assertions.assertThrows(
            NoSuchElementException.class,
            () -> businessService.decrementInventoryItemQuantity(100L, 10));

  }

  @Test
  void decrementInventoryItemQuantity_inventoryItemDoesNotExist_returnsCorrectErrorMessage() {
    Exception exception = Assertions.assertThrows(
            NoSuchElementException.class,
            () -> businessService.decrementInventoryItemQuantity(100L, 1));
    Assertions.assertEquals("No value present", exception.getMessage());

  }

  @Test
  void decrementInventoryItemQuantity_lessThanTotalQuantityRemoved_quantityIsUpdatedCorrectly() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);
    when(inventoryItemRepository.findByInventoryItemId(any(long.class))).thenReturn(Optional.of(item1));
    businessService.decrementInventoryItemQuantity(7L, 7);
    Assertions.assertEquals(3, item1.getQuantity());
  }

  @Test
  void decrementInventoryItemQuantity_moreThanTotalQuantityRemoved_throwsBadRequestException() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);
    when(inventoryItemRepository.findByInventoryItemId(any(long.class))).thenReturn(Optional.of(item1));
    Assertions.assertThrows(
            InsufficientInventoryException.class,
            () -> businessService.decrementInventoryItemQuantity(7L, 11));
  }

  @Test
  void decrementInventoryItemQuantity_moreThanTotalQuantityRemoved_ReturnsCorrectErrorMessage() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);
    when(inventoryItemRepository.findByInventoryItemId(any(long.class))).thenReturn(Optional.of(item1));
    Exception exception = Assertions.assertThrows(
            InsufficientInventoryException.class,
            () -> businessService.decrementInventoryItemQuantity(7L, 11));
    Assertions.assertEquals("Cannot decrement inventory below zero.", exception.getMessage());

  }

  @Test
  void decrementInventoryItemQuantity_zeroQuantityRemoved_quantityRemainingIsCorrect() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);
    when(inventoryItemRepository.findByInventoryItemId(any(long.class))).thenReturn(Optional.of(item1));
    businessService.decrementInventoryItemQuantity(7L, 0);

    Assertions.assertEquals(10, item1.getQuantity());
  }

}
