<template>
  <div id="user"
       @click="handleClick"
       @mouseover="handleHover"
       @mouseleave="handleHover"
       :style="hoverStyle">
    <b-avatar id="avatar"
              class="mr-3"
              square
              variant="primary"/>
    <span id="name" class="mr-auto">{{user.firstName}} {{user.lastName}}</span>
    <b-badge id="primaryBadge" v-show="isPrimaryAdmin" variant="primary" pill>Primary</b-badge>
  </div>
</template>

<script>
export default {
  name: "User",
  props: {
    user: Object,
    isPrimaryAdmin: Boolean
  },
  data() {
    return {
      isHovered: false
    }
  },
  methods: {
    handleClick() {
      this.$router.push({name: "Profile", params: {id: this.user.id}})
    },
    handleHover(event) {
      event.type === "mouseover" ? this.isHovered = true : this.isHovered = false;
    }
  },
  computed: {
    hoverStyle() {
      if (this.isHovered) {
        return "background-color: lightgray;";
      }
      return "background-color: white;";
    }
  }
}
</script>

<style scoped>

#user {
  padding: 5px;
  user-select: none;
}

#primaryBadge {
  align-content: center;
  float: right;
  margin-top: 10px;
}

</style>