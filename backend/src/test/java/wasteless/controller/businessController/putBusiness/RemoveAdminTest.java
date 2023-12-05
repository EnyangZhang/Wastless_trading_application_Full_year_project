package wasteless.controller.businessController.putBusiness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.model.Address;
import wasteless.model.Business;
import wasteless.model.User;
import wasteless.repository.BusinessRepository;
import wasteless.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class RemoveAdminTest {

  @Autowired
  private MockMvc mockMvc;

  private final String reqBody = "{\"userId\": %s}";
  private final String url = "/businesses/%s/removeAdministrator";
  @MockBean private UserRepository userRepository;
  @MockBean private BusinessRepository businessRepository;
  private User primaryAdmin;
  private User targetUser;
  private User alreadyAdmin;
  private User dgaa;
  private User gaa;
  private Business targetBusiness;

  @BeforeEach
  public void setup() {
    primaryAdmin =
        new User(
            1L,
            "Primary",
            "Admin",
            "",
            "a",
            "a",
            "primary@admin.co",
            LocalDate.parse("1997-02-01"),
            "",
            new Address("8", "Madeup Road", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"),
            "password123",
            LocalDate.now(),
            "user",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashSet<>());

    targetUser =
        new User(
            2L,
            "Target",
            "User",
            "",
            "a",
            "a",
            "target@user.co",
            LocalDate.parse("1997-02-01"),
            "",
            new Address("8", "Madeup Road", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"),
            "password123",
            LocalDate.now(),
            "user",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashSet<>());

    alreadyAdmin =
        new User(
            3L,
            "Already",
            "Admin",
            "",
            "a",
            "a",
            "already@admin.co",
            LocalDate.parse("1997-02-01"),
            "",
            new Address("8", "Madeup Road", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"),
            "password123",
            LocalDate.now(),
            "user",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashSet<>());

    dgaa =
        new User(
            4L,
            "admin@defaultglobal",
            "Default Global",
            "",
            "DGAA",
            "",
            "admin@defaultglobal",
            LocalDate.parse("1997-02-01"),
            "",
            new Address("8", "Madeup Road", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"),
            "password123",
            LocalDate.now(),
            "default_global_admin",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashSet<>());

    gaa =
        new User(
            5L,
            "global",
            "admin",
            "",
            "GAA",
            "",
            "global@admin.co",
            LocalDate.parse("1997-02-01"),
            "",
            new Address("8", "Madeup Road", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"),
            "password123",
            LocalDate.now(),
            "global_admin",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashSet<>());

    ArrayList<User> admins = new ArrayList<>();
    admins.add(alreadyAdmin);
    targetBusiness =
        new Business(
            "targetBusiness",
            "Test target bus",
            new Address("8", "Madeup Road", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"),
            "",
            1L,
            admins);

    when(userRepository.findByEmail("primary@admin.co"))
        .thenReturn(java.util.Optional.ofNullable(primaryAdmin));
    when(userRepository.findByEmail("target@user.co"))
        .thenReturn(java.util.Optional.ofNullable(targetUser));
    when(userRepository.findByEmail("already@admin.co"))
        .thenReturn(java.util.Optional.ofNullable(alreadyAdmin));
    when(userRepository.findByEmail("admin@defaultglobal"))
        .thenReturn(java.util.Optional.ofNullable(dgaa));
    when(userRepository.findByEmail("global@admin.co"))
        .thenReturn(java.util.Optional.ofNullable(gaa));

    when(userRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(primaryAdmin));
    when(userRepository.findById(2)).thenReturn(java.util.Optional.ofNullable(targetUser));
    when(userRepository.findById(3)).thenReturn(java.util.Optional.ofNullable(alreadyAdmin));
    when(userRepository.findById(4)).thenReturn(java.util.Optional.ofNullable(dgaa));
    when(userRepository.findById(5)).thenReturn(java.util.Optional.ofNullable(gaa));

    when(businessRepository.findByBusinessId(1L)).thenReturn(java.util.Optional.ofNullable(targetBusiness));
    ArrayList<Business> targetBusinessList = new ArrayList<>();
    targetBusinessList.add(targetBusiness);
    when(businessRepository.findAllByPrimaryAdminId(1L)).thenReturn(targetBusinessList);
    when(businessRepository.findAllByPrimaryAdminId(2L)).thenReturn(new ArrayList<>());
  }

  @WithMockUser("admin@defaultglobal")
  @Test
  void removeAdminSuccessfullyDGAA() throws Exception {
    long busId = 1;
    long targetUserId = 2;
    targetUser.addBusinessAdministered(targetBusiness);
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(String.format(url, busId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(reqBody, targetUserId)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser("global@admin.co")
  @Test
  void removeAdminSuccessfullyGAA() throws Exception {
    long busId = 1;
    long targetUserId = 2;
    targetUser.addBusinessAdministered(targetBusiness);
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(String.format(url, busId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(reqBody, targetUserId)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser("primary@admin.co")
  @Test
  void removeAdminSuccessfullyPrimaryAdmin() throws Exception {
    long busId = 1;
    long targetUserId = 2;
    targetUser.addBusinessAdministered(targetBusiness);
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(String.format(url, busId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(reqBody, targetUserId)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void removeAdminNotLoggedIn() throws Exception {
    long busId = 1;
    long targetUserId = 2;
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(String.format(url, busId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(reqBody, targetUserId)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser("admin@defaultglobal")
  @Test
  void removeAdminTargetUserDoesNotExist() throws Exception {
    long busId = 1;
    long targetUserId = 99;
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(String.format(url, busId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(reqBody, targetUserId)))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @WithMockUser("admin@defaultglobal")
  @Test
  void removeAdminTargetUserPrimaryAdmin() throws Exception {
    long busId = 1;
    long targetUserId = 1;
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(String.format(url, busId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(reqBody, targetUserId)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @WithMockUser("admin@defaultglobal")
  @Test
  void removeAdminTargetUserNotAdmin() throws Exception {
    long busId = 1;
    long targetUserId = 2;
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(String.format(url, busId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(reqBody, targetUserId)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @WithMockUser("already@admin.co")
  @Test
  void removeAdminUserRegularAdmin() throws Exception {
    long busId = 1;
    long targetUserId = 3;
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(String.format(url, busId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(reqBody, targetUserId)))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser("target@user.co")
  @Test
  void removeAdminUserRegularUser() throws Exception {
    long busId = 1;
    long targetUserId = 3;
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(String.format(url, busId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(reqBody, targetUserId)))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser("admin@defaultglobal")
  @Test
  void removeAdminBusinessDoesNotExist() throws Exception {
    long busId = 99;
    long targetUserId = 3;
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(String.format(url, busId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(reqBody, targetUserId)))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }
}
