<template>
  <div>
    <b-container
        id="notificationContainer"
        fluid>
      <b-card class="full-height">
        <b-card-header>
          <b-card-title id="notificationViewTitle">
            View all notifications
          </b-card-title>
        </b-card-header>
        <b-card class="notifications" :id="item.id" href="#" v-for="item in notifications" :key="item.id">
          <NotificationCard :notification="item"/>
        </b-card>
        <b-card-footer v-if="showNoNotifications" id="noNotificationFooter">
          No notifications to display!
        </b-card-footer>
        <Paginator :itemsLength="totalPages"
                   :page="page"
                   :perPage="size"
                   @changePage="changePage($event)"/>
      </b-card>
    </b-container>
  </div>
</template>

<script>
import NotificationCard from "@/components/notifications/NotificationCard";
import Paginator from "../components/Paginator";
import api from "../Api";
import storage_util from "../javascript_modules/storage_util";

/**
 * This view holds all the notifications for a user. It is accessible via the notification dropdown in the navbar
 */
export default {
  name: "NotificationView.vue",
  components: {Paginator, NotificationCard},
  data() {
    return {
      notifications: [],
      totalPages: 0,
      page: 1,
      size: 9,
      polling: null
    }
  },
  created() {
    this.pollData();
  },
  beforeDestroy() {
    clearInterval(this.polling);
  },
  mounted() {
    this.getNotifications();
  },
  methods: {
    pollData() {
      this.polling = setInterval(() => {
        this.getNotifications()
      }, 10000);
    },

    /**
     * Loads notifications to display on the selected page
     */
    getNotifications: async function () {
      this.notifications = await this.getPageNotifications();
    },
    /**
     * Paginates the results from the backend
     */
    getPageNotifications: async function () {
      let allNotifications = [];
      try {
        let response = await api.getNotifications(storage_util.getCurrentUser())

        let userNotifications = response.data;
        for (let userNotification of userNotifications) {
          let notifId = userNotification.id;
          let notifIsRead = userNotification.read;
          let notifCreated = userNotification.created;
          let notifCategory = userNotification.category;
          const notifTitle = userNotification.title;
          const notifMessage = userNotification.message;
          const notifActionId = userNotification.actionId;

          let notification = {
            id: notifId,
            read: notifIsRead,
            created: notifCreated,
            category: notifCategory,
            title: notifTitle,
            message: notifMessage,
            actionId: notifActionId
          }
          allNotifications.unshift(notification);
        }
      } catch {
        //Empty catch
      }

      this.totalPages = allNotifications.length;
      let rtnNotifications = [];

      for (let i = (this.size * (this.page-1)); i < (this.size * this.page) && i < allNotifications.length; i++) {
        let notif = allNotifications[i];
        if (notif) {
          rtnNotifications.push(notif);
        }
      }
      return rtnNotifications;
    },
    /**
     * Function to handle page changes for the notifications
     * @param updatedPage
     */
    changePage(updatedPage) {
      this.page = updatedPage;
      this.getNotifications();
    },
  },
  computed: {
    /**
     * Computed method to see if the user has any notifications
     * Used by the b-card-footer to display a message if there are no notifications.
     * @returns {boolean}
     */
    showNoNotifications() {
      return this.notifications.length === 0
    }
  }
}
</script>

<style scoped>

.full-height {
  height: 90vh;
}

.notifications {
  margin-top: 5px;
  margin-bottom: 5px;
}

</style>