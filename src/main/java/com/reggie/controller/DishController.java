package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.service.CategoryService;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    DishService dishService;
    @Autowired
    DishFlavorService dishFlavorService;
    @Autowired
    CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list =  records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category!=null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询对应的菜品信息和口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@RequestParam("ids") Long[] id, @PathVariable Integer status){
        List<Dish> list = dishService.listByIds(Arrays.asList(id));
        list = list.stream().map((item)->{
            item.setStatus(status);
            return item;
                }).collect(Collectors.toList());
        dishService.updateBatchById(list);
        return R.success("状态修改成功");
    }

    @DeleteMapping
    public R<String> batchDelete(@RequestParam Long[] ids){
        dishService.removeByIds(Arrays.asList(ids));
        return R.success("已经删除成功啦");
    }

    /**
     * 根据条件查询对应的菜品信息
     * Dish dish   比传id更好 模糊查询 更方便 可以通过别的字段  eg name
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByDesc(Dish::getSort).orderByAsc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }
}
