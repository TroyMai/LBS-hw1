package com.team.demo.generator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ky
 * @since 2024年04月08日
 */
@Getter
@Setter
  @Accessors(chain = true)
  @TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

    @TableField("username")
    private String username;

    @TableField("teamname")
    private String teamname;

    @TableField("distance")
    private double distance;

    @TableField("x")
    private double x;

    @TableField("y")
    private double y;


}
