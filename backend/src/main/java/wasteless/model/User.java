package wasteless.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** This class defines an individual user account, its attributes and its methods. */
@Getter
@Setter
@NoArgsConstructor // Generates a constructor that takes no arguments (for JPA)\
@AllArgsConstructor
@Entity // Declares user as a JPA entity
public class User implements Searchable {

  /** User ID attribute */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long userId;

  /** First name attribute */
  @Column
  @NotBlank(message = "First name cannot be empty")
  @Size(min = 1, max = 250, message = "firstName must be between 1 and 250 characters")
  private String firstName;

  /** Last name attribute */
  @Column
  @NotBlank(message = "Last name cannot be empty")
  @Size(min = 1, max = 250, message = "lastName must be between 1 and 250 characters")
  private String lastName;

  /** Middle name attribute */
  @Column
  @Size(max = 250, message = "middleName must be less than 250 characters")
  private String middleName;

  /** Nickname attribute */
  @Column
  @Size(max = 250, message = "Nickname cannot exceed 250 characters")
  private String nickname;

  /** Bio attribute */
  @Column
  @Size(max = 2000, message = "Bio cannot exceed 2000 characters")
  private String bio;

  /**
   * E-mail attribute. Accepts up to 64 characters in the username. Accepts up to 67 characters in
   * domain (excluding "@").
   */
  @Column(unique = true)
  @NotBlank(message = "E-mail cannot be empty")
  @Email(message = "Invalid E-mail")
  private String email;

  /** Date of birth attribute */
  @Column
  @NotNull(message = "Date cannot be null")
  @Past(message = "Date of Birth cannot be in the future")
  private LocalDate dateOfBirth;

  /** Phone number attribute */
  @Column
  @Size(max = 250)
  private String phoneNumber;

  /** Home address attribute */
  @Valid
  @NotNull(message = "Address cannot be null")
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "address_id", referencedColumnName = "addressId")
  private Address homeAddress;

  /** Password in hashed format */
  @Column
  @NotBlank(message = "Password cannot be empty")
  @Size(min = 1, max = 250)
  private String password;

  /** Registration date. Automatically set when a user is created. */
  @Column private LocalDate created;

  /** The role of the user. For example 'user', 'global_admin', or ''default_global_admin'' */
  @Column
  @Pattern(regexp = "user|global_admin|default_global_admin")
  private String role;

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof User) {
      return ((User) obj).getId() == getId();
    } else {
      return false;
    }
  }

  /** Multivalued attribute, a list of all the businesses that the user is admin of */
  @ManyToMany
  @JoinTable(
      name = "admins",
      joinColumns = @JoinColumn(name = "userId"),
      inverseJoinColumns = @JoinColumn(name = "businessId"))
  private List<Business> businessesAdministered;

  /**  A list of all the sale items purchased by the user. */
  @OneToMany(mappedBy = "purchaser")
  @JsonIgnore
  private List<SaleItem> purchases;

  /** A list of all the notifications related to the user */
  @OneToMany(mappedBy = "relatedUser")
  @JsonIgnore
  private List<Notification> notifications;

  @ManyToMany(mappedBy = "likedByUsers")
  @JsonIgnore
  private Set<SaleItem> likedSaleListings;

  /**
   * Constructor for User class. Note that the 'role' attribute is automatically set to user.
   *
   * @param firstName User's first name (required)
   * @param lastName User's last name (required)
   * @param middleName User's middle name
   * @param nickname User's chosen nickname
   * @param bio User's bio section
   * @param email User's e-mail address (required)
   * @param dateofBirth User's date of birth (required)
   * @param phoneNumber User's phone number
   * @param homeAddress User's home address (required)
   * @param password User's password (as a hash) (required)
   */
  public User(
      String firstName,
      String lastName,
      String middleName,
      String nickname,
      String bio,
      String email,
      LocalDate dateofBirth,
      String phoneNumber,
      Address homeAddress,
      String password) {
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.nickname = nickname;
    this.bio = bio;
    this.email = email;
    this.dateOfBirth = dateofBirth;
    this.phoneNumber = phoneNumber;
    this.homeAddress = homeAddress;
    this.password = password;
    this.created = LocalDate.now();
    this.role = "user";
    this.businessesAdministered = new ArrayList<>();
  }

  /**
   * toString method for User class
   *
   * @return all of User's attributes as a single string.
   */
  @Override
  public String toString() {
    return "User"
        + "\nID: "
        + userId
        + "\nFirst Name: "
        + firstName
        + "\nMiddle Name: "
        + middleName
        + "\nLast Name: "
        + lastName
        + "\nNickname: "
        + nickname
        + "\nBio: "
        + bio
        + "\nE-Mail: "
        + email
        + "\nDate of Birth: "
        + dateOfBirth
        + "\nPhone Number: "
        + phoneNumber
        + "\nHome Address: "
        + homeAddress
        + "\nRegistration Date: "
        + created
        + "\nRole: "
        + role;
  }

  /**
   * This method adds a new business to the businessAdministered arrayList
   *
   * @param business Instance of business to be added
   */
  public void addBusinessAdministered(Business business) {
    this.businessesAdministered.add(business);
  }

  /**
   * This method gets the age of the user account in months rounded down
   *
   * @return The age of the account in months
   */
  @JsonIgnore
  public int getAccountAge() {
    return Period.between(created, LocalDate.now()).getMonths();
  }

  @JsonIgnore // Ignores the deserialization of password into JSON
  public String getPassword() {
    return password;
  }

  /** @param passwordHash The user's password hash (not actual plaintext password) to update */
  @JsonProperty // Allows serialization of JSON into password
  public void setPassword(String passwordHash) {
    this.password = passwordHash;
  }

  /**
   * Sets the role of the user. Will only accept 'user', 'global_admin' or 'default_global_admin',
   * otherwise does nothing.
   *
   * @param role a string
   */
  public void setRole(String role) {
    this.role = role;
  }

  public long getId() {return this.userId;}
}
