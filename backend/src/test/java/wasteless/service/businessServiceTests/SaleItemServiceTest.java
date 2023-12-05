package wasteless.service.businessServiceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import wasteless.controller.jsonobjects.SaleItemJson;
import wasteless.exception.BadRequestException;
import wasteless.model.InventoryItem;
import wasteless.model.SaleItem;
import wasteless.model.User;
import wasteless.repository.InventoryItemRepository;
import wasteless.repository.SaleItemRepository;
import wasteless.security.AuthUtil;
import wasteless.service.BusinessService;
import wasteless.service.NotificationService;
import wasteless.test_helpers.BusinessDataCreator;
import wasteless.test_helpers.UserDataCreator;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;

@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/CreateUserData.sql"})
@SpringBootTest
class SaleItemServiceTest {
  @Autowired private BusinessService businessService;

  @MockBean private AuthUtil authUtil;

  @MockBean private SaleItemRepository saleItemRepository;

  @MockBean private InventoryItemRepository inventoryItemRepository;

  @MockBean private NotificationService notificationService;

  private SaleItemJson saleItemJson;

  private User user;

  private User defaultAdmin;

  private InventoryItem inventoryItem;

  private SaleItem unsoldSaleItem;

  private SaleItem soldSaleItem;

  @BeforeEach
  void setup() {
    inventoryItem = BusinessDataCreator.createInventoryItem(1L);
    inventoryItem.setQuantity(4);

    saleItemJson = BusinessDataCreator.createSaleItemJson(inventoryItem);
    user = UserDataCreator.createUser("test@email.com", "user");

    defaultAdmin = new User();
    defaultAdmin.setUserId(3L);
    defaultAdmin.setRole("default_global_admin");

    unsoldSaleItem = BusinessDataCreator.createSaleItem(7L);
    soldSaleItem = BusinessDataCreator.createSaleItem(7L);

    unsoldSaleItem.setSold(false);
    unsoldSaleItem.setInventoryItem(inventoryItem);
    unsoldSaleItem.setSaleItemId(5L);
    soldSaleItem.setSold(true);
    soldSaleItem.setInventoryItem(inventoryItem);
    soldSaleItem.setSaleItemId(6L);

  }

  @Test
  void addSaleItem_notLogin_Unauthorized() {
    when(authUtil.getCurrentUser()).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    Assertions.assertThrows(
        ResponseStatusException.class, () -> businessService.addSaleItem(1, saleItemJson));
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void addSaleItem_loginButBusinessNotExist_NoSuchElementException() {
    when(authUtil.getCurrentUser()).thenReturn(user);
    Assertions.assertThrows(
        NoSuchElementException.class, () -> businessService.addSaleItem(100, saleItemJson));
  }

  @Test
  void addSaleItem_successAddedItem_returnNewSaleItemId() {
    businessService = Mockito.spy(businessService);
    saleItemJson.setQuantity(2);
    Mockito.doNothing().when(businessService).requireAdminOrGAA(any(), any());
    Mockito.when(inventoryItemRepository.findByInventoryItemId(any(long.class))).thenReturn(Optional.of(inventoryItem));
    Mockito.when(saleItemRepository.save(any(SaleItem.class))).
            thenReturn(BusinessDataCreator.createSaleItem(4));
    Assertions.assertEquals(4, businessService.addSaleItem(1, saleItemJson));
  }

  @Test
  void saleItemValidation_acceptableQuantity_returnsTrue() {
    saleItemJson.setQuantity(2);
    Mockito.when(inventoryItemRepository.findByInventoryItemId(any(long.class))).thenReturn(Optional.of(inventoryItem));
    Assertions.assertTrue(businessService.saleItemValidation(saleItemJson));
  }

  @Test
  void saleItemValidation_unacceptableQuantity_throwsBadRequestException() {
    saleItemJson.setQuantity(6);
    Mockito.when(inventoryItemRepository.findByInventoryItemId(any(long.class))).thenReturn(Optional.of(inventoryItem));
    Assertions.assertThrows(
        BadRequestException.class, () -> businessService.saleItemValidation(saleItemJson));
  }

  @Test
  void saleItemValidation_unacceptableQuantity_returnsCorrectErrorMessage() {
    saleItemJson.setQuantity(6);
    Mockito.when(inventoryItemRepository.findByInventoryItemId(any(long.class))).thenReturn(Optional.of(inventoryItem));

    Exception exception =
        Assertions.assertThrows(
            BadRequestException.class, () -> businessService.saleItemValidation(saleItemJson));
    Assertions.assertEquals("Quantity exceeded inventory item quantity.", exception.getMessage());
  }

  @Test
  void purchaseSaleItem_saleItemExistsAndNotSold_saleItemSetToSold() {
    businessService = Mockito.spy(businessService);
    when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);
    when(saleItemRepository.findById(7L)).thenReturn(Optional.of(unsoldSaleItem));
    when(saleItemRepository.save(any(SaleItem.class))).
            thenReturn(BusinessDataCreator.createSaleItem(7L));
    Mockito.doNothing().when(businessService).decrementInventoryItemQuantity(any(), any());
    businessService.purchaseSaleItem(1L, 7L);
    Assertions.assertTrue(unsoldSaleItem.isSold());
  }

  @Test
  void purchaseSaleItem_saleItemExistsAndNotSold_purchaseTimeSet() {
    businessService = Mockito.spy(businessService);
    when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);
    when(saleItemRepository.findById(7L)).thenReturn(Optional.of(unsoldSaleItem));
    when(saleItemRepository.save(any(SaleItem.class))).
            thenReturn(BusinessDataCreator.createSaleItem(7L));
    Mockito.doNothing().when(businessService).decrementInventoryItemQuantity(any(), any());
    businessService.purchaseSaleItem(1L,7L);
    Assertions.assertEquals(LocalDateTime.now().withNano(0), unsoldSaleItem.getPurchased().withNano(0));
  }

  @Test
  void purchaseSaleItem_SaleItemExistsAndNotSold_PurchaserIdSet() {
    businessService = Mockito.spy(businessService);
    when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);
    when(saleItemRepository.findById(7L)).thenReturn(Optional.of(unsoldSaleItem));
    when(saleItemRepository.save(any(SaleItem.class))).
            thenReturn(BusinessDataCreator.createSaleItem(7L));
    Mockito.doNothing().when(businessService).decrementInventoryItemQuantity(any(), any());
    businessService.purchaseSaleItem(1L,7L);
    Assertions.assertEquals(defaultAdmin.getId(), unsoldSaleItem.getPurchaser().getId());
  }

  @Test
  void purchaseSaleItem_saleItemExistsAndNotSold_notificationCreated() {
    SaleItem purchasedItem = BusinessDataCreator.createSaleItem(7L);
    purchasedItem.setSold(true);

    businessService = Mockito.spy(businessService);
    when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);
    when(saleItemRepository.findById(7L)).thenReturn(Optional.of(unsoldSaleItem));
    when(saleItemRepository.save(any(SaleItem.class))).thenReturn(purchasedItem);
    Mockito.doNothing().when(businessService).decrementInventoryItemQuantity(any(), any());

    businessService.purchaseSaleItem(1L, 7L);
    verify(notificationService).createPurchaseNotification(purchasedItem);
  }

  @Test
  void purchaseSaleItem_saleItemExistsAndSold_notificationServiceNotCalled() {
    SaleItem purchasedItem = BusinessDataCreator.createSaleItem(7L);
    purchasedItem.setSold(true);

    businessService = Mockito.spy(businessService);
    when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);
    when(saleItemRepository.findById(7L)).thenReturn(Optional.of(soldSaleItem));
    when(saleItemRepository.save(any(SaleItem.class))).thenReturn(purchasedItem);

    Mockito.doNothing().when(businessService).decrementInventoryItemQuantity(any(), any());
    Assertions.assertThrows(BadRequestException.class, () -> businessService.purchaseSaleItem(1L, 7L));
    verify(notificationService, never()).createPurchaseNotification(any(SaleItem.class));
  }

  @Test
  void purchaseSaleItem_saleItemDoesNotExist_notificationServiceNotCalled() {
    SaleItem purchasedItem = BusinessDataCreator.createSaleItem(7L);
    purchasedItem.setSold(true);
    when(saleItemRepository.save(any(SaleItem.class))).thenReturn(purchasedItem);

    Assertions.assertThrows(
            NoSuchElementException.class, () -> businessService.purchaseSaleItem(1L,100L));
    verify(notificationService, never()).createPurchaseNotification(any(SaleItem.class));
  }

  @Test
  void purchaseSaleItem_saleItemExistsAndSold_throwsBadRequest() {
    businessService = Mockito.spy(businessService);
    when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);
    when(saleItemRepository.findById(7L)).thenReturn(Optional.of(soldSaleItem));
    Mockito.doNothing().when(businessService).decrementInventoryItemQuantity(any(), any());
    Assertions.assertThrows(
            BadRequestException.class, () -> businessService.purchaseSaleItem(1L,7L));
  }

  @Test
  void purchaseSaleItem_saleItemExistsAndSold_returnsCorrectErrorMessage() {
    businessService = Mockito.spy(businessService);
    when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);
    when(saleItemRepository.findById(7L)).thenReturn(Optional.of(soldSaleItem));
    Mockito.doNothing().when(businessService).decrementInventoryItemQuantity(any(), any());
    Exception exception = Assertions.assertThrows(
            BadRequestException.class, () -> businessService.purchaseSaleItem(1L,7L));
    Assertions.assertEquals("Cannot purchase an already sold Sale Listing", exception.getMessage());
  }

  @Test
  void purchaseSaleItem_saleItemDoesNotExist_throwsNoSuchElementException() {
    Assertions.assertThrows(
            NoSuchElementException.class, () -> businessService.purchaseSaleItem(1L,100L));
  }

  @Test
  void purchaseSaleItem_saleItemDoesNotExist_returnsCorrectErrorMessage() {

    Exception exception = Assertions.assertThrows(
            NoSuchElementException.class, () -> businessService.purchaseSaleItem(1L,100L));
    Assertions.assertEquals("No value present", exception.getMessage());
  }

  @Test
  void retrieveSaleItems_isSoldTrue_returnsSoldListingsOnly() {
    businessService = Mockito.spy(businessService);
    when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);

    List<SaleItem> soldList = new ArrayList<>();
    soldList.add(soldSaleItem);

    when(saleItemRepository.findSoldListingsByBusiness(any(long.class))).thenReturn(soldList);

    List<SaleItem> soldItems = businessService.retrieveSaleItems(soldSaleItem.getInventoryItem().getBusiness().getBusinessId(), true);
    Integer expectedLength = 1;
    Assertions.assertEquals(expectedLength, soldItems.size());
  }

  @Test
  void retrieveSaleItems_isSoldFalse_returnsSoldListingsOnly() {
    businessService = Mockito.spy(businessService);
    when(authUtil.getCurrentUser()).thenReturn(defaultAdmin);

    List<SaleItem> unsoldList = new ArrayList<>();
    unsoldList.add(unsoldSaleItem);

    when(saleItemRepository.findSaleListingsByBusiness(any(long.class))).thenReturn(unsoldList);

    List<SaleItem> unsoldItems = businessService.retrieveSaleItems(soldSaleItem.getInventoryItem().getBusiness().getBusinessId(), false);
    Integer expectedLength = 1;
    Assertions.assertEquals(expectedLength, unsoldItems.size());
  }

  @Test
  void sendPurchaseNotificationToOtherUsers_saleItemExists_callsNotificationServiceCorrectTimes() {
    when(saleItemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(soldSaleItem));
    Set<User> likedUsers = new HashSet<>(List.of(user, defaultAdmin));
    soldSaleItem.setLikedByUsers(likedUsers);
    // check that the notification service is called once (since it should not be called for the purchaser)
    Mockito.doNothing().when(notificationService).createLikedItemPurchaseNotification(any(SaleItem.class), any(User.class));
    soldSaleItem.setPurchaser(defaultAdmin);
    businessService.sendPurchaseNotificationToOtherUsers(soldSaleItem.getSaleItemId());
    Mockito.verify(notificationService).createLikedItemPurchaseNotification(any(SaleItem.class), any(User.class));
  }

  @Test
  void sendPurchaseNotificationToOtherUsers_saleItemExistsNoLikes_doesNotCallNotificationService() {
    when(saleItemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(soldSaleItem));
    soldSaleItem.setLikedByUsers(new HashSet<>());
    // check that the notification service is not called
    Mockito.doNothing().when(notificationService).createLikedItemPurchaseNotification(any(SaleItem.class), any(User.class));
    businessService.sendPurchaseNotificationToOtherUsers(soldSaleItem.getSaleItemId());
    Mockito.verify(notificationService, times(0)).createLikedItemPurchaseNotification(any(SaleItem.class), any(User.class));
  }

  @Test
  void sendPurchaseNotificationToOtherUsers_saleItemDoesNotExist_throws406() {
    when(saleItemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    Assertions.assertThrows(NotAcceptableStatusException.class, () -> businessService.sendPurchaseNotificationToOtherUsers(2L));
  }
}
