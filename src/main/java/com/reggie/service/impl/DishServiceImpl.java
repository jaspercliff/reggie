package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.mapper.DishMapper;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    //   新增菜品 同时保存对应的口味数据
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
//        保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long id = dishDto.getId();
//            保存菜品的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
//        查询菜品基本信息
        Dish dish = this.getById(id);
//                口味信息
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    //    更新菜品信息和口味
    @Override
    public void updateWithFlavor(DishDto dishDto) {
//        更新dish
        this.updateById(dishDto);
//        flavor
//        清理flavor then 重新添加
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
//        添加
        List<DishFlavor> flavors = dishDto.getFlavors();
        Long id = dishDto.getId();
        /**
         * 用于映射每个元素到对应的结果
         */
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;

/**
 * 使用 Collector对此流的元素进行归纳
 */
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
