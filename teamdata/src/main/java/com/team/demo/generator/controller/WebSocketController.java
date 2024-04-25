package com.team.demo.generator.controller;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.demo.config.Result;
import com.team.demo.generator.dao.GameMapper;
import com.team.demo.generator.dao.UserMapper;
import com.team.demo.generator.entity.Game;
import com.team.demo.generator.entity.Team;
import com.team.demo.generator.entity.User;
import com.team.demo.websocket.WinnerEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@Controller
public class WebSocketController {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private UserMapper userMapper;

    @Resource
    private GameMapper gameMapper;

    // 获取用户列表
    @MessageMapping("/user/getUserList")
    @SendTo("/topic/userList")
    public List<User> getUserList() {
         List<Integer> userL = new ArrayList<>();

        for(int i=0;i<userMapper.countAll();i++)
        {
            userL.add(i+1);
        }

        List<User> user = userMapper.queryUserByIds(userL);
        System.out.println("user = " + user);
        return user;
    }

    // 添加新用户
    @MessageMapping("/user/postUser")
    @SendTo("/topic/postUserResponse")
    public String postUser(@RequestBody User user) {
        UserController.users.put(user.getId(), user);
        return "success";
    }

    // 根据ID获取用户
    @MessageMapping("/user/getUser")
    @SendTo("/topic/user")
    public User getUser(Integer id) {
        return userMapper.findById(id);
    }

    // 创建新游戏
    @MessageMapping("/user/newGame")
    @SendTo("/topic/newGameResponse")
    public Result<?> newGame(@RequestBody Game newGame) {
        UserController.gameName = newGame.getGamename();
        UserController.f_x = newGame.getX();
        UserController.f_y = newGame.getY();
        return Result.success();
    }

    // 保存用户信息
    @MessageMapping("/user/save")
    @SendTo("/topic/saveUserResponse")
    public Result<?> save(@RequestBody User user) {
        userMapper.insert(user);
        return Result.success();
    }

    // 根据ID分页获取用户
    @MessageMapping("/user/getPager")
    @SendTo("/topic/userPager")
    public Page<User> getPager(Integer id) {
        return userMapper.findP(id);
    }

    // 获取获胜者列表
    @MessageMapping("/user/winnerlist")
    @SendTo("/topic/winnerList")
    public List<Game> winner() {
        return gameMapper.findWinner();
    }

    // 查找用户
    @MessageMapping("/user/find")
    @SendTo("/topic/findUsers")
    public List<User> find() {
        List<Integer> userL = new ArrayList<>();
        for (int i = 0; i < userMapper.countAll(); i++) {
            userL.add(i + 1);
        }
        return userMapper.queryUserByIds(userL);
    }

    // 分页查找用户
    @MessageMapping("/user/findPage")
    @SendTo("/topic/findPage")
    public Result<?> findPage(Integer pageNum, Integer pageSize, String search) {
        return Result.success(userMapper.selectPage(new Page<>(pageNum, pageSize), Wrappers.<User>lambdaQuery().like(User::getUsername, search)));
    }

    // 刷新并获取所有用户信息
    @MessageMapping("/user/refresh")
    @SendTo("/topic/refreshUsers")
    public List<User> findAndRefresh() {
        // 更新用户坐标逻辑
        return new ArrayList<>(UserController.users.values());
    }

    // 重置用户信息
    @MessageMapping("/user/reset")
    public void reset() {
        userMapper.truncateUserTable();
        UserController.users.clear();
    }

    // 添加队伍成员
    @MessageMapping("/user/addMember")
    @SendTo("/topic/addMemberResponse")
    public Result<?> addMember(@RequestBody User user) {
        userMapper.insert(user);
        return Result.success();
    }

    // 获取胜利者
    /*@MessageMapping("/user/getWinner")
    @SendTo("/topic/getWinner")
    public String getWinner() {
        for (Team team : UserController.userTeam) {
            if (team.getWinNum() >= 5) {
                return team.getTeamName() + " win!";
            }
        }
        return "no one win";
    }*/


    @MessageMapping("/user/getWinner")
    public void getWinner() {
        for (Team team : UserController.userTeam) {
            if (team.getWinNum() >= 5) {
                String winnerMessage = team.getTeamName() + " win!";
                eventPublisher.publishEvent(new WinnerEvent(this, winnerMessage));
                return;
            }
        }
    }
}

