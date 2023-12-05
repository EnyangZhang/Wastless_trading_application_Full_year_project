<template>
  <div id="app">
    <div id="banner">
      <NavigationBar/>
    </div>
    <div id="content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import NavigationBar from "./components/NavigationBar";
// Vue app instance
// it is declared as a reusable component in this case.
// For global instance https://vuejs.org/v2/guide/instance.html
// For comparison: https://stackoverflow.com/questions/48727863/vue-export-default-vs-new-vue

const app = {
  name: "app",
  components: {
    // list your components here to register them (located under 'components' folder)
    // https://vuejs.org/v2/guide/components-registration.html
    NavigationBar,
  },
  // app initial state
  // https://vuejs.org/v2/guide/instance.html#Data-and-Methods
  data: () => {
    return {};
  },
};

// make the 'app' available
export default app;
</script>

<style>

html, body {
margin: 0 !important;
padding: 0 !important;
width: 90vw;
}

#app {
  width: 100vw;
  height: 100vh;
  overflow-x:hidden;
  background-color: whitesmoke;
}
</style>