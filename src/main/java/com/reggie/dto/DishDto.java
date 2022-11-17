package com.reggie.dto;

import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * dto data transfer object 数据传输对象
 * 一般用于展示层和服务层之间的数据传输
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
