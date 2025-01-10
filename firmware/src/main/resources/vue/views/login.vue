<template id="login">
  <app-frame>

    <span>
      <span class="loginBorder">
          <br>
          &nbsp;access code:
      </span>
      <br>
      <input v-on:keyup.enter="login" v-model="accesscode" autofocus>
      <button type="submit" v-on:click="login" class="button">Login</button>
    </span>


  </app-frame>
</template>
<script>
Vue.component("login", {
  template: "#login",
  data: () => ({
    accesscode: "",
  }),
  created() {
    this.load()
  },
  methods: {
    load: function (event) {
    },
    login: function (event) {
      axios.post(`/api/login`, this.accesscode)
          .then(text => window.location.href = "/play")
          .catch(() => alert("Error"));
    },
  }
});

</script>
<style scoped>


.loginBorder {
  color: white;
}


</style>
