<template>
  <div id="notifications">
    <b-dropdown class="mr-1"
                right
                variant="outline-light"
                ref="notificationRef"
                id="notificationsDropDown">
      <template #button-content>
        <BIconBell v-if="seen"/>
        <BIconBellFill v-else/>
        {{totalUnread}}
      </template>
      <div id="notification-dropdown-item">
        <!--Display the notifications that the user has with a separator-->
        <div>
          <b-dropdown-text class="dropdown-text">Notifications: (Showing {{shownNotifications.length}} out of {{notifications.length}})</b-dropdown-text>
          <b-dropdown-divider/>
        </div>
        <div style="min-height: 25rem" class="notification-dropdown-item" v-if="shownNotifications.length !== 0">
          <div :id="item.id" href="#" v-for="item in shownNotifications" :key="item.id">
            <NotificationCard :notification="item" compact></NotificationCard>
          </div>
        </div>
        <b-dropdown-text v-if="shownNotifications.length === 0"> No notifications to display! </b-dropdown-text>
        <b-dropdown-divider/>
        <b-dropdown-text>
          <b-btn
              id="goToAllNotificationsBtn"
              class="dropdown-text"
              @click="goToNotificationView">
            View all notifications
          </b-btn>
        </b-dropdown-text>

      </div>
    </b-dropdown>
  </div>
</template>

<script>
import NotificationCard from "@/components/notifications/NotificationCard";
import storage_util from "../../javascript_modules/storage_util"
import {BIconBell, BIconBellFill} from 'bootstrap-vue'
import api from "../../Api";

/**
 * This component contains a button with a dropdown menu in order to display user notifications.
 * The button will change icon depending on all notifications have been seen or not.
 */
export default {
name: "NotificationDropDown",
  components: {
    NotificationCard,
    BIconBell,
    BIconBellFill
  },
  data() {
    return {
      notifications: [],
      shownNotifications: [],
      totalUnread: 0,
      seen: true,
    }
  },
  created() {
    setInterval(() => {
      this.getNotifications()
    }, 10000);
  },
  mounted() {
    this.getNotifications();
    this.$root.$on('notificationRead', ()=> {
      if (this.totalUnread) {
        this.totalUnread--;
      }
    });
  },
  methods: {
    /**
     * Loads in notifications to show and sets the number of unread
     * notifications.
     */
    getNotifications: async function () {
      try {
        let response = await api.getNotifications(storage_util.getCurrentUser())
        this.notifications = [];
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
          this.notifications.unshift(notification);
        }
      } catch {
        //Empty catch
      }

      this.shownNotifications = [];
      this.totalUnread = 0;

      for (let notification in this.notifications) {
        if (this.shownNotifications.length < 10) {
          this.shownNotifications.push(this.notifications[notification]);
        }
        if (!this.notifications[notification].read) {
          this.totalUnread ++;
        }
      }
      if (this.notifications.length > 10) {
        this.notifications.slice(0, 9);
      }
      this.seen = false
    },

    /**
     * This function is called when the view all notification button is pressed in the notification dropdown
     * It closes the dropdown and redirects to the notification page if not already on the page.
     */
    goToNotificationView: function () {
      this.$refs.notificationRef.hide()
      if (this.$route.name !== "Notifications") {
        this.$router.push({name: 'Notifications', params: {id: storage_util.getCurrentUser()}});
      }
    }
  }
}
</script>

<style scoped>

.notification-dropdown-item {
  height:200px;
  overflow-y:auto;
}

</style>