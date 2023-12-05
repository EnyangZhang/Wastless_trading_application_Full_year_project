<template>
  <b-modal ref="modal" id="createCardModal" title="Create Card" hide-footer>
    <div>
      <b-form id="createCardForm">
        <div id="section" class="p-col"><strong>Section*</strong><br/>
          <b-form-select id="sectionSelect"
                         autocomplete="off"
                         :options="sectionOptions"
                         :state="sectionValidation"
                         v-model="form.section"></b-form-select>
          <b-form-invalid-feedback>
            Please select a section.
          </b-form-invalid-feedback>
          <b-form-valid-feedback :state="sectionValidation"></b-form-valid-feedback>
        </div>
        <br/>
        <b-card id="formDialogue">
          <b-card-title>
            Title*
            <b-form-input id="formTitle"
                          v-model="form.title"
                          @keydown.enter="onSubmit"
                          :state="titleValidation"></b-form-input>
          </b-card-title>
          <b-form-invalid-feedback :state="titleValidation">
            Please enter a valid card title less than 32 characters.
          </b-form-invalid-feedback>
          <b-form-valid-feedback></b-form-valid-feedback>
          <b-card-body>
            <div id="cardInfoDialogue">

              <b-row>
                <!--Card Image-->
                <b-col id="card-image-container">
                  <img id="card-image"
                       alt="image"
                       :src="require('../../../public/default.png')"/>
                </b-col>
                <b-col>
                  <!--Creator Info-->
                  <b-card-text class="card-info" id="creator-info">
                    <b-card-sub-title>Created By</b-card-sub-title>
                    <view-user :user="currentUser"/>
                  </b-card-text>

                  <!--Creator Location Info-->
                  <b-card-text class="card-info" id="creator-location">
                    <b-card-sub-title>Location</b-card-sub-title>
                    <formatted-address :address="userLocation"/>
                  </b-card-text>
                </b-col>
              </b-row>

              <!--Card description-->
              <div class="p-grid">
                <div id="description" class="p-col"><strong>Info</strong><br/>
                  <b-form-textarea id="formDescription"
                                   :state="descriptionValidation"
                                   v-model="form.description"></b-form-textarea>
                  <b-form-invalid-feedback :state="descriptionValidation">
                    Please enter a valid card description less than 250 characters.
                  </b-form-invalid-feedback>
                </div>
              </div>

              <!--Card Keywords-->
              <div class="p-grid">
                <div id="keywords" @keydown.enter="onSubmit" class="p-col"><strong>Keywords</strong>
                  <div class="top-row">
                    <MultiSelect v-model="selectedKeywords"
                                 id="keyword-select"
                                 name="MultiSelect"
                                 :options="keywords"
                                 optionLabel="name"
                                 :filter="true"
                                 v-bind:class="{multiselectCustom: MultiSelectEmpty, multiSelectValid: keywordValidation}"
                                 placeholder="Select Keywords"
                                 display="chip">
                      <template #value="slotProps">
                        <template v-if="!slotProps.value || slotProps.value.length === 0">
                          Select Keywords
                        </template>
                      </template>
                      <template #option="slotProps">
                        <div>
                          <div>{{slotProps.option.name}}</div>
                        </div>
                      </template>
                    </MultiSelect>
                  </div>
                </div>


              </div>
            </div>
          </b-card-body>

          <div id="buttons" class="row">
            <div class="col">
              <b-button block
                        id="cancelButton"
                        name="cancel"
                        @click="$bvModal.hide('createCardModal')">Cancel</b-button>
            </div>
            <div class="col">
              <b-button block
                        id="submitButton"
                        name="submit"
                        @click="onSubmit"
                        :disabled="canSubmit() === false"
                        variant="success">Create Card</b-button>
            </div>
          </div>

        </b-card>
      </b-form>
    </div>
  </b-modal>
</template>

<script>
import api from "@/Api";
import validation_util from "@/javascript_modules/validation_util";
import ViewUser from "@/components/ViewUser";
import FormattedAddress from "@/components/address/FormattedAddress";
import storage_util from "../../javascript_modules/storage_util";

export default {
  name: "CreateCardModal",
  components: {FormattedAddress, ViewUser},
  data() {
    return {
      currentUser: undefined,
      userLocation: undefined,

      cardKeywordString: '',

      keywordOptions: [
        'Brand New', 'Old', 'Homegrown', 'Fresh'
      ],

      sectionOptions: [
        'For Sale', 'Exchange', 'Wanted'
      ],

      createCardClicked: false,
      MultiSelectEmpty: true,

      form: {
        creatorId: null,
        section: '',
        title: '',
        description: '',
        keywordIds: [],
      },


      selectedKeywords: [],

      // Three hardcoded keywords since we don't have keywords stored at the backend.
      keywords: [
        {name: 'Selling', id: 1},
        {name: 'Swapping', id: 2},
        {name: 'Food', id: 3},
      ],

      submitted: false
    }
  },
  async mounted() {
    await this.setValues();
  },
  methods: {
    /**
     * Shows the dialog.
     */
    show() {
      this.$refs.modal.show();
      this.form = {
        creatorId: null,
            section: '',
            title: '',
            description: '',
            keywordIds: [],
      }
      this.selectedKeywords = [];
      this.submitted = false;
      this.createCardClicked = false;
      this.setValues();
    },

    /**
     * Sets non-user changeable values in the form.
     * @returns {Promise<void>}
     */
    async setValues() {
      this.form.creatorId = storage_util.getCurrentUser();
      this.currentUser = await storage_util.getCurrentUserInfo();
      this.setUserLocation();
    },

    /**
     * Sets the user location for the address formatter to use while on the create card dialogue
     */
    setUserLocation () {
      this.userLocation = {suburb: this.currentUser.homeAddress.suburb, city: this.currentUser.homeAddress.city}
    },

    /**
     * Get the keyword ids into a list from the list of selected keywords in the form multiselect.
     * Selected keywords contains a list of keyword objects that are currently selected.
     */
    getKeywordIds() {
      this.form.keywordIds = []
      for (let i = 0; i < Object.keys(this.selectedKeywords).length; i++){
        this.form.keywordIds.push(this.selectedKeywords[i].id)
      }
    },

    /**
     * Sends post request to the server if all fields are validated
     * Checks if card has valid data. Also checks if the submitted value is false.
     * If the submitted value is true, then it will not proceed to send the form through the API.
     *
     * the proceed value is set to false again once the API request is completed.
     */
    onSubmit(event) {
      this.createCardClicked = true;
      this.getKeywordIds();
      if (this.canSubmit() && !this.submitted) {
        this.submitted = true;
        let marketplaceCard = Object.assign({}, this.form)
        api.addMarketplaceCard(marketplaceCard)
            .then(() => {
              this.errorMessage = "";
              this.submitted = false;
              this.$bvToast.toast(`Card created in ${this.form.section}`, {
                title: 'Card Created Successfully',
                variant: 'success',
                toaster: 'b-toaster-bottom-right',
                autoHideDelay: 5000,
                appendToast: true
              });
              this.$refs.modal.hide();
              this.$emit("createdCard");
            })
            .catch((error) => {
              if (error.response !== undefined) {
                if (error.response.status === 400) {
                  this.errorMessage = error.response.data.message;
                }
                event.preventDefault();
                this.submitted = false;
              }
            });
      } else {
        event.preventDefault();
      }
    },

    /**
     * Method that checks if all fields are valid and if so allows the user to submit the form
     * @returns {boolean}
     */
    canSubmit() {
      if (this.sectionValidation && this.titleValidation) {
        if (this.descriptionValidation !== false && this.keywordValidation !== false) {
          return true;
        }
      } else {
        return false;
      }

    }
  },
  computed: {
    /**
     * Computed property to set valid/invalid state of card section input on create card form
     * @returns {null|boolean}
     */
    sectionValidation() {
      if (this.createCardClicked || this.form.section) {
        return validation_util.isFormSelectValid(this.form.section);
      } else {
        return null;
      }
    },

    /**
     * Computed property to set valid/invalid state of card title input on create card form
     * @returns {null|boolean}
     */
    titleValidation() {
      if (this.createCardClicked || this.form.title) {
        return validation_util.isCardTitleValid(this.form.title);
      } else {
        return null;
      }
    },

    /**
     * Computed property to set valid/invalid state of card description input on create card form
     * @returns {null|boolean}
     */
    descriptionValidation() {
      return validation_util.isNRDescriptionValid(this.form.description);
    },

    /**
     * Computed property to set valid/no state of card keywords multiselect option on create card form
     * @returns {null|boolean}
     */
    keywordValidation() {
      if( validation_util.isMultiSelectValid(this.selectedKeywords)) {
        return true;
      } else {
        return null;
      }
    }
  }
}
</script>

<style scoped>
/*Class styles*/
.card-info {
  background-color: white;
  padding: 10px 7px;
}

#card-image {
  width: 100%;
  height: 100%;
  max-width: 250px;
  max-height: 200px;
}

#card-image-container {
  width: 250px;
  height: 200px;
  margin-bottom: 10px;
  text-align: center;
}

.p-multiselect {
  width: 100%;
}

.multiSelectValid {
  width: 100%;
  border-color: forestgreen;
}

</style>