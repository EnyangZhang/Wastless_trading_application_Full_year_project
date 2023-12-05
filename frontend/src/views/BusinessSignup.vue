<template>
  <div id="businessSignup">
    <b-container fluid="true">
      <!-- Rows and columns centre the form in the middle of the page -->
      <b-row class="row justify-content-center">
        <b-col md="5" cols="auto" align-self="center">
          <b-card id="edit-profile-pane">
            <b-card-title title="Create New Business" align="center">Create New Business</b-card-title>
            <b-form @submit="onSubmit" v-if="show">
              <!-- Required input field for first name -->
              <div class="bue-input">
                <b-form-input block
                              type="text"
                              name="businessName"
                              v-model="form.name"
                              placeholder="Name of Business*"
                              :state="businessNameValidation()"/>
                <b-form-invalid-feedback :state="businessNameValidation()">
                  {{ businessNameInvalidMessage() }}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="businessNameValidation()"></b-form-valid-feedback>
              </div>
              <br/>
              <!-- Selection for business type -->
              <div class="bue-input">
                <b-form-select block
                          v-model="form.businessType"
                          :options="options"
                          :state="businessTypeValidation()"/>
                <b-form-invalid-feedback :state="businessTypeValidation()">
                  {{ businessTypeInvalidMessage() }}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="businessTypeValidation()"></b-form-valid-feedback>
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
                              v-model="form.address.streetNumber"
                              :state="streetNumberValidation()"
                              placeholder="Street Number*"/>
                <b-form-invalid-feedback :state="streetNumberValidation()">
                  {{ streetNumberInvalidMessage() }}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="streetNumberValidation()"></b-form-valid-feedback>
              </div>
              <br/>

              <!--Address input field for street-->
              <b-form-group>
                <b-form-input block
                              type="text"
                              name="streetName"
                              v-model="form.address.streetName"
                              placeholder="Street Name*"
                              :state="addressValidation.isNotInvalidField(submitted, form.address.streetName)"
                />
                <b-form-invalid-feedback
                    v-bind:class="{'yellow-error': addressValidation.containsSpecialChars(form.address.streetName)}"
                    :state="addressValidation.validField(submitted, form.address.streetName)">
                  {{ addressValidation.fieldMessage('Street name', form.address.streetName) }}
                </b-form-invalid-feedback>
              </b-form-group>

              <!--Address input field for suburb-->
              <b-form-group>
                <b-form-input block
                              type="text"
                              name="suburb"
                              v-model="form.address.suburb"
                              placeholder="Suburb*"
                              :state="addressValidation.isNotInvalidField(submitted, form.address.suburb)"
                />
                <b-form-invalid-feedback
                    v-bind:class="{'yellow-error': addressValidation.containsSpecialChars(form.address.suburb)}"
                    :state="addressValidation.validField(submitted, form.address.suburb)">
                  {{ addressValidation.fieldMessage('Suburb', form.address.suburb) }}
                </b-form-invalid-feedback>
              </b-form-group>

              <!--Address input field for city-->
              <b-form-group>
                <b-form-input block
                              type="text"
                              name="city"
                              v-model="form.address.city"
                              placeholder="City*"
                              :state="addressValidation.isNotInvalidField(submitted, form.address.city)"
                />
                <b-form-invalid-feedback
                    v-bind:class="{'yellow-error': addressValidation.containsSpecialChars(form.address.city)}"
                    :state="addressValidation.validField(submitted, form.address.city)">
                  {{ addressValidation.fieldMessage('City', form.address.city) }}
                </b-form-invalid-feedback>
              </b-form-group>

              <!--Address input field for region-->
              <b-form-group>
                <b-form-input block
                              type="text"
                              name="region"
                              v-model="form.address.region"
                              placeholder="Region*"
                              :state="addressValidation.isNotInvalidField(submitted, form.address.region)"
                />
                <b-form-invalid-feedback
                    v-bind:class="{'yellow-error': addressValidation.containsSpecialChars(form.address.region)}"
                    :state="addressValidation.validField(submitted, form.address.region)">
                  {{ addressValidation.fieldMessage('Region', form.address.region) }}
                </b-form-invalid-feedback>
              </b-form-group>

              <!--Address input field for country-->
              <b-form-group>
                <b-form-input block
                              type="text"
                              name="country"
                              v-model="form.address.country"
                              placeholder="Country*"
                              :state="countryValidation()"
                />
                <b-form-invalid-feedback :state="countryValidation()">
                  {{ countryMessage() }}
                </b-form-invalid-feedback>
              </b-form-group>

              <!-- Input field for post code -->
              <div class="postcode">
                <b-form-input block
                              type="text"
                              name="postCode"
                              autocomplete="nope"
                              v-model="form.address.postcode"
                              placeholder="Postcode*"
                              :state="postCodeValidation()"/>
                <b-form-invalid-feedback :state="postCodeValidation()">
                  {{ postCodeMessage() }}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="postCodeValidation()"></b-form-valid-feedback>
              </div>
              <br/>
              <div class="bue-input">
                <b-form-textarea block
                                 type="text"
                                 name="bio"
                                 v-model="form.description"
                                 placeholder="Description of your business..."
                                 :state="descriptionNotTooLong()"/>
                <b-form-invalid-feedback :state="descriptionNotTooLong()">
                  Please do not exceed 500 characters.
                </b-form-invalid-feedback>
              </div>

              <br/>
              <div>
                <b-card-text id="underage-warning">(You must be 16 or older to create a new business)</b-card-text>
                <b-button block
                          type="submit"
                          name="submit"
                          variant="success"
                          v-on:click="submitted = true">Create New Business</b-button>
                <b-form-invalid-feedback :state="canCreateBusiness">
                  Cannot create business: You must be 16 or older to create a new business.
                </b-form-invalid-feedback>
                <b-form-invalid-feedback :state="fieldsValidated()">
                  Cannot create business: You must fill in all required fields correctly.
                </b-form-invalid-feedback>
                <b-form-invalid-feedback :state="noFailedRequests">
                  Unknown error
                </b-form-invalid-feedback>
              </div>
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
import StorageUtil from "../javascript_modules/storage_util";
import AddressValidation from '../validation/address.validation';
import BusinessValidation from '../validation/business.validation';
import Validation from '../validation/general.validation';

const BusinessSignup = {
  name: "BusinessSignup",
  data: function () {

    const now = new Date()
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
    const minDate = new Date(1900, 0, 1)

    return {
      min: minDate,
      max: today,
      submitted: false,
      form: {
        name: '',
        address: {
          streetNumber: '',
          streetName: '',
          suburb: '',
          city: '',
          region: '',
          country: '',
          postcode: ''
        },
        description: '',
        businessType: null,
      },
      show : true,

      suggestions: [],

      options: [
        {value: null, text: "Business Type*"},
        {value: "Accommodation and Food Services", text: "Accommodation and Food Services"},
        {value: "Retail Trade", text: "Retail Trade"},
        {value: "Charitable Organisation", text: "Charitable Organisation"},
        {value: "Non-Profit Organisation", text: "Non-Profit Organisation"}
      ],

      canCreateBusiness: undefined,
      noFailedRequests: true,

      addressValidation: AddressValidation
    }
  },
  methods: {

    createAddressObject(location) {
      return {
        streetNumber: (location.housenumber) ? location.housenumber : '',
        streetName: (location.street) ? location.street : '',
        suburb: (location.district) ? location.district : '',
        city: (location.city) ? location.city : '',
        region: (location.state) ? location.state : '',
        country: (location.country) ? location.country : '',
        postcode: (location.postcode) ? location.postcode : ''
      };
    },

    /**
     * Get address auto-complete result from api.
     * @param target the input from user.
     * @param searchType the field the user is inputting
     * @returns {Promise<void>}
     */
    searchApi: function (userInputAddress) {
      api.getAutoComplete(userInputAddress).then((response) => {
        let responseLocations = response.data.features
        this.suggestions = []
        let suggestionSet = new Set()

        for (let location of responseLocations) {
          let address = this.createAddressObject(location.properties);
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
      }).catch(
          this.suggestions = []
      )

    },

    fillAddress(address) {
      this.form.address.streetNumber = address.streetNumber
      this.form.address.streetName = address.streetName
      this.form.address.suburb = address.suburb
      this.form.address.city = address.city
      this.form.address.region = address.region
      this.form.address.country = address.country
      this.form.address.postcode = address.postcode

    },


    /** Calls business validation function */
    businessNameValidation() {
      return BusinessValidation.businessNameValidation(this.submitted, this.form.name);
    },

    /** Calls business validation function */
    businessNameInvalidMessage() {
      return BusinessValidation.businessNameInvalidMessage();
    },

    /** Calls general dropdown validation function */
    businessTypeValidation() {
      return Validation.validDropdown(this.submitted, this.form.businessType);
    },

    /** Calls business validation function */
    businessTypeInvalidMessage() {
      return BusinessValidation.businessTypeInvalidMessage();
    },

    /** Calls address validation function */
    streetNumberValidation() {
      return AddressValidation.streetNumber(this.submitted, this.form.address.streetNumber);
    },

    /** Calls address validation function */
    streetNumberInvalidMessage() {
      return AddressValidation.streetNumberMessage();
    },

    countryValidation() {
      return AddressValidation.countryValidation(this.submitted, this.form.address.country);
    },

    countryMessage() {
      return AddressValidation.countryMessage();
    },

    postCodeValidation() {
      return AddressValidation.postcode(this.submitted, this.form.address.postcode);
    },

    postCodeMessage() {
      return AddressValidation.postcodeMessage();
    },

    /**
     * Checks if all the fields have been validated to check if the create business field can be clicked
     *
     *  @returns{null|boolean}
     */
    fieldsValidated() {
      let fieldsValid = null;
      let detailsValidated = false;
      let addressValidated = false;
      let descriptionNotTooLong = null;

      if (this.businessNameValidation() && this.businessTypeValidation()) {
        detailsValidated = true;
      }

      if (this.streetNumberValidation()
          && AddressValidation.isNotInvalidField(this.submitted, this.form.address.streetName) !== false
          && AddressValidation.isNotInvalidField(this.submitted, this.form.address.suburb) !== false
          && AddressValidation.isNotInvalidField(this.submitted, this.form.address.city) !== false
          && AddressValidation.isNotInvalidField(this.submitted, this.form.address.region) !== false
          && this.countryValidation(this.form.address.country)
          && this.postCodeValidation(this.form.address.postcode)) {
        addressValidated = true;
      }
      if (this.descriptionNotTooLong() !== false) {
        descriptionNotTooLong = true;
      }

      if (detailsValidated && addressValidated && descriptionNotTooLong) {
        fieldsValid = true;
      } else if (this.submitted === true && (detailsValidated && addressValidated && descriptionNotTooLong) === false) {
        fieldsValid = false;
      }


      return fieldsValid;

    },

    /**
     * Method to check if the bio exceeds 500 characters
     *
     * @returns {null|boolean}
     */
    descriptionNotTooLong() {
      if (this.form.description.trim().length > 500) {
        return false;
      } else {
        return null;
      }
    },
    /**
     * Event handler: "submit" button is clicked.
     * Calls API to send a request and passes in the form contents as parameter.
     * If a 201 response status is returned, the user is taken to the business page via router.
     * If a non-201 status is returned, or an error is encountered during the request,
     * an appropriate message is sent and no further action is taken.
     */
    async onSubmit(event) {
      event.preventDefault();
      await this.calculateAge();
      if (this.canCreateBusiness && this.fieldsValidated()) {
        api.createBusiness(this.form).then((response) => {
          if (response.status === 201) {
            const id = response.data.businessId;
            this.$router.push({name: 'BusinessProfile', params: {id: id}});
          }
        }).catch(() => {
          this.noFailedRequests = false;
          event.preventDefault();
        });
      }
    },
    async calculateAge() {
      const now = new Date();
      let age;
      let dateOfBirth = await this.getDateOfBirth(StorageUtil.getCurrentUser());
      let ageDiff;
      if (dateOfBirth !== undefined) {
        ageDiff = new Date(now - dateOfBirth);
        age = Math.abs(ageDiff.getUTCFullYear()-1970);
      }
      this.canCreateBusiness = !((age !== undefined) && (age < 16));
    },
    getDateOfBirth(id) {
      return new Promise(function (resolve, reject) {
        api.getUser(id).then(userResponse => {
          if (userResponse.status === 200) {
            const dateOfBirth = new Date(userResponse.data.dateOfBirth);
            resolve(dateOfBirth);
          } else {
            this.noFailedRequests = false;
          }
        }).catch((err) => {
          this.noFailedRequests = false;
          reject(err);
        });
      });
    }
  },
  components: {

    // list your components here to register them (located under 'components' folder)
    // https://vuejs.org/v2/guide/components-registration.html
    AutoCompleteInput,

  },
  computed: {
  }
}

export default BusinessSignup;

</script>

<style scoped>
#edit-profile-pane {
  padding: 0 0 0 0 !important;
  margin: 5vh 0 5vh 0 !important;
}

.image-input {
  margin-left:15px;
  margin-top:20px;
  margin-bottom:25px;
}

#underage-warning {
  text-align: center;
}

.yellow-error {
  color: darkorange;
}

</style>