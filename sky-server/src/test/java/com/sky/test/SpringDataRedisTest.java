package com.sky.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate(){
        System.out.println(redisTemplate);//输出不为空说明对象创建成功
        //获取对应value类型的操作对象，基于对象的相关方法执行对应的Redis命令
        ValueOperations valueOperations = redisTemplate.opsForValue();//字符串类型
        HashOperations hashOperations = redisTemplate.opsForHash();//Hash类型
        ListOperations listOperations = redisTemplate.opsForList();//列表类型
        SetOperations setOperations = redisTemplate.opsForSet();//无序集合类型
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();//有序集合类型
    }

    /**
     * 操作字符串类型数据
     * set-->set(key,value)
     * get-->get(key)
     * setex-->set(key,value,timeout,unit)
     * setnx-->setIfAbsent(key,value)
     */
    @Test
    public void testString(){
        //获取字符串类型的操作对象
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("name","wyq");
        String name = (String) valueOperations.get("name");
        System.out.println(name);
        //设置期限40秒
        valueOperations.set("code","123456",40, TimeUnit.SECONDS);
        //如果不存在即创建，如存在不可被覆盖
        valueOperations.setIfAbsent("id","77");
        valueOperations.setIfAbsent("id","33");
    }

    /**
     * 操作哈希类型数据
     * 设置hset-->put(key,field,value)
     * 获取值hget-->get(key,field)
     * 删除hdel-->delete(key,field)
     * 获取键集合hkeys-->keys(key)
     * 获取值集合hvals-->values(key)
     */
    @Test
    public void testHash(){
        HashOperations hashOperations = redisTemplate.opsForHash();
        //存值
        hashOperations.put("student","name","wyq");
        hashOperations.put("student","id","77");
        hashOperations.put("student","gender","man");
        hashOperations.put("student","skill","volleyball");
        //取值
        System.out.println(hashOperations.get("student", "name"));
        //删除键值对
        hashOperations.delete("student","skill");
        //获取键集合(不可重复，返回Set)
        Set studentSet = hashOperations.keys("student");
        System.out.println(studentSet);
        //获取值集合（可重复，返回List）
        List studentValues = hashOperations.values("student");
        System.out.println(studentValues);
    }

    /**
     * 操作集合类型数据
     * 存入l/rpush-->left/rightPush[All](key,element1[,element2...])
     * 获取所有元素lrange-->range(key,start,end)
     * 移除r/lpop-->rightPop(key)
     * 获取元素个数llen-->size(key)
     */
    @Test
    public void testList(){
        ListOperations listOperations = redisTemplate.opsForList();
        //存入元素
        listOperations.leftPush("排队","1");
        listOperations.leftPushAll("排队","2","3","4");
        listOperations.rightPushAll("排队","5","6");
        //获取所有元素个数和指定范围元素集合(List)
        listOperations.size("排队");
        List range = listOperations.range("排队", 0, -1);
        System.out.println(range);
        //删除元素
        listOperations.leftPop("排队");
        listOperations.rightPop("排队");
    }

    /**
     * 操作集合类型数据
     * 添加成员sadd-->add(key,value1,value2...)
     * 获取所有成员smembers-->members(key)
     * 获取集合成员个数scard-->size(key)
     * 两个集合交集sinter-->intersect(key1,key2)
     * 两个集合并集sunion-->union(key1,key2)
     * 删除集合srem-->remove(key,value1,value2...)
     */
    @Test
    public void testSet(){
        SetOperations setOperations = redisTemplate.opsForSet();
        //存入
        setOperations.add("set1","a","s","d","f");
        setOperations.add("set2","d","f","g","h");
        //查看所有和个数
        Set set1 = setOperations.members("set1");
        Long size = setOperations.size("set1");
        System.out.println("set1:"+set1+"\nnumbers："+size);
        //交集并集
        Set intersect = setOperations.intersect("set1", "set2");
        Set union = setOperations.union("set1", "set2");
        System.out.println("交集："+intersect+"\n并集："+union);
        //删除集合元素
        setOperations.remove("set1", "a", "f");
        System.out.println(setOperations.members("set1"));
    }

    /**
     * 操作有序集合类型
     * 添加zadd-->add(key,value,score)
     * 获取指定范围成员集合zrange-->range(key,star,end)
     * 修改成员分数zincrby-->incrementScore(key,value,delta)
     * 删除成员zrem-->remove(key,value)
     */
    @Test
    public void testZset(){
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add("zset","a",1.1);
        zSetOperations.add("zset","a",1.2);//会覆盖
        zSetOperations.add("zset","b",1.3);
        zSetOperations.add("zset","c",1.4);
        zSetOperations.incrementScore("zset","c",6.6);
        Set zset = zSetOperations.range("zset", 0, -1);
        System.out.println(zset);
        zSetOperations.remove("zset","b");
    }

    /**
     * 通用命令
     * 获取所有键集合keys-->keys(通配符)
     * 判断键是否存在exists-->hasKey(key)
     * 获取该键的类型type-->type(key)
     * 删除该键del-->delete(key)
     */
    @Test
    public void testCommon(){
        Set keys = redisTemplate.keys("*");
        System.out.println(keys);
        System.out.println("student exists?"+redisTemplate.hasKey("student"));
        for (Object key : keys) {
            DataType type = redisTemplate.type(key);
            System.out.println(key+"'s type:"+type.name());
        }
        redisTemplate.delete("set2");
    }

}
