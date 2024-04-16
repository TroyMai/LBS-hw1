<template>
  <div style="display: flex; flex-direction: column; gap: 20px; margin: 10px; width: 500px">
    <div style="display: flex; gap: 10px;justify-content: space-around; align-items: center">
      <el-form-item label="ID">
        <el-input v-model="user.id" placeholder="Enter user id"></el-input>
      </el-form-item>
      <el-text type="primary" size="large" style="font-size: 30px">{{ winner }}</el-text>
    </div>
    <div class="shadow-box">
      <el-descriptions title="User Info" column="2" size="large">
        <el-descriptions-item label="Username">{{ user.username }}</el-descriptions-item>
        <el-descriptions-item label="Teamname">
          <el-tag
              :type="user.teamname === 'Team A' ? 'danger' : user.teamname === 'Team B' ? '' : 'success'"
              disable-transitions
          >{{ user.teamname }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="X">{{ user.x }}</el-descriptions-item>
        <el-descriptions-item label="Y">{{ user.y }}</el-descriptions-item>
        <el-descriptions-item label="Distance">{{ user.distance }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src

import request from "@/utils/request";

export default {
  name: 'HomeView',
  components: {},
  data() {
    return {
      user: {
        id: 1,
        username: "Eve",
        teamname: "Team A",
        distance: 30.479501308256342,
        x: -20,
        y: 23,
      },
      winner: 'no one win',
    }
  },
  mounted() {
    this.refreshInterval = setInterval(() => {
      this.findUser()
    }, 1000)
  },
  methods: {
    findUser() {
      if (this.user.id == null) {
        return
      }
      request.get(`/user/${this.user.id}`).then(res => {
        this.user = res
      })
      request.get(`/user/getWinner`).then(res => {
        this.winner = res
      })
    }
  }
}
</script>

<style scoped>
.shadow-box {
  border-radius: 5px;
  box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}
</style>
