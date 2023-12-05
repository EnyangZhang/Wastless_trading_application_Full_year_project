<!-- This file deals with the login screen that non-logged in users will be presented with -->

<template>

  <div id="login">
    <!-- rows and columns are used here to centre the login panel -->
    <b-container fluid="true">
      <b-row class="row justify-content-center mb-2">
        <b-col cols="auto"
               align-self="center">
        </b-col>
      </b-row>
      <b-row class="row justify-content-center">
        <b-col md="5">
          <b-img fluid center :src="require('../../public/logo_square_shadow.png')" rounded="circle" class="my-3" style="height: 25%"/>
          <b-card id="login-pane">
            <b-form-group valid-feedback=""
                          :invalid-feedback="error"
                          :state="state">
              <b-form-input block
                            type="email"
                            name="email"
                            v-model="email"
                            placeholder="Email" />
              <br/>
              <b-form-input type="password"
                            name="password"
                            v-model="password"
                            @keydown.enter="login"
                            placeholder="Password" />
              <br/>
              <b-button block
                        id="loginButton"
                        @click=login
                        type="submit"
                        name="submit"
                        variant="success">Log In</b-button>
              <br/>
            </b-form-group>
            <b-button block
                      @click=signup
                      type="button"
                      name="register"
                      variant="outline-success">Create New Account</b-button>
          </b-card>
        </b-col>
      </b-row>
    </b-container>
  </div>

</template>


<script>

import api from "../Api";

const Login = {
  name: "Login",
  data: function () {
    return {
      email: "",
      password: "",
      error: ""
    };
  },

  computed: {
    // state should return false if an error message needs to be displayed.
    state () {
      return this.error.length === 0;
    },
  },

  methods: {
    /** Calls API login function, then if login is successful should go to the profile page, if unsuccessful it will
     * display an error message */
    login: function () {
      api.login(this.email, this.password).then((response) => {
          if (response.status === 200) {
            this.$root.$emit('login');
            this.$router.push({name: 'HomePage'});
            this.resetLoginForm();
          } else if (response.status === 401) {
            this.error = "The email or password that you've entered is incorrect.";
          } else {
            this.error = "Unknown error";
          }
      }).catch(() => {
        this.error = "Unknown error";
      });
    },
    /**
     * Switch page to sign up page.
     * Clear the login fields as well
     */
    signup: function () {
      this.resetLoginForm();
      this.$router.push("/signup");
    },

    resetLoginForm: function() {
      this.email = '';
      this.password = '';
      this.error = ""
    }
  }
}

export default Login;

</script>


<style scoped>

[v-cloak] {
  display: none;
}

</style>