<template>
  <div id="keyword-badge">
    <div v-if="keywords && keywords.length > 0">
      <b-badge variant="success"
               class="keyword-badge"
               id="keyword"
               v-bind:key="keyword.id"
               v-for="keyword in keywords">
        {{keyword.name}}
      </b-badge>
    </div>

    <div v-else>
      <b-badge variant="light">No Keywords</b-badge>
    </div>
  </div>
</template>

<script>
export default {
  name: "Keywords",
  props: {
    keywords: Array,
  }
}
</script>

<style scoped>

.keyword-badge {
  padding: 5px;
  margin: 2px;
}

</style>