<!-- This file deals with the sign up page that users will visit when they want to create an account -->

<template>
    <div id="signup">
      <b-container fluid="true">
        <!-- Rows and columns centre the form in the middle of the page -->
        <b-row class="row justify-content-center">
          <b-col md="5" cols="auto" align-self="center">
            <b-card id="edit-profile-pane">
              <b-card-title title="Sign Up" align="center">Sign Up</b-card-title>
              <b-form id="signupForm" @submit="onSubmit" v-if="show" :novalidate=true>
                <!-- Required input field for first name -->
                <div class="firstName">
                  <b-form-input block
                                type="text"
                                name="firstName"
                                v-model="form.firstName"
                                placeholder="First Name*"
                                :state="signupValidation.isNotInvalidName(submitted, form.firstName)"
                                />
                  <b-form-invalid-feedback id="invalidFirstName"
                                           v-bind:class="{'yellow-error': signupValidation.containsSpecialChars(form.firstName)}"
                                           :state="signupValidation.validName(submitted, form.firstName)">
                    {{ signupValidation.nameMessage(form.firstName)}}
                  </b-form-invalid-feedback>
                </div>
                <br/>
                <!-- Input field for middle name -->
                <div class="middleName">
                  <b-form-input block
                                type="text"
                                name="middleName"
                                v-model="form.middleName"
                                placeholder="Middle Name(s)"
                                :state="signupValidation.isNotInvalidOptionalName(form.middleName)"
                  />
                  <b-form-invalid-feedback
                      id="invalidNickname"
                      v-bind:class="{'yellow-error': signupValidation.containsSpecialChars(form.middleName)}"
                      :state="signupValidation.validOptionalName(form.middleName)">
                    {{signupValidation.nameMessage(form.middleName)}}
                  </b-form-invalid-feedback>
                </div>
                <br/>
                <!-- Required input field for surname -->
                <div class="surname">
                  <b-form-input block
                                type="text"
                                name="surname"
                                v-model="form.lastName"
                                placeholder="Last Name*"
                                :state="signupValidation.isNotInvalidName(submitted, form.lastName)"/>
                  <b-form-invalid-feedback
                      id="invalidLastName"
                      v-bind:class="{'yellow-error': signupValidation.containsSpecialChars(form.lastName)}"
                      :state="signupValidation.validName(submitted, form.lastName)">
                    {{signupValidation.nameMessage(form.lastName)}}
                  </b-form-invalid-feedback>
                </div>
                <br/>
                <!-- Input field for nickname -->
                <div class="nickname">
                  <b-form-input block
                                type="text"
                                name="nickname"
                                v-model="form.nickname"
                                placeholder="Nickname"
                                :state="signupValidation.lengthValidation(form.nickname, 100)"
                  />
                  <b-form-invalid-feedback
                      id="invalidMiddleName"
                      :state="signupValidation.lengthValidation(form.nickname, 100)">
                    Limit of 100 characters exceeded
                  </b-form-invalid-feedback>
                </div>
                <br/>
                <!-- Required input field for password -->
                <div class="password">
                  <b-form-input block
                                type="password"
                                name="password"
                                v-model="form.password"
                                placeholder="Enter Password*"
                                title="Your password must contain at least 8 characters"
                                :state="signupValidation.password(submitted, form.password)"/>
                  <b-form-invalid-feedback
                      id="invalidPassword"
                      :state="signupValidation.password(submitted, form.password)">
                    Your password must be 8-250 characters long
                  </b-form-invalid-feedback>
                  <b-form-valid-feedback :state="signupValidation.password(submitted, form.password)"/>
                </div>
                <br/>
                <!-- Required input field for confirming password -->
                <div class="confirmPassword">
                  <b-form-input block
                                type="password"
                                name="confirmPassword"
                                v-model="form.confirmPassword"
                                placeholder="Confirm Password*"
                                :state="passwordConfirmed()"/>
                  <b-form-invalid-feedback
                      id="invalidPasswordConfirm"
                      :state="passwordConfirmed()">
                    {{confirmPasswordInvalidFeedback}}
                  </b-form-invalid-feedback>
                  <b-form-valid-feedback :state="passwordConfirmed()"></b-form-valid-feedback>
                </div>
                <br/>
                <!-- Required input field for email -->
                <div class="email">
                  <b-form-input block
                                type="text"
                                name="email"
                                v-model="form.email"
                                placeholder="Email Address*"
                                title="Your email will be visible to other users."
                                v-on:keydown="isEmailUnique=true"
                                :state="(signupValidation.email(submitted, form.email) && isEmailDuplicate())"/>
                </div>
                <b-form-invalid-feedback
                    id="invalidEmail"
                    :state="signupValidation.email(submitted, form.email)">
                  Please enter a valid email address with less than 250 characters.
                </b-form-invalid-feedback>
                <b-form-valid-feedback
                    :state="(signupValidation.email(submitted, form.email) && isEmailDuplicate())"/>
                <b-form-invalid-feedback
                    id="duplicateEmail"
                    :state="isEmailDuplicate()">
                  Email already in use.
                </b-form-invalid-feedback>
                <br/>
                <!-- Input field for phone number -->
                <div class="phoneNumber">
                  <b-form-input block
                                type="tel"
                                name="phoneNumber"
                                v-model="form.phoneNumber"
                                placeholder="Phone Number"
                                :state="signupValidation.phoneNumber(form.phoneNumber)"/>
                  <b-form-invalid-feedback
                      id="invalidPhoneNumber"
                      :state="signupValidation.phoneNumber(form.phoneNumber)">
                    Please enter a valid phone number. A valid phone number has 7-15 characters (optionally with a + at the start and up to 5 spaces throughout)
                  </b-form-invalid-feedback>
                  <b-form-valid-feedback :state="signupValidation.phoneNumber(form.phoneNumber)"/>
                </div>
                <br/>
                <!-- Required input field for birthday-->
                <div class="birthday">
                  <b-form-input block
                                name="birthday"
                                type="text"
                                v-model="form.dateOfBirth"
                                placeholder="Date of Birth*"
                                title="Please enter your date of birth"
                                onfocus="(this.type='date')"
                                :state="dateOfBirthValidation()"/>
                  <b-form-invalid-feedback
                      id="invalidDateOfBirth"
                      :state="dateOfBirthValidation()">
                    {{dateOfBirthInvalidFeedback}}
                  </b-form-invalid-feedback>
                  <b-form-valid-feedback :state="dateOfBirthValidation()"/>
                </div>
                <br/>

                <!--Address autocomplete input field-->
                <b-form-group id="inputGroup">
                  <AutoCompleteInput
                      @setSuggestion="fillAddress($event)"
                      :suggestions="suggestions"
                      @clearSuggestions="suggestions = []"
                      @getSuggestions="searchApi($event)"/>
                </b-form-group>


                <!--Address input field for street number-->
              <div class="streetNumber">
                <b-form-input block
                              type="text"
                              name="streetNumber"
                              autocomplete="nope"
                              v-model="form.homeAddress.streetNumber"
                              :state="addressValidation.streetNumber(submitted, form.homeAddress.streetNumber)"
                              placeholder="Street Number*"/>
                <b-form-invalid-feedback
                    id="invalidStreetNumber"
                    :state="addressValidation.streetNumber(submitted, form.homeAddress.streetNumber)">
                  {{ addressValidation.streetNumberMessage() }}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="addressValidation.streetNumber(submitted, form.homeAddress.streetNumber)"></b-form-valid-feedback>

              </div>
              <br/>

                <!--Address input field for street-->
                <b-form-group>
                  <b-form-input block
                                type="text"
                                name="streetName"
                                v-model="form.homeAddress.streetName"
                                placeholder="Street Name*"
                                :state="addressValidation.isNotInvalidField(submitted, form.homeAddress.streetName)"
                  />
                  <b-form-invalid-feedback
                      id="invalidStreetName"
                      v-bind:class="{'yellow-error': addressValidation.containsSpecialChars(form.homeAddress.streetName)}"
                      :state="addressValidation.validField(submitted, form.homeAddress.streetName)">
                    {{ addressValidation.fieldMessage('Street name', form.homeAddress.streetName) }}
                  </b-form-invalid-feedback>
                </b-form-group>

                <!--Address input field for suburb-->
                <b-form-group>
                  <b-form-input block
                                type="text"
                                name="suburb"
                                v-model="form.homeAddress.suburb"
                                placeholder="Suburb*"
                                :state="addressValidation.isNotInvalidField(submitted, form.homeAddress.suburb)"
                  />
                  <b-form-invalid-feedback
                      id="invalidSuburb"
                      v-bind:class="{'yellow-error': addressValidation.containsSpecialChars(form.homeAddress.suburb)}"
                      :state="addressValidation.validField(submitted, form.homeAddress.suburb)">
                    {{ addressValidation.fieldMessage('Suburb', form.homeAddress.suburb) }}
                  </b-form-invalid-feedback>
                </b-form-group>

                <!--Address input field for city-->
                <b-form-group>
                  <b-form-input block
                                type="text"
                                name="city"
                                v-model="form.homeAddress.city"
                                placeholder="City*"
                                :state="addressValidation.isNotInvalidField(submitted, form.homeAddress.city)"
                  />
                  <b-form-invalid-feedback
                      id="invalidCity"
                      v-bind:class="{'yellow-error': addressValidation.containsSpecialChars(form.homeAddress.city)}"
                      :state="addressValidation.validField(submitted, form.homeAddress.city)">
                    {{ addressValidation.fieldMessage('City', form.homeAddress.city) }}
                  </b-form-invalid-feedback>
                </b-form-group>

                <!--Address input field for region-->
                <b-form-group>
                  <b-form-input block
                                type="text"
                                name="region"
                                v-model="form.homeAddress.region"
                                placeholder="Region*"
                                :state="addressValidation.isNotInvalidField(submitted, form.homeAddress.region)"
                  />
                  <b-form-invalid-feedback
                      id="invalidRegion"
                      v-bind:class="{'yellow-error': addressValidation.containsSpecialChars(form.homeAddress.region)}"
                      :state="addressValidation.validField(submitted, form.homeAddress.region)">
                    {{ addressValidation.fieldMessage('Region', form.homeAddress.region) }}
                  </b-form-invalid-feedback>
                </b-form-group>

                <!--Address input field for country-->
                <b-form-group>
                  <b-form-input block
                                type="text"
                                name="country"
                                v-model="form.homeAddress.country"
                                placeholder="Country*"
                                :state="addressValidation.countryValidation(submitted, form.homeAddress.country)"
                  />
                  <b-form-invalid-feedback
                      id="invalidCountry"
                      :state="addressValidation.countryValidation(submitted, form.homeAddress.country)">
                    {{ addressValidation.countryMessage() }}
                  </b-form-invalid-feedback>
                </b-form-group>

              <!-- Input field for post code -->
              <div class="postcode">
                <b-form-input block
                              type="text"
                              name="postCode"
                              autocomplete="nope"
                              v-model="form.homeAddress.postcode"
                              placeholder="Postcode*"
                              :state="addressValidation.postcode(submitted, form.homeAddress.postcode)"/>
                <b-form-invalid-feedback
                    id="invalidPostcode"
                    :state="addressValidation.postcode(submitted, form.homeAddress.postcode)">
                  {{ addressValidation.postcodeMessage() }}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="addressValidation.postcode(submitted, form.homeAddress.postcode)"></b-form-valid-feedback>

              </div>
              <br/>

                <br/>
                <!-- Input field for user bio -->
                <div class="bio">
                  <b-form-textarea block
                                   type="text"
                                   name="bio"
                                   v-model="form.bio"
                                   placeholder="Write a brief description about yourself..."
                                   :state="signupValidation.lengthValidation(this.form.bio, 500)"
                  />
                  <b-form-invalid-feedback
                      id="invalidBio"
                      :state="signupValidation.lengthValidation(this.form.bio, 500)">
                    Please do not exceed 500 characters.
                  </b-form-invalid-feedback>
                </div>
                <br/>
                <b-button block
                          type="submit"
                          name="submit"
                          id="submit"
                          variant="success"> Sign up </b-button>
                <br/>
              </b-form>
            </b-card>
          </b-col>

        </b-row>

      </b-container>
    </div>
</template>

<script>

import AutoCompleteInput from "../components/address/AutoCompleteWidget";
import api from "../Api.js"
import AddressValidation from "../validation/address.validation";
import SignupValidation from "../validation/signup.validation";

const Signup = {
  name: "Signup",
  data: function () {
    const now = new Date();
    // Plus one to the day as otherwise people exactly 13 can't register
    const maxDate = new Date(now.getFullYear()-13, now.getMonth(), now.getDate()+1);
    const minDate = new Date(1900, 0, 1);


    return {
      min: minDate,
      max: maxDate,
      submitted: false, // submit button pressed
      showDoesNotMatch: false,
      showNotLongEnough: false,
      showTooYoung: false,
      showTooOld: false,
      form: {
        firstName: '',
        middleName: '',
        lastName: '',
        nickname: '',
        password: '',
        confirmPassword: '',
        email: '',
        phoneNumber: null,
        dateOfBirth: null,
        homeAddress: {
          streetNumber: '',
          streetName: '',
          suburb: '',
          city: '',
          region: '',
          country: '',
          postcode: '',
        },
        bio: ''
      },
      show : true,

      suggestions: [],
      selected: 'item1',
      items: {
        1: {id: 1, val: 'item1'},
        2: {id: 2, val: 'item2'},
        3: {id: 3, val: 'item3'},
      },
      isEmailUnique: true,
      signupValidation: SignupValidation,
      addressValidation: AddressValidation
    }
  },
  methods: {

    /**
     * Method to reset signup page input fields when either submit is clicked and an account is successfully created
     * or when the login page button on the navbar is pressed and the user is taken away from the page;
     */

    resetSignUpForm() {
      this.form.firstName = '';
      this.form.middleName = '';
      this.form.lastName = '';
      this.form.nickname = '';
      this.form.password = '';
      this.form.confirmPassword = '';
      this.form.email = '';
      this.form.phoneNumber = null;
      this.form.dateOfBirth = null;
      this.form.homeAddress.streetName = '';
      this.form.homeAddress.streetNumber = '';
      this.form.homeAddress.suburb = '';
      this.form.homeAddress.city = '';
      this.form.homeAddress.region = '';
      this.form.homeAddress.country = '';
      this.form.homeAddress.postcode = '';
      this.form.bio = '';
    },

    /**
     * Get address auto-complete result from api.
     * @param userInputAddress the input from user.
     * @returns {Promise<void>}
     */
    searchApi: function (userInputAddress) {
      api.getAutoComplete(userInputAddress).then((response) => {
        let responseLocations = response.data.features
        this.suggestions = []
        let suggestionSet = new Set()

        for (let i = 0; i < responseLocations.length; i++) {
          let location = responseLocations[i].properties
          let address = {
            streetNumber: (location.housenumber) ? location.housenumber : '',
            streetName: (location.street) ? location.street : '',
            suburb: (location.district) ? location.district : '',
            city: (location.city) ? location.city : '',
            region: (location.state) ? location.state : '',
            country: (location.country) ? location.country : '',
            postcode: (location.postcode) ? location.postcode : ''
          }
          let addressString = address["streetNumber"] + address["streetName"] + address["suburb"]  +
              address["city"] + address["region"] + address["country"] + address["postcode"]

          // Remove numbers/spaces/commas from user input to check if suggestions contain a substring of the user input
          userInputAddress = userInputAddress.replace(/[0-9]/g, '').replace(/\s+/g, '').replace(/,/g, '');

          // Remove spaces from suggestion to match with user input
          addressString = addressString.replace(/\s+/g, '')

          if (addressString.toLowerCase().includes(userInputAddress.toLowerCase())) {
            suggestionSet.add(address)
          }
          if (suggestionSet.size === 5){
            break
          }
        }
        this.suggestions = Array.from(suggestionSet)
      }) .catch(
          this.suggestions = []
      )

    },

    /**
     * Validator triggered by submit button.
     * @param event
     */
    onSubmit: function (event) {
      this.submitted = true;
      // When clicked checks if all fields are valid and submits data if true else does nothing and alerts the user
      if (this.canSubmit()) {
        event.preventDefault();
        this.$emit('successfulSignup'); // for testing purposes
        api.createUser(this.form).then( (response) => {
          if (response.status === 409) {
            this.isEmailUnique = false;
          } else if (response.status === 201) {
            const id = response.data.userId;
            this.$router.push({name: 'Profile', params: {id: id}});
            this.resetSignUpForm();
          }
        })
        .catch(() => {
        })
      } else {
        event.preventDefault();
      }
    },

    fillAddress(address) {
      this.form.homeAddress.streetNumber = address.streetNumber
      this.form.homeAddress.streetName = address.streetName
      this.form.homeAddress.suburb = address.suburb
      this.form.homeAddress.city = address.city
      this.form.homeAddress.region = address.region
      this.form.homeAddress.country = address.country
      this.form.homeAddress.postcode = address.postcode
    },

    /**
     * Method to check on the frontend whether or not the user can sign up when they click the Sign up button
     * @returns {boolean}
     */
    // The methods below check the validity of the input fields of the sign up page

    isEmailDuplicate() {
      return this.isEmailUnique;
    },

    canSubmit() {
      let namesValidated = false;
      let passwordsValidated = false;
      let detailsValidated = false;
      let addressValidated = false;
      let otherFieldsNotTooLong = true;

      if (SignupValidation.isNotInvalidName(this.submitted, this.form.firstName) !== false
          && SignupValidation.isNotInvalidName(this.submitted, this.form.lastName) !== false) {
        namesValidated = true;
      }
      if (SignupValidation.password(this.submitted, this.form.password) && this.passwordConfirmed()) {
        passwordsValidated = true;
      }
      if (SignupValidation.email(this.submitted, this.form.email)
          && this.dateOfBirthValidation()
          && SignupValidation.phoneNumber(this.form.phoneNumber) !== false) {
        detailsValidated = true;
      }

      let validStreetNumber = AddressValidation.streetNumber(this.submitted, this.form.homeAddress.streetNumber);
      let validStreetName = AddressValidation.isNotInvalidField(this.submitted, this.form.homeAddress.streetName) !== false;
      let validSuburb = AddressValidation.isNotInvalidField(this.submitted, this.form.homeAddress.suburb) !== false;
      let validCity = AddressValidation.isNotInvalidField(this.submitted, this.form.homeAddress.city) !== false;
      let validRegion = AddressValidation.isNotInvalidField(this.submitted, this.form.homeAddress.region) !== false;
      let validCountry = AddressValidation.countryValidation(this.submitted, this.form.homeAddress.country);
      let validPostcode = AddressValidation.postcode(this.submitted, this.form.homeAddress.postcode);
      if (validStreetName && validStreetNumber && validSuburb && validCity && validRegion && validCountry
          && validCountry && validPostcode) {
        addressValidated = true;
      }


      if (SignupValidation.lengthValidation(this.form.bio, 500) === false
          || SignupValidation.isNotInvalidOptionalName(this.form.middleName) === false
          || SignupValidation.isNotInvalidOptionalName(this.form.nickname) === false) {
        otherFieldsNotTooLong = false;
      }

      return namesValidated && passwordsValidated && detailsValidated && addressValidated && otherFieldsNotTooLong;
    },

    /**
     * Method to validate confirm password field.
     * Checks if the submit button has been pressed so the box is not highlighted red on page load, but only when
     * data is entered in either the password or confirmed password fields and is not valid according to the checks in the method
     * @returns {null|boolean}
     */
    passwordConfirmed() {
      if (this.form.confirmPassword.length === 0 && this.form.password.length === 0) {
        if (this.submitted === false) {
          return null;
        } else if (this.submitted === true) {
          this.showNotLongEnough = true;
          this.showDoesNotMatch = false;
          return false;
        }
      } else if (this.form.confirmPassword.length < 8) {
        this.showNotLongEnough = true;
        this.showDoesNotMatch = false
        return false;
      } else if (this.form.confirmPassword !== this.form.password) {
        this.showNotLongEnough = false;
        this.showDoesNotMatch = true;
        return false;
      } else {
        return this.form.confirmPassword === this.form.password;
      }
    },
    
    /**
     * Calls signup validation function
     */
    dateOfBirthValidation() {
      let {valid, showTooYoung, showTooOld} = SignupValidation.dob(
          this.form.dateOfBirth, this.submitted, this.min, this.max);
      this.showTooYoung = showTooYoung;
      this.showTooOld = showTooOld;
      return valid;
    },
  },

  components: {

    // list your components here to register them (located under 'components' folder)
    // https://vuejs.org/v2/guide/components-registration.html
    AutoCompleteInput,

  },
  computed: {
    autoCompleteData() {
      document.$refs.autoComplete.fetchData()
    },
    /**
     * Computed function that updates the error text for the confirm password input field
     * @returns {string}
     */
    confirmPasswordInvalidFeedback() {
      if (this.showDoesNotMatch) {
        return "Your passwords must match.";
      } else if (this.showNotLongEnough) {
        return "Your password must be at least 8 characters long."
      }
    },
    /**
     * Computed function that updates the error text for the date of birth input field
     * @returns {string}
     */
    dateOfBirthInvalidFeedback() {
      if (this.showTooOld) {
        return "Please enter your date of birth (you must be born on or after 01/01/1900)."
      } else if(this.showTooYoung) {
        return "Please enter your date of birth (you must be older than 13 to register)."
      } else {
        return "Please enter your date of birth";
      }
    }
  },

  /**
   * mounts the reset form function it can be called in the NavigationBar.vue module
   *
   */
  mounted: function() {
    this.$root.$on('resetSignup', () => {
      this.resetSignUpForm();
    })
  }
}

export default Signup;

</script>

<style scoped>
#edit-profile-pane {
  padding: 0 0 0 0 !important;
  margin: 5vh 0 5vh 0 !important;
}

.yellow-error {
  color: darkorange;
}


</style>

