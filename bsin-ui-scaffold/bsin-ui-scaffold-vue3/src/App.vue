<template>
  <div id="app">
    <div id="react"></div>
    <router-view />
  </div>
</template>

<script>
export default {
  name: "app",
  created() {
    let a = document.getElementById("loading-wrap");
    a.style.display = "none";
  },
};
</script>


<style lang="less">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: #2c3e50;

    &.router-link-exact-active {
      color: #42b983;
    }
  }
}
</style>
