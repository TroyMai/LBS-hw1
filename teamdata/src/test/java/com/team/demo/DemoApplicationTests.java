package com.team.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.team.demo.generator.dao.GameMapper;
import com.team.demo.generator.dao.UserMapper;
import com.team.demo.generator.entity.Game;
import com.team.demo.generator.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private GameMapper gameMapper;

	@Test
	void contextLoads() {

	}



	@Test
	void testFind()
	{
		List<Integer> userL = new ArrayList<>();
		userL.add(1);
		userL.add(2);
		userL.add(3);
		userL.add(4);
		userL.add(5);
		List<User> user = userMapper.queryUserByIds(userL);
		System.out.println("user = " + user);
	}

	@Test
	void testWent()
	{
		userMapper.updateCoordinatesById(Math.random(),Math.random(),1);

		//更新数值
		User user = new User();

		user.setDistance(Math.sqrt(user.getX()*user.getX()+user.getY()*user.getY()));
		//user.setDistance(2000);

		//条件
		QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("username","Lucy");

		//更新
		userMapper.update(user,wrapper);

	}

	@Test
	void testDelete()
	{
		User d_user = new User();
		d_user.setId(5);
		userMapper.deleteById(d_user);
	}


	@Test
	void testInsert()
	{
		User user = new User();
		user.setId(5);
		user.setUsername("Lucy");
		user.setTeamname("Team A");
		user.setDistance(5);
		user.setX(3);
		user.setY(4);
		userMapper.insert(user);
	}

	@Test
	void testQueryWrapper()
	{
		BigDecimal number1 = new BigDecimal("5");
		QueryWrapper<User> wrapper = new QueryWrapper<User>()
				.select("id","username","teamname","distance","x","y")
				.like("username","o")
				.le("distance",number1);//greater equal
		List<User> users = userMapper.selectList(wrapper);
		users.forEach(System.out::println);
	}

	@Test
	void testUpdateUser()
	{
		//更新数值
		User user = new User();

		user.setDistance(Math.sqrt(user.getX()*user.getX()+user.getY()*user.getY()));
		//user.setDistance(2000);

		//条件
		QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("username","Lucy");

		//更新
		userMapper.update(user,wrapper);
	}
	@Test
	void testUpdateUser2() {

		List<Integer> ids = new ArrayList<>();

		for(int i=0;i<userMapper.countAll();i++)
		{
			ids.add(i);
		}

		UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
				.setSql("distance = SQRT(POW(x, 2) + POW(y, 2))" )
				.in("id",ids);
		userMapper.update(null,wrapper);
	}

	@Test
	void testCount()
	{
		int a = userMapper.countAll();
		System.out.println(a);
	}

	@Test
	void testReset()
	{
		userMapper.truncateUserTable();
		userMapper.reset();
		//System.out.println(a);
	}

	@Test
	void testInsertWinner()
	{
		// 获取当前时间
		LocalDateTime now = LocalDateTime.now();

		// 定义日期时间格式
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		// 格式化当前时间为字符串
		String formattedDateTime = now.format(formatter);
		userMapper.saveWinner("Team B",formattedDateTime,"run");
	}

	@Test
	void testFindWinner()
	{
		List<Game> game = gameMapper.findWinner();
		System.out.println("game = " + game);

	}
}
