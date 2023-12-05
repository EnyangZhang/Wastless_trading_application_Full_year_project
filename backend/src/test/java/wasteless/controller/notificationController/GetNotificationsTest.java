package wasteless.controller.notificationController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.model.Notification;
import wasteless.model.User;
import wasteless.security.AuthUtil;
import wasteless.service.NotificationService;
import wasteless.test_helpers.NotificationDataCreator;
import wasteless.test_helpers.UserDataCreator;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class GetNotificationsTest {
    @MockBean
    NotificationService notificationService;

    @MockBean
    AuthUtil authUtil;

    @Autowired
    private MockMvc mockMvc;

    User user1;
    User dgaa;
    Notification notification;

    @BeforeEach
    void beforeEach() {
        user1 = UserDataCreator.createUser("user1@email.com", "user");
        dgaa = UserDataCreator.createUser("dgaa@email.com", "default_global_admin");
        notification = NotificationDataCreator.createGenericNotification(user1);
    }

    @Test
    @WithMockUser
    void getNotifications_withCorrectUser_sends200() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(user1);
        mockMvc
                .perform(MockMvcRequestBuilders.get("/notifications/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser
    void getNotifications_withDGAA_sends_200() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(dgaa);
        mockMvc
                .perform(MockMvcRequestBuilders.get("/notifications/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
