package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 根据查询条件查询地址集合
     * @param addressBook 查询条件
     * @return 返回地址集合
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 新增地址
     * @param addressBook 新增的地址的具体信息
     */
    void insert(AddressBook addressBook);

    /**
     * 根据id查询地址
     * @param id 地址id
     * @return 返回查询到的地址
     */
    AddressBook getById(Long id);

    /**
     * 根据id修改地址
     * @param addressBook 需要修改的地址数据
     */
    void update(AddressBook addressBook);

    /**
     * 根据userid修改该用户所有地址默认状态
     * @param addressBook 包含修改userid和is_default的entity
     */
    void updateIsDefaultByUserId(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id 需要删除的地址id
     */
    void deleteById(Long id);
}
