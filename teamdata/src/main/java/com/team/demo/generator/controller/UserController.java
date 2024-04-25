package com.team.demo.generator.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.demo.config.Result;
import com.team.demo.generator.dao.GameMapper;
import com.team.demo.generator.dao.UserMapper;
import com.team.demo.generator.entity.Game;
import com.team.demo.generator.entity.Team;
import com.team.demo.generator.entity.User;
import org.apache.logging.log4j.message.ReusableMessage;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ky
 * @since 2024年04月08日
 */
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    static Map<Integer, User> users = Collections.synchronizedMap(new HashMap<>());

    static List<Team> userTeam = new ArrayList<>();

    static String gameName = "unknown";

    static Integer f_x = 0;
    static Integer f_y = 0;

    boolean have = false;

    @Resource
    UserMapper userMapper;

    @Resource
    GameMapper gameMapper;

    @GetMapping("/")
    public List<User> getUserList() {
        List<User> r = new ArrayList<>(users.values());
        return r;
    }


    @PostMapping("/")
    public String postUser(@RequestBody User user) {
        users.put(user.getId(), user);
        return "success";
    }


    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {

        User user = userMapper.findById(id);
        return user;
    }

    @PostMapping("/new_game")
    public Result<?> newGame(@RequestBody Game newGame)
    {
        //userMapper.insert(user);
        gameName = newGame.getGamename();
        f_x = newGame.getX();
        f_y = newGame.getY();
        return Result.success();
    }

    @PostMapping
    public Result<?> save(@RequestBody User user)
    {
        userMapper.insert(user);
        return Result.success();
    }

    @GetMapping("distance/{id}")
    public Page<User> getPager(@PathVariable Integer id) {

        return userMapper.findP(id);
    }

    @GetMapping("/winnerlist")
    public List<Game> winner()
    {
        List<Game> game = gameMapper.findWinner();
        //System.out.println("user = " + game);
        return game;
    }

    @GetMapping("/find")
    public List<User> find()
    {
        List<Integer> userL = new ArrayList<>();

        for(int i=0;i<userMapper.countAll();i++)
        {
            userL.add(i+1);
        }

        List<User> user = userMapper.queryUserByIds(userL);
        System.out.println("user = " + user);
        return user;
    }


    @GetMapping
    public Result<?> findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize,@RequestParam String search)
    {
        userMapper.selectPage(new Page<>(pageNum,pageSize), Wrappers.<User>lambdaQuery().like(User::getUsername,search));
        return Result.success();
    }

    @GetMapping("/refresh")
    public List<User> findAndFresh()
    {
        haveSuccess();

        //获取所有用户
        List<Integer> userL = new ArrayList<>();

        for(int i=0;i<userMapper.countAll();i++)
        {
            userL.add(i+1);
        }

        List<User> user = userMapper.queryUserByIds(userL);

        if(!haveSuccess()) {
            //对所有用户进行数据更改
            for (int i = 0; i < user.size(); i++) {
                if(user.get(i).getDistance()>2)
                {
                    double x = user.get(i).getX()-f_x;
                    double y = user.get(i).getY()-f_y;
                    userMapper.updateCoordinatesById(x / Math.abs(x) * Math.random()*1.5,(y) / Math.abs(y) * Math.random()*1.5, i + 1);
                }
            }

            //写回更新
            List<Integer> ids = new ArrayList<>();

            for (int i = 0; i < userMapper.countAll(); i++) {
                ids.add(i+1);
            }

            userMapper.updateUserByIds(ids,f_x,f_y);
        }


        //最后更新
        user = userMapper.queryUserByIds(userL);

        System.out.println("user = " + user);

        return user;
    }


    @PostMapping("/reset")
    public void reset()
    {
        userMapper.truncateUserTable();

        userMapper.reset();

        userTeam = new ArrayList<>();

        gameName = "unknown";
        have = false;

        f_x = 0;
        f_y = 0;

        //写回更新
        List<Integer> ids = new ArrayList<>();

        for (int i = 0; i < userMapper.countAll(); i++) {
            ids.add(i+1);
        }

       /* List<User> user = userMapper.queryUserByIds(ids);

         for (int i = 0; i < user.size(); i++) {
                if(user.get(i).getDistance()>2)
                {
                    double x = user.get(i).getX();
                    double y = user.get(i).getY();
                    userMapper.updateCoordinatesById(x,y, i + 1);
                }
            }*/


        /*UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
                .setSql("distance = SQRT(POW(x, 2) + POW(y, 2))")
                .in("id", ids);
        userMapper.update(null, wrapper);*/
        userMapper.updateUserByIds(ids,f_x,f_y);

        haveSuccess();
    }

    @PostMapping("/addMember")
    public Result<?> add(@RequestParam String  userName, @RequestParam String  teamName,@RequestParam double x,@RequestParam double y)
    {
        User user = new User();
        user.setUsername(userName);
        user.setTeamname(teamName);
        user.setX(x);
        user.setY(y);
        user.setDistance(100);
        userMapper.insert(user);
        return Result.success();
    }


    @GetMapping("/getWinner")
    public String getWinner()
    {
        haveSuccess();

        for (Team team : userTeam) {
            if (team.getWinNum() >= 5) {
                // 获取当前时间
                LocalDateTime now = LocalDateTime.now();

                // 定义日期时间格式
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                // 格式化当前时间为字符串
                String formattedDateTime = now.format(formatter);

                // 输出当前时间字符串
                System.out.println("Current time: " + formattedDateTime);

                if(!have)
                {
                    userMapper.saveWinner(team.getTeamName(),formattedDateTime,gameName);
                    have=true;
                }


                return team.getTeamName()+" win!";
            }
        }

        return "no one win";
    }




    public boolean haveSuccess()
    {
        //获取所有用户
        List<Integer> userL = new ArrayList<>();

        for(int i=0;i<userMapper.countAll();i++)
        {
            userL.add(i+1);
        }

        List<User> user = userMapper.queryUserByIds(userL);


        for(User user_n : user)
        {

            if(Math.abs(user_n.getDistance())< 5d)
            {
                String teamName = user_n.getTeamname();
                userTeam = teamRefresh(teamName,userTeam,user_n.getId());
            }

        }

        for (Team team : userTeam) {
            if (team.getWinNum() >= 5) {
                return true;
            }
        }

        return false;
    }

    public boolean teamCheck(Team team,Integer id)
    {
        for(Integer id_n : team.getConclude())
        {
            if(id_n.equals(id))
            {
                return true;
            }
        }
        return false;
    }

    public List<Team> teamRefresh(String teamName,List<Team> userTeam,Integer id)
    {
        boolean flag = false;

        for (Team team : userTeam) {
            if (team.getTeamName().equals(teamName)) {
                flag = true;
                if(!teamCheck(team,id))
                {
                    team.getConclude().add(id);
                    team.setWinNum(team.getWinNum() + 1);
                }
            }
        }

        if(!flag)
        {
            Team a = new Team();
            a.resetConclude();
            a.setTeamName(teamName);
            a.setWinNum(1);
            a.getConclude().add(id);
            userTeam.add(a);
        }
        return userTeam;
    }




}

