package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

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
    void save(AddressBook addressBook);

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
     * 设置默认地址
     * @param addressBook 需要被设置为默认的地址
     */
    void setDefault(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id 需要删除的地址id
     */
    void deleteById(Long id);
}
