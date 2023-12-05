package wasteless.service.businessServiceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.NotAcceptableStatusException;
import wasteless.model.Business;
import wasteless.model.SaleItem;
import wasteless.model.User;
import wasteless.repository.BusinessRepository;
import wasteless.repository.SaleItemRepository;
import wasteless.security.AuthUtil;
import wasteless.service.BusinessService;
import wasteless.service.NotificationService;
import wasteless.test_helpers.BusinessDataCreator;
import wasteless.test_helpers.UserDataCreator;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class LikingSaleItemTest {
    @Autowired
    private BusinessService businessService;

    @MockBean
    private AuthUtil authUtil;

    @MockBean
    private SaleItemRepository saleItemRepository;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private NotificationService notificationService;

    private User user;
    private Business business;
    private SaleItem saleItem;

    @BeforeEach
    void setup() {
        user = UserDataCreator.createUser();
        saleItem = BusinessDataCreator.createSaleItem(1);
        saleItem.setLikedByUsers(new HashSet<>());
        business = saleItem.getInventoryItem().getBusiness();
        doNothing().when(notificationService).createUnlikedAnItemNotification(any(SaleItem.class), any(User.class));
        doNothing().when(notificationService).createLikedAnItemNotification(any(SaleItem.class), any(User.class));
    }

    @Test
    void likeSaleItem_correctParameters_noExceptionThrown() {
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(saleItemRepository.findById(anyLong())).thenReturn(Optional.of(saleItem));
        when(businessRepository.findByBusinessId(anyLong())).thenReturn(Optional.of(business));

        assertDoesNotThrow(() -> businessService.likeSaleItem(1, 1));
    }

    @Test
    void unlikeSaleItem_correctParameters_noExceptionThrown() {
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(saleItemRepository.findById(anyLong())).thenReturn(Optional.of(saleItem));
        when(businessRepository.findByBusinessId(anyLong())).thenReturn(Optional.of(business));

        assertDoesNotThrow(() -> businessService.unlikeSaleItem(1, 1));
    }

    @Test
    void likeSaleItem_correctParameters_saveCalledWithCorrectAttributes() {
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(saleItemRepository.findById(anyLong())).thenReturn(Optional.of(saleItem));
        when(businessRepository.findByBusinessId(anyLong())).thenReturn(Optional.of(business));

        businessService.likeSaleItem(1L, 1L);

        ArgumentCaptor<SaleItem> saleItemArgumentCaptor = ArgumentCaptor.forClass(SaleItem.class);
        verify(saleItemRepository).save(saleItemArgumentCaptor.capture());
        SaleItem saveCalledWith = saleItemArgumentCaptor.getValue();
        assertEquals(1, saveCalledWith.getLikedByUsers().size());
    }

    @Test
    void unlikeSaleItem_correctParameters_saveCalledWithCorrectAttributes() {
        saleItem.getLikedByUsers().add(user);

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(saleItemRepository.findById(anyLong())).thenReturn(Optional.of(saleItem));
        when(businessRepository.findByBusinessId(anyLong())).thenReturn(Optional.of(business));

        businessService.unlikeSaleItem(1L, 1L);

        ArgumentCaptor<SaleItem> saleItemArgumentCaptor = ArgumentCaptor.forClass(SaleItem.class);
        verify(saleItemRepository).save(saleItemArgumentCaptor.capture());
        SaleItem saveCalledWith = saleItemArgumentCaptor.getValue();
        assertEquals(0, saveCalledWith.getLikedByUsers().size());
    }

    @Test
    void likeSaleItem_incorrectBusinessIDParameter_badRequestExceptionThrown() {
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findByBusinessId(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotAcceptableStatusException.class, () -> businessService.likeSaleItem(1, 1));
        assertTrue(exception.getMessage().contains("Business ID does not exist."));
    }

    @Test
    void likeSaleItem_incorrectSaleItemIDParameter_badRequestExceptionThrown() {
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findByBusinessId(anyLong())).thenReturn(Optional.of(business));
        when(saleItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotAcceptableStatusException.class, () -> businessService.likeSaleItem(1, 1));
        assertTrue(exception.getMessage().contains("SaleItem ID does not exist."));
    }

    @Test
    void likeSaleItem_saleItemUnrelatedToBusiness_badRequestExceptionThrown() {
        SaleItem unrelatedSaleItem = BusinessDataCreator.createSaleItem(2);
        unrelatedSaleItem.getInventoryItem().setBusiness(BusinessDataCreator.createBusiness(100));

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findByBusinessId(anyLong())).thenReturn(Optional.of(business));
        when(saleItemRepository.findById(anyLong())).thenReturn(Optional.of(unrelatedSaleItem));

        Exception exception = assertThrows(NotAcceptableStatusException.class, () -> businessService.likeSaleItem(1, 1));
        assertTrue(exception.getMessage().contains("SaleItem not related to business."));
    }

    @Test
    void unlikeSaleItem_thatIsNotLiked_noActionTaken() {
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(saleItemRepository.findById(anyLong())).thenReturn(Optional.of(saleItem));
        when(businessRepository.findByBusinessId(anyLong())).thenReturn(Optional.of(business));

        businessService.unlikeSaleItem(1L, 1L);

        ArgumentCaptor<SaleItem> saleItemArgumentCaptor = ArgumentCaptor.forClass(SaleItem.class);
        verify(saleItemRepository).save(saleItemArgumentCaptor.capture());
        SaleItem saveCalledWith = saleItemArgumentCaptor.getValue();
        assertEquals(0, saveCalledWith.getLikedByUsers().size());
    }

    @Test
    void likeSaleItem_thatIsAlreadyLiked_noActionTaken() {
        saleItem.getLikedByUsers().add(user);

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(saleItemRepository.findById(anyLong())).thenReturn(Optional.of(saleItem));
        when(businessRepository.findByBusinessId(anyLong())).thenReturn(Optional.of(business));

        businessService.likeSaleItem(1L, 1L);

        ArgumentCaptor<SaleItem> saleItemArgumentCaptor = ArgumentCaptor.forClass(SaleItem.class);
        verify(saleItemRepository).save(saleItemArgumentCaptor.capture());
        SaleItem saveCalledWith = saleItemArgumentCaptor.getValue();
        assertEquals(1, saveCalledWith.getLikedByUsers().size());
    }
}
