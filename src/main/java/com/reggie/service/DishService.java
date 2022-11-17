package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;
import org.springframework.transaction.annotation.Transactional;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);
//    根据id查询对应的菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

}
