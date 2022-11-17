package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Category;
import com.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
            log.info("page={},pageSize = {}",page,pageSize);
//            分页构造器
            Page<Category> pageInfo = new Page<>(page,pageSize);
//            条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//            添加排序条件
        queryWrapper.orderByDesc(Category::getSort);
//        进行分页查询
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long id){
        categoryService.remove(id);
        return R.success("删除分类成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){

        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }


    @GetMapping("/list")
    /**
     *  根据条件查询分类数据   在新增菜品时下拉框要现实的菜品类别
     */
    public R<List<Category>> list(Category category){
//        条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//        添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
//          添加排序体条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list();
        return R.success(list);
    }
}
