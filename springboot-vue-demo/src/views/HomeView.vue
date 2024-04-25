<template>
  <div style="display:flex;flex-direction: column; margin:10px; gap:10px; text-align: center">
    <div style="display: flex;flex-direction: row; gap: 10px; height: 240%">
      <div style="flex: 1">
        <el-table :data="tableData" border stripe :default-sort="{prop: 'distance', order: 'ascending'}"
                  max-height="210">
          <el-table-column prop="id" label="ID" width="75" sortable/>
          <el-table-column prop="username" label="Username"/>
          <el-table-column
              prop="teamname"
              label="Teamname"
              :filters="[
        { text: 'Team A', value: 'Team A' },
        { text: 'Team B', value: 'Team B' },
        { text: 'Team C', value: 'Team C' },
      ]"
              :filter-method="filterTeam"
          >
            <template #default="scope">
              <el-tag
                  :type="scope.row.teamname === 'Team A' ? 'danger' : scope.row.teamname === 'Team B' ? '' : 'success'"
                  disable-transitions
              >{{ scope.row.teamname }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="x" label="X"/>
          <el-table-column prop="y" label="Y"/>
          <el-table-column prop="distance" label="Distance" sortable/>
        </el-table>
      </div>
      <div
          style="padding: 10px; width: 40%; height:210px; display: flex; flex-direction: column; gap: 10px;align-items: center; justify-content: space-between;border-radius: 5px; box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.1)">
        <el-button type="primary" size="large" style="width: 100%" @click="reset"
                   :disabled="!isFormComplete || gameStarted">Start New Game
        </el-button>
        <!--        <el-button type="primary" @click="refresh">Refresh</el-button>-->
        <el-form :model="gameData" :label-position="'top'" style="width: 100%" size="default">
          <el-form-item label="Game Name">
            <el-input v-model="gameData.gamename" placeholder="Enter game name" :disabled="gameStarted"></el-input>
          </el-form-item>
          <div style="display:flex; gap:10px; justify-content: space-between">
            <el-form-item label="X">
              <el-input v-model.number="gameData.x" placeholder="X coordinate" :disabled="gameStarted"></el-input>
            </el-form-item>
            <el-form-item label="Y">
              <el-input v-model.number="gameData.y" placeholder="Y coordinate" :disabled="gameStarted"></el-input>
            </el-form-item>
          </div>
        </el-form>
      </div>
    </div>
    <div style="height: 700px; display: flex; gap:10px;">
      <div style="width: 700px; height: 700px; border: 1px solid #ccc; position: relative">
        <el-text class="mx-1" type="primary"
                 style="position: absolute; top: 10%; left: 10%;align-content: center;font-size: 40px">{{
            winner
          }}
        </el-text>
        <canvas ref="canvas" style="width: 100%; height: 100%"></canvas>
      </div>
      <div style="flex: 1;height: 100%; overflow-y: auto; padding: 10px">
        <el-timeline>
          <el-timeline-item v-for="(game, index) in winnerlist"
                            :key="index"
                            :timestamp="game.time" center placement="top">
            <el-card>
              <h4>{{ game.gamename }} ({{ game.id }})</h4>
              <p>{{ game.teamname }} win!</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import request from "../../utils/request";
import SockJS from 'sockjs-client';
import {Stomp} from '@stomp/stompjs';

export default {
  name: 'HomeView',
  components: {},
  data() {
    return {
      stompClient: null,
      users: [],
      gameStarted: false,
      tableData: [],
      winner: '',
      refreshInterval: null,
      gameData: {
        gamename: null,
        x: null,
        y: null,
      },
      winnerlist: [],
    }
  },
  computed: {
    isFormComplete() {
      // 检查所有必需字段是否已填写
      return this.gameData.gamename && this.gameData.x !== null && this.gameData.y !== null;
    }
  },
  mounted() {
    this.load('user/find');
    this.loadlog();
    // this.refresh();
    this.refreshInterval = setInterval(() => {
      this.load('user/refresh')
    }, 1000)
    this.connect();
  },
  beforeDestroy() {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
  },

  methods: {
    connect() {
      const socket = new SockJS('http://localhost:9090/websocket'); // 根据实际后端部署调整 URL
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect({}, () => {
        this.onConnected();
      }, error => {
        console.error('Connection error:', error);
      });
    },
    onConnected() {
      // // 订阅服务器发送的用户列表
      // this.stompClient.subscribe('/topic/userList', message => {
      //   this.users = JSON.parse(message.body);
      //   console.log(this.users)
      // });
      // 订阅服务器发送的获胜信息
      this.stompClient.subscribe('/topic/getWinner', message => {
        this.winner = message.body
        if (this.winner != 'no one win') {
          this.refreshInterval = clearInterval(this.refreshInterval)
          this.gameStarted = false
          this.loadlog()
        }
        // 处理获胜信息
        // ...
      });
      // // 请求用户列表
      // this.requestUserList();
    },
    // requestUserList() {
    //   this.stompClient.send("/app/user/getUserList", {}, {});
    // },
    filterTeam(value, row) {
      return row.teamname === value
    },
    // },
    // methods: {
    load(url) {
      request.get(url).then(res => {
        res.forEach((user) => {
          user.x = parseFloat(user.x.toFixed(2))
          user.y = parseFloat(user.y.toFixed(2))
          user.distance = parseFloat(user.distance.toFixed(2))
        })
        this.tableData = res
        this.stompClient.send("/app/user/getWinner", {}, {});
        this.drawMap(this.tableData, this.gameData);
        // request.get('user/getWinner').then(winner => {
        //   this.winner = winner
        //   this.drawMap(this.tableData, this.gameData);
        //   if (this.winner != 'no one win') {
        //     this.refreshInterval = clearInterval(this.refreshInterval)
        //     this.gameStarted = false
        //     this.loadlog()
        //   }
        // })
      })
    },
    drawMap(tableData, gameData) {
      const canvas = this.$refs.canvas;
      if (canvas.getContext) {
        const ctx = canvas.getContext('2d');
        ctx.textAlign = 'center';

        function resizeCanvas() {
          canvas.width = canvas.offsetWidth;
          canvas.height = canvas.offsetHeight;
          drawPoints();
        }

        function drawPoints() {
          ctx.clearRect(0, 0, canvas.width, canvas.height); // 清除之前的绘制
          const scale = canvas.width / 400; // 假设原始宽度为400
          // 设置原点坐标，这里我们将原点设置在画布中心
          const origin = {x: canvas.width / 2, y: canvas.height / 2};
          // 设置坐标轴的长度
          const axisLength = canvas.width / 2 - 10 * scale;
          const step = 2
          const range = 25
          const single = axisLength / range
          const colors = {
            "Team A": "red",
            "Team B": "blue",
            "Team C": "green"
          }
          const aim = 5

          // 绘制X轴
          ctx.beginPath();
          ctx.moveTo(origin.x - axisLength, origin.y);
          ctx.lineTo(origin.x + axisLength, origin.y);
          ctx.stroke();

          // 绘制Y轴
          ctx.beginPath();
          ctx.moveTo(origin.x, origin.y - axisLength);
          ctx.lineTo(origin.x, origin.y + axisLength);
          ctx.stroke();

          // 添加X轴刻度
          ctx.textAlign = 'center';
          for (let i = 0; i < range; i += step) {
            ctx.beginPath();
            ctx.moveTo(origin.x + single * i, origin.y - 2 * scale);
            ctx.lineTo(origin.x + single * i, origin.y);
            ctx.stroke();
            ctx.fillText(i, origin.x + single * i, origin.y + 7 * scale);
          }
          for (let i = -step; i > -range; i -= step) {
            ctx.beginPath();
            ctx.moveTo(origin.x + single * i, origin.y - 2 * scale);
            ctx.lineTo(origin.x + single * i, origin.y);
            ctx.stroke();
            ctx.fillText(i, origin.x + single * i, origin.y + 7 * scale);
          }

          // 添加Y轴刻度
          ctx.textAlign = 'right';
          for (let i = step; i < range; i += step) {
            ctx.beginPath();
            ctx.moveTo(origin.x, origin.y - axisLength / range * i);
            ctx.lineTo(origin.x + 2 * scale, origin.y - axisLength / range * i);
            ctx.stroke();
            ctx.fillText(i, origin.x - 3 * scale, origin.y - axisLength / range * i);
          }
          for (let i = -step; i > -range; i -= step) {
            ctx.beginPath();
            ctx.moveTo(origin.x, origin.y - axisLength / range * i);
            ctx.lineTo(origin.x + 2 * scale, origin.y - axisLength / range * i);
            ctx.stroke();
            ctx.fillText(i, origin.x - 3 * scale, origin.y - axisLength / range * i);
          }

          tableData.forEach(point => {
            ctx.fillStyle = colors[point.teamname];
            ctx.beginPath();
            ctx.arc(origin.x + single * point.x, origin.y - single * point.y, 0.2 * single, 0, Math.PI * 2, true);
            // ctx.fillText(point.username, point.distance * scale + 10 * scale, canvas.height - point.distance * scale - 10 * scale - 10 * scale);

            ctx.fill();
            if (point.distance <= aim) {
              ctx.strokeStyle = 'black'
              ctx.beginPath();
              ctx.arc(origin.x + single * point.x, origin.y - single * point.y, 0.3 * single, 0, Math.PI * 2, false);
              ctx.stroke();
            }
          });
          ctx.strokeStyle = 'black';
          ctx.setLineDash([15, 10]); // [线段长度, 空白长度]
          ctx.beginPath();
          ctx.arc(origin.x + single * gameData.x, origin.y - single * gameData.y, aim * single, 0, Math.PI * 2, false);
          // ctx.lineWidth = 2; // 设置轮廓线宽
          ctx.stroke();

          function drawFlag(ctx, x, y, width, height) {
            // 绘制旗杆
            y -= 1.5 * height;
            ctx.beginPath();
            ctx.moveTo(x, y);
            ctx.lineTo(x, y + height * 2); // 假设旗杆高度为100
            ctx.strokeStyle = 'black'; // 设置旗杆颜色
            ctx.lineWidth = 2; // 设置旗杆宽度
            ctx.stroke();

            // 绘制旗面
            ctx.beginPath();
            ctx.lineTo(x, y);
            ctx.lineTo(x + width, y + height / 2);
            ctx.lineTo(x, y + height);
            ctx.closePath();
            ctx.fillStyle = 'red'; // 设置旗面颜色
            ctx.fill();
          }

          // 设置旗子的位置和尺寸
          const flagX = origin.x + single * gameData.x; // 在X轴正方向偏移50单位
          const flagY = origin.y - single * gameData.y; // 在Y轴负方向偏移50单位
          const flagWidth = 4.5 * scale; // 旗面的宽度
          const flagHeight = 4 * scale; // 旗面的高度

          // 绘制旗子
          drawFlag(ctx, flagX, flagY, flagWidth, flagHeight);

          // ctx.fillStyle = 'black'
          // ctx.font = single + 'px Arial';
          // ctx.beginPath()
          // ctx.fillText(winner, origin.x + 13 * single, origin.y - 17 * single); // 绘制文字
          // ctx.stroke();
        }

        // 初始绘制
        resizeCanvas();

        // 监听窗口尺寸变化
        window.addEventListener('resize', resizeCanvas);
      }
    },
    reset() {
      this.gameStarted = true
      request.post('user/reset').then(res => {
        request.post('user/new_game', this.gameData).then(res => {
              this.load('user/find')
              this.winner = 'no one win'
              this.refreshInterval = clearInterval(this.refreshInterval)
              this.refreshInterval = setInterval(() => {
                this.load('user/refresh')
              }, 1000)
            }
        )
      })
    },
    refresh() {
      this.load('user/refresh')
    },
    loadlog() {
      request.get('user/winnerlist').then(res => {
        this.winnerlist = res.reverse()
      })
    }
  },
}
</script>
