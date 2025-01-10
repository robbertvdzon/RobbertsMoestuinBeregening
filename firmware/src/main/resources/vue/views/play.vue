<template id="play">
    <app-frame>

        <div>


            <div class="headerBorder">
                <table width=300px>
                    <tr>
                        <td>Timer start:</td>
                        <td>
                          <input type="text" v-model="userdata.role" />
                        </td>
                    </tr>
                    <tr>
                        <td>Duration:</td>
                        <td>{{ userdata.role }}</td>
                    </tr>
                    <tr>
                        <td>Enable timer:</td>
                        <td>{{ userdata.role }}</td>
                    </tr>
                  <tr>
                    <td colspan="2">
                    <button v-on:click="dicht" class="button">Save</button>
                    </td>
                  </tr>
                </table>
            </div>
          <br>



          <div class="headerBorder">
            <table width=300px>
              <tr>
                <td>Timer sproeier:</td>
                <td>{{ tuin.displayData.time }}</td>
              </tr>
              <tr>
                <td>KlepState:</td>
                <td>{{ tuin.displayData.klepState }}</td>
              </tr>
              <tr>
                <td>Ip:</td>
                <td>{{ tuin.displayData.ip }}</td>
              </tr>
            </table>
          </div>


            <br>
            <span v-if="userdata.role!='ADMIN'">
                <button type="submit" v-on:click="login" class="button">Inloggen</button>
            </span>

            <span v-if="userdata.role=='ADMIN'">
            <hr>
                <button v-on:click="min10" class="button">-10</button>
                <button v-on:click="min1" class="button">-1</button>
                <button v-on:click="plus1" class="button">+1</button>
                <button v-on:click="plus10" class="button">+10</button>
              <br>
              <br>
                <button v-on:click="dicht" class="button">Dicht</button>
                <button v-on:click="reload" class="button">Reload</button>
                <button v-on:click="logout" class="button">Uitloggen</button>

       </span>

            <br>
        </div>
    </app-frame>
</template>

<style>


.button {
    border: none;
    background-color: white; /* Green */
    color: black;
    padding: 5px 32px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 16px;
}

</style>

<script>

Vue.component("play", {
    template: "#play",
    data: () => ({
        status: null,
        userdata: {
            username: "",
            role: ""
        },
        tuin: {
            klep: "",
            gisteren: ""
        },
        van: "",
        naar: "",
        possible_tos: [],
        testfield: {"s": "dd"},
    }),
    computed: {},

    created() {
        this.load()
    },
    methods: {
        load: function (event) {
            fetch(`/api/game/load`)
                .then(res => res.text())
                .then(text => this.updateTuin(JSON.parse(text)))
                .catch(err => alert(err));
            fetch(`/api/userdata`)
                .then(res => res.text())
                .then(text => this.userdata = JSON.parse(text))
                .catch(() => alert("Error"));
        },
        logout: function (event) {
            fetch(`/api/logout`)
                .catch(() => alert("Error"))
                .then(text => window.location.href = "/play");
        },
        login: function (event) {
            window.location.href = "/login";
        },
        demo: function (event) {
            window.location.href = "/demo";
        },
        manual: function (event) {
            window.location.href = "/manual";
        },
        updateTuin: function (newTuin) {
            this.tuin = newTuin;
        },
        reload: function () {
            fetch(`/api/game/reload`)
                .then(res => res.text())
                .then(text => this.updateTuin(JSON.parse(text)))
                .catch(() => alert("Error"));
        },
        plus1: function () {
            fetch(`/api/game/plus1`)
                .then(res => res.text())
                .then(text => this.updateTuin(JSON.parse(text)))
                .catch(() => alert("Error"));
        },
        plus10: function () {
            fetch(`/api/game/plus10`)
                .then(res => res.text())
                .then(text => this.updateTuin(JSON.parse(text)))
                .catch(() => alert("Error"));
        },
        min1: function () {
            fetch(`/api/game/min1`)
                .then(res => res.text())
                .then(text => this.updateTuin(JSON.parse(text)))
                .catch(() => alert("Error"));
        },
        min10: function () {
            fetch(`/api/game/min10`)
                .then(res => res.text())
                .then(text => this.updateTuin(JSON.parse(text)))
                .catch(() => alert("Error"));
        },
        dicht: function () {
            fetch(`/api/game/dicht`)
                .then(res => res.text())
                .then(text => this.updateTuin(JSON.parse(text)))
                .catch(() => alert("Error"));
        },
    }
});

$(document).ready(function () {
    $("#playPrev").click(function () {
        window.location.href = "/manual";
    });
    $("#playNext").click(function () {
        window.location.href = "/demo";
    });
});

</script>
<style scoped>
.headerBorder {
    color: white;
    background-color: #163d00;
    font-size: 13px;
}

.schaakmat {
    font-size: 38px;
}

.label {
    color: white;
}

</style>
