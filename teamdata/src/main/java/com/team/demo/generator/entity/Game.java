package com.team.demo.generator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
@TableName("winner")
public class Game {

  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

    @TableField("gamename")
    private String gamename;

    @TableField(exist = false)
    private Integer x;

    @TableField(exist = false)
    private Integer y;

    @TableField("teamname")
    private String teamname;

    @TableField("time")
    private String time;

    // 省略构造函数、getter 和 setter 方法


}
