<!--This file deals with the search users page. Search results are presented in a table format-->
<template>
  <div>
    <b-card id="resultsCard"
            fluid="true">
      <div class="card-header d-flex justify-content-between align-items-center">
        <b-form inline
                @submit.prevent="doNothing">
          <b-form-input id="searchField"
                        class="mr-sm-2"
                        v-model="search"
                        type="text"
                        name="searchField"
                        :state="queryEntered"
                        @keydown.enter="searchUsers"
                        placeholder="Search Users"></b-form-input>
          <b-button class="mr-1"
                    variant="success"
                    @click=searchUsers>Search</b-button>
          Displaying results {{ resultMin }} to {{ resultMax }} of {{ numItems }}
        </b-form>
        <div class="btn-group" role="group">
          <paginator :page=currentPage
                     :items-length=totalItems
                     :per-page=perPage
                     :key="refreshPage"
                     @changePage="changePage"/>
        </div>
      </div>
      <b-table hover
               ref="table"
               :responsive="true"
               :items="items"
               :fields="filteredFields"
               primary-key="id"
               sort-icon-left
               :per-page="perPage"
               no-local-sorting
               @sort-changed="sortingChanged"
               @row-clicked="rowClickHandler">
      </b-table>
    </b-card>
    <b-modal ref="view-profile-modal"
             ok-title="Save"
             :hide-footer="disableAdminButtons()"
             :title="viewProfileTitle"
             @ok="saveChanges"
             @cancel="cancelChanges"
             ok-variant="success">
      <b-form-group label-cols = "3"
                    label="First name: "
                    label-for="input-horizontal">
        <b-form-input v-model="selectedUser.fName" :disabled="true" class="white-background"></b-form-input>
      </b-form-group>
      <b-form-group label-cols = "3"
                    label="Last name: "
                    label-for="input-horizontal">
        <b-form-input v-model="selectedUser.lName" :disabled="true" class="white-background"></b-form-input>
      </b-form-group>
      <b-form-group label-cols = "3"
                    label="Middle name: "
                    label-for="input-horizontal">
        <b-form-input v-model="selectedUser.mName" :disabled="true" class="white-background"></b-form-input>
      </b-form-group>
      <b-form-group label-cols = "3"
                    label="Nickname: "
                    label-for="input-horizontal">
        <b-form-input v-model="selectedUser.nName" :disabled="true" class="white-background"></b-form-input>
      </b-form-group>
      <b-form-group label-cols = "3"
                    label="Bio: "
                    label-for="input-horizontal">
        <b-form-textarea no-auto-shrink
                         no-resize
                         rows="6"
                         v-model="selectedUser.bio"
                         :disabled="true"
                         class="white-background">
        </b-form-textarea>
      </b-form-group>
      <b-form-group label-cols = "3"
                    label="Email: "
                    label-for="input-horizontal">
        <b-form-input v-model="selectedUser.email" :disabled="true" class="white-background"></b-form-input>
      </b-form-group>
      <b-form-group label-cols = "3"
                    label="Member since: "
                    label-for="input-horizontal">
        <b-form-input v-model="selectedUser.memberDate" :disabled="true" class="white-background"></b-form-input>
      </b-form-group>
      <b-form-group label-cols = "3"
                    label="Admin: "
                    v-if="!disableAdminCheckbox()"
                    label-for="input-horizontal">
        <b-form-checkbox v-model="selectedUser.isAdmin"
                         :disabled="disableAdminCheckbox()"
                         class="checkbox">
          {{adminMessage}}
        </b-form-checkbox>
      </b-form-group>

      <b-form-group>
        <b-dropdown id="dropdown-header" text="View Businesses Administered" variant="success" class="m-2">
          <b-dropdown-header id="dropdown-header-label">
            Select business that you want to view
          </b-dropdown-header>
          <b-dropdown-item :id="item.data" href="#" v-for="item in options" :key="item.data" @click="toBusinessListings(item.data)">
            {{ item.text }}
          </b-dropdown-item>
        </b-dropdown>
      </b-form-group >

      <!--Link to user's marketplace cards-->
      <div id="user-cards">
        <b-button id="to-marketplace-cards-btn"
                  class="m-2"
                  variant="success"
                  @click="$router.push(`/user-cards/${selectedUser.userId}`)">
          View Marketplace Cards
        </b-button>
      </div>

      <template #modal-footer>
        <!-- Remove administrator button -->
          <b-button variant="danger"
                    v-if="canRemoveBusinessAdmin()"
                    @click="removeBusinessAdmin">Remove Business Admin</b-button>
          <!-- Add business administrator button-->
          <b-button variant="success"
                    v-if="canAddBusinessAdmin()"
                    @click="addAsBusinessAdmin">Add as Business Admin</b-button>
          <!-- Cancel changes button -->
          <b-button @click="cancelChanges">Cancel</b-button>
          <!-- Save changes button -->
          <b-button variant="success" @click="saveChanges">Save</b-button>
      </template>
    </b-modal>
  </div>
</template>

<script>

import api from "../Api";
import {timeFormat} from "@/javascript_modules/user_profile";
import StorageUtil from "../javascript_modules/storage_util";
import Paginator from "@/components/Paginator";

const SearchUsers = {
  components: {Paginator},
  data() {
    return {
      search: "",          // search query entered
      currentSearch: "",
      queryEntered: null,  // state of the search box (if 'false' the search box will be highlighted in red)
      currentPage: 1,      // current page of search results
      perPage: 10, // number of search results displayed per page
      refreshPage: 1,
      totalItems: 0,

      sortOptions:{nickname: 'nickname', firstName: 'firstName', middleName: 'middleName', lastName: 'lastName', email: 'email', isAdmin: "role"}, //Sorting options

      orderOptions: {asc: 'asc', desc: 'desc'}, //Ordering options

      sortBy: "default",
      orderBy: "asc",

      searched: false,

      items: [],           // variable for the search results that will be displayed in the table
      fields: [            // variable for the columns in the search results table
        {key: 'nickname', sortable: true},
        {key: 'firstName', sortable: true},
        {key: 'middleName', sortable: true},
        {key: 'lastName', sortable: true},
        {key: 'email', sortable: true},
        {key: 'isAdmin', sortable: true, requiresAdmin: true}
      ],
      filteredFields: [], // variable for columns in search table filtered for if current user is admin
                          // this is set in the mounted method.

      // Profile attributes for the selected user:
      selectedUser: {
        userId: 0,
        fName: "",
        lName: "",
        mName: "",
        nName: "",
        bio: "",
        email: "",
        memberDate: "",
        role: "",
        isAdmin: false,
        savedIsAdmin: false,
        businessesAdministered: []
      },

      // Profile attributes of the currently logged in user
      currentUser: {
        userId: null,
        fName: "",
        lName: "",
        mName: "",
        nName: "",
        bio: "",
        email: "",
        memberDate: "",
        dateOfBirth: "",
        phoneNum: null,
        homeAddr: "",
        role: "",
        businessesAdministered: [],
      },

      // Adding or removing business admin
      targetUser: {
        userId: null
      },

      // Message displayed next to is admin checkbox
      adminMessage: "",

      options: []
    }
  },


  /**
   * Mounted function is called when the page is loaded. It sets the attributes for the currently logged in user.
   */
  mounted: function () {
    window.addEventListener(StorageUtil.SWITCHED_ACCOUNT_EVENT, () => {
      this.getCurrentUserInfo()
    })
    this.getCurrentUserInfo();
  },


  computed: {

    /** Computes the value of the modal title for viewing a profile */
    viewProfileTitle() {
      return `${this.selectedUser.fName}'s Profile`;
    },

    /** This function us for input into the message: "Displaying results {min} to {max} of {total number}" */
    resultMin() {
      if (this.totalItems > 0) {
        return (this.currentPage - 1) * this.perPage + 1;
      }
      return 0;
    },

    /** This function us for input into the message: "Displaying results {min} to {max} of {total number}" */
    resultMax() {
      let maxPossibleOnPage = this.currentPage * this.perPage;
      if (maxPossibleOnPage > this.totalItems) {
        return this.totalItems;
      } else {
        return maxPossibleOnPage;
      }
    },

    /** This function us for input into the message: "Displaying results {min} to {max} of {total number}" */
    numItems() {
      return this.totalItems;
    }
  },
  methods: {

    /**
     * Returns the fields to be displayed depending if the current user is a DGAA or not.
     */
    computeFields() {
      let fields;
      // If the user isn't an admin, filter out fields that require auth.
      if (this.currentUser.role !== 'default_global_admin') {
        fields = this.fields.filter(function (field) {
          return !field.requiresAdmin
        });
      }
      // If the user IS an admin, return all fields.
      else {
        fields = this.fields;
      }
      return fields;
    },

    /**
     * This function stops the page from reloading when you press the enter key for search
     */
    doNothing: function() {
      // Does nothing
    },

    isAdmin: function (role) {
      if (role === 'global_admin' || role === 'default_global_admin') {
        return 'Yes';
      } else {
        return 'No';
      }
    },

    /**
     * This function triggered by search button. It allow the paginator to be refreshed when user starts a new search. It
     * also helps to remain the previous search result if user deletes the search input. It detects if the user tries to
     * search an empty input.
     */
    searchUsers: function () {
      if (this.search.length > 0){
        this.currentSearch = this.search;
        this.currentPage = 1;
        this.refreshPage = !this.refreshPage;
        this.getUsers()
        this.queryEntered = null;
      } else {
        this.queryEntered = false;
      }
    },

    /**
     * Request all users that match the given search query from the server. Initially results will be displayed in the
     * order that they are received in from the server, so it is important that the server sends results in a sensible
     * order. this.items will be set to the items received from the server.
     */
     getUsers: function () {
       api.searchUsers(this.currentSearch, this.currentPage, this.perPage, this.sortBy, this.orderBy)
            .then((response) => {
              for (let item of response.data) {
                item['isAdmin'] = this.isAdmin(item.role);
              }
              this.items = response.data;
              this.queryEntered = null;
              this.totalItems = Number(response.headers["total-length"]);
            })
        this.searched = true
    },

    /**
     * This function detects what status of the sorting and ordering options. The sorting will be only triggered when user
     * has already input search query to search bar.
     * ctx.sortBy Field key for sorting by (or null for no sorting).
     * ctx.sortDesc true if sorting descending, false otherwise.
     */
    sortingChanged: function (ctx){
      this.sortBy = this.sortOptions[ctx.sortBy]
      if (ctx.sortDesc) {
        this.orderBy = this.orderOptions.desc
      } else {
        this.orderBy = this.orderOptions.asc
      }

      if (this.searched) {
        this.getUsers()
      }
    },

    /**
     * This function returns the message that will be displayed next to the is admin checkbox of the currently selected
     * user
     */
    getAdminMessage: function () {
      if (this.selectedUser.role === 'default_global_admin') {
        return "This user is the default global administrator. You cannot disable admin permissions.";
      } else {
        return "Application administrators can view and edit any information in the application.";
      }
    },

    /** This method is called when a search result row is clicked on. */
    // eslint-disable-next-line no-unused-vars
    rowClickHandler: function (record, index) {
      this.selectedUser.userId = record.id
      this.selectedUser.fName = record.firstName;
      this.selectedUser.lName = record.lastName;
      this.selectedUser.mName = record.middleName;
      this.selectedUser.nName = record.nickname;
      this.selectedUser.bio = record.bio;
      this.selectedUser.email = record.email;
      this.selectedUser.memberDate = timeFormat(record.created);
      this.selectedUser.role = record.role;
      this.selectedUser.isAdmin = record.role === 'global_admin' || record.role === 'default_global_admin';
      this.selectedUser.savedIsAdmin = this.selectedUser.isAdmin;
      this.selectedUser.businessesAdministered = record.businessesAdministered;
      this.adminMessage = this.getAdminMessage();
      this.setSelectedBusinesses()
      this.$refs['view-profile-modal'].show();
    },

    /**
     * Updates the current page to control the results displayed in the table. It calls api to get contains for next or
     * previous page.
     */
    changePage(page) {
      this.currentPage = page;
      this.getUsers();
    },

    /**
     * Returns true if the current user is the DGAA AND the user they are viewing is not the DGAA.
     * Otherwise returns false.
     */
    disableAdminCheckbox: function () {
      let record_is_dgaa = (this.selectedUser.role === 'default_global_admin');
      let current_user_is_dgaa = (this.currentUser.role === 'default_global_admin');
      return (!current_user_is_dgaa) || record_is_dgaa;
    },

    disableAdminButtons: function () {
      let record_is_dgaa = (this.selectedUser.role === 'default_global_admin');
      let current_user_is_dgaa = (this.currentUser.role === 'default_global_admin');
      let acting_as_business = (StorageUtil.getActingAs() !== 'ACTING_AS_CURRENT_USER');
      return (!current_user_is_dgaa && !acting_as_business) || record_is_dgaa;
    },

    /** This function updated the values of the currently logged in user's profile attributes */
    getCurrentUserInfo: function () {
      api.getUser(StorageUtil.getCurrentUser())
          .then((response) => {
            let userInfo = response.data;
            this.currentUser.userId = userInfo.userId;
            this.currentUser.fName = userInfo.firstName;
            this.currentUser.lName = userInfo.lastName;
            this.currentUser.mName = userInfo.middleName;
            this.currentUser.nName = userInfo.nickname;
            this.currentUser.bio = userInfo.bio;
            this.currentUser.email = userInfo.email;
            this.currentUser.dateOfBirth = userInfo.dateOfBirth;
            this.currentUser.phoneNum = userInfo.phoneNumber;
            this.currentUser.homeAddr = userInfo.homeAddress;
            this.currentUser.memberDate = timeFormat(userInfo.created);
            this.currentUser.role = userInfo.role;
            this.currentUser.businessesAdministered = userInfo.businessesAdministered;
            this.currentUser.isAdmin = userInfo.role === 'global_admin' || userInfo.role === 'default_global_admin';

            this.filteredFields = this.computeFields()
          })
          .catch(() => {
            this.currentUser.isAdmin = false;
          })
    },

    /** Resets isAdmin data, and closes the dialog */
    cancelChanges: function () {
      this.selectedUser.isAdmin = this.selectedUser.savedIsAdmin;
      this.$refs['view-profile-modal'].hide();
    },

    /**
     * If the admin status of the user has changed, send the appropriate request to the backend.
     */
    saveChanges: function () {
      let savedIsAdmin = this.selectedUser.savedIsAdmin;
      let isAdmin = this.selectedUser.isAdmin;
      if (savedIsAdmin !== isAdmin && isAdmin) {
        this.makeAdmin();
      } else if (savedIsAdmin !== isAdmin && !isAdmin) {
        this.revokeAdmin();
      }
      this.$refs['view-profile-modal'].hide();
    },

    /**
     * Send make admin request to backend and update the user's admin status in the current list of search results.
     */
    makeAdmin: function () {
      api.makeadmin(this.selectedUser.userId)
          .then((response) => {
            if (response.status === 200) {
              for (let record of this.items) {
                if (record.id === this.selectedUser.userId) {
                  record.isAdmin = 'Yes';
                  record.role = 'global_admin';
                }
              }
            } else {
              this.selectedUser.isAdmin = this.selectedUser.savedIsAdmin;
            }
          })
          .catch(() => {
            this.selectedUser.isAdmin = this.selectedUser.savedIsAdmin;
          })
    },

    /**
     * Send revoke admin request to backend and update the user's admin status in the current list of search results.
     */
    revokeAdmin: function () {
      api.revokeadmin(this.selectedUser.userId)
          .then((response) => {
            if (response.status === 200) {
              for (let record of this.items) {
                if (record.id === this.selectedUser.userId) {
                  record.isAdmin = 'No';
                  record.role = 'user';
                }
              }
            } else {
              this.selectedUser.isAdmin = this.selectedUser.savedIsAdmin;
            }
          })
          .catch(() => {
            this.selectedUser.isAdmin = this.selectedUser.savedIsAdmin;
          })
    },

    canAddBusinessAdmin: function () {
      let acting_as_business = true;
      let actingAs = StorageUtil.getActingAs();
      let businesses_administered = this.selectedUser.businessesAdministered;
      let userIsPrimaryAdminOfThisBusiness = false;
      let userIsAdminOfThisBusiness = false;
      if (actingAs === 'ACTING_AS_CURRENT_USER') {
        acting_as_business = false;
      }
      businesses_administered.forEach((business) => {
        if (business.businessId === parseInt(actingAs) && business.primaryAdminId === this.selectedUser.userId) {
          userIsPrimaryAdminOfThisBusiness = true;
        }
        if (business.businessId === parseInt(actingAs)) {
          userIsAdminOfThisBusiness = true;
        }
      });
      return (acting_as_business) && !userIsPrimaryAdminOfThisBusiness && !userIsAdminOfThisBusiness && this.selectedUser.userId !== StorageUtil.getCurrentUser();
    },

    canRemoveBusinessAdmin: function () {
      let acting_as_business = true;
      let actingAs = StorageUtil.getActingAs();
      let userIsPrimaryAdminOfThisBusiness = false;
      let userIsAdminOfThisBusiness = false;
      let businesses_administered = this.selectedUser.businessesAdministered;
      if (actingAs === 'ACTING_AS_CURRENT_USER') {
        acting_as_business = false;
      }
      businesses_administered.forEach((business) => {
        if (business.businessId === parseInt(actingAs) && business.primaryAdminId === this.selectedUser.userId) {
          userIsPrimaryAdminOfThisBusiness = true;
        }
        if (business.businessId === parseInt(actingAs)) {
          userIsAdminOfThisBusiness = true;
        }
      });
      return (acting_as_business) && !userIsPrimaryAdminOfThisBusiness && userIsAdminOfThisBusiness && this.selectedUser.userId !== StorageUtil.getCurrentUser();
    },

    addAsBusinessAdmin: function () {
      this.targetUser.userId = this.selectedUser.userId;
      api.addAsBusinessAdmin(StorageUtil.getActingAs(), this.targetUser).then((response) => {
        if (response.status === 200) {
          this.$refs['view-profile-modal'].hide();
          this.getUsers();
        }
      }).catch();
    },

    removeBusinessAdmin: function () {
      this.targetUser.userId = this.selectedUser.userId;
      api.removeBusinessAdmin(StorageUtil.getActingAs(), this.targetUser).then((response) => {
        if (response.status === 200) {
          this.$refs['view-profile-modal'].hide();
          this.getUsers();
        }
      }).catch();
    },
    toBusinessListings: function (businessId) {
      const id = businessId;
      const path = `/business/${id}/listings`;
      if (id !== undefined && this.$route.path !== path) {
        this.$router.push({name: 'BusinessProfile', params: {id: id}})
      }
    },

    setSelectedBusinesses: function () {
      // Gets current user
      let businesses = this.selectedUser.businessesAdministered;

      // Creates list of businesses to act as
      this.options = []
      for (let business of businesses) {
        this.options.push({
          text: business.name,
          data: business.businessId
        });
      }
    }
  }
};

export default SearchUsers;

</script>