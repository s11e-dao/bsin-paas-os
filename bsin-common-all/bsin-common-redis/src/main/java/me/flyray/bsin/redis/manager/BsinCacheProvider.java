package me.flyray.bsin.redis.manager;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class BsinCacheProvider {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private RedisTemplate<Object, Object> redisObjTemplate;

    // 指定用redis的序列化方式进行序列化
    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer(); // 序列化为String
        // 不能反序列化
        // Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new
        // Jackson2JsonRedisSerializer(Object.class);//序列化为Json
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(serializer);
        this.redisObjTemplate = redisTemplate;
    }

    public <T> void set(String key, T value) {
        String json = JSON.toJSON(value).toString();
        // 将json对象转换为字符串
        redisTemplate.opsForValue().set(key, json);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setEx(String key, String value, Long expireTime) {
        redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
    }

    public void sAdd(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public void sAdd(String key, String... value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public Boolean setBit(String key, long l, boolean value) {
        return redisTemplate.opsForValue().setBit(key, l, value);
    }

    public Boolean getBit(String key, long l) {
        return redisTemplate.opsForValue().getBit(key, l);
    }

    public List<Long> bitField(String key, BitFieldSubCommands command) {
        return redisTemplate.opsForValue().bitField(key, command);
    }

    public <T> T execute(RedisCallback<T> var1) {
        return redisTemplate.execute(var1);
    }
    /**
     * 此方法将ArrayList集合直接存储为一个字符串
     *
     * @param key 存储的名字
     * @param list 要存储的集合对象
     * @param expireTime 该对象的有效时间，单位为秒
     */
    public <T> void setListEx(String key, List<T> list, Long expireTime) {
        redisTemplate
                .opsForValue()
                .set(key, JSONArray.toJSONString(list), expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取无序集合
     *
     * @param key1
     */
    public Set<String> getMembers(String key1) {
        return redisTemplate.opsForSet().members(key1);
    }

    /**
     * 求两个无序集合的交集
     *
     * @param key1
     * @param key2
     */
    public void setIntersect(String key1, String key2) {
        redisTemplate.opsForSet().intersect(key1, key2);
    }

    /**
     * 求两个无序集合的交集存在 key3集合中
     *
     * @param key1
     * @param key2
     * @param key3
     */
    public void setIntersectAndStore(String key1, String key2, String key3) {
        redisTemplate.opsForSet().intersectAndStore(key1, key2, key3);
    }

    /**
     * 无序集合的并集
     *
     * @param key1
     * @param key2
     */
    public void setUnion(String key1, String key2) {
        redisTemplate.opsForSet().union(key1, key2);
    }

    /**
     * 求两个结合的并集存在 key3中
     *
     * @param key1
     * @param key2
     * @param key3
     */
    public void setUnionAndStore(String key1, String key2, String key3) {
        redisTemplate.opsForSet().unionAndStore(key1, key2, key3);
    }

    /**
     * 求两个无序集合的差集存入key3集合中
     *
     * @param key1
     * @param key2
     * @param key3
     */
    public void setDifferenceAndStore(String key1, String key2, String key3) {
        redisTemplate.opsForSet().differenceAndStore(key1, key2, key3);
    }

    /**
     * 求两个无序集合的差集
     *
     * @param key1
     * @param key2
     */
    public void setDifference(String key1, String key2) {
        redisTemplate.opsForSet().difference(key1, key2);
    }

    public boolean set(String key, String value, long validTime) {
        boolean result =
                redisTemplate.execute(
                        new RedisCallback<Boolean>() {
                            @Override
                            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                                connection.set(serializer.serialize(key), serializer.serialize(value));
                                connection.expire(serializer.serialize(key), validTime);
                                return true;
                            }
                        });
        return result;
    }

    /**
     * 判断某个key是否存在
     *
     * @param key
     * @return
     */
    public boolean exist(String key) {
        Boolean flag = false;
        try {
            flag = redisTemplate.hasKey(key);
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public <T> T get(String key, Class<T> clazz) {
        JSONObject jso = com.alibaba.fastjson.JSON.parseObject(get(key));
        return JSONObject.toJavaObject(jso, clazz);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        return JSONObject.parseArray(get(key), clazz);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    /*String result = template.execute(new RedisCallback<String>() {
        @Override
        public String doInRedis(RedisConnection connection) throws DataAccessException {
            RedisSerializer<String> serializer = template.getStringSerializer();
            byte[] value = connection.get(serializer.serialize(key));
            return serializer.deserialize(value);
        }
    });
    return result;*/
    }

    public boolean del(String key) {
        return redisTemplate.delete(key);
    }

    /** operations for List */

    /**
     * 指定 key 的过期时间
     *
     * @param key 键
     * @param time 时间(秒)
     */
    public void setKeyTime(String key, long time) {
        redisObjTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 根据 key 获取过期时间（-1 即为永不过期）
     *
     * @param key 键
     * @return 过期时间
     */
    public Long getKeyTime(String key) {
        return redisObjTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return 如果存在 key 则返回 true，否则返回 false
     */
    public Boolean hasKey(String key) {
        return redisObjTemplate.hasKey(key);
    }

    /**
     * 删除 key
     *
     * @param key 键
     */
    public Long delKey(String... key) {
        if (key == null || key.length < 1) {
            return 0L;
        }
        return redisObjTemplate.delete(Arrays.asList(key));
    }

    /**
     * 获取 Key 的类型
     *
     * @param key 键
     */
    public String keyType(String key) {
        DataType dataType = redisObjTemplate.type(key);
        assert dataType != null;
        return dataType.code();
    }

    /**
     * 批量设置值
     *
     * @param map 要插入的 key value 集合
     */
    public void barchSet(Map<String, Object> map) {
        redisObjTemplate.opsForValue().multiSet(map);
    }

    /**
     * 批量获取值
     *
     * @param list 查询的 Key 列表
     * @return value 列表
     */
    public List<Object> batchGet(List<String> list) {
        return redisObjTemplate.opsForValue().multiGet(Collections.singleton(list));
    }

    /**
     * 获取指定对象类型key的值
     *
     * @param key 键
     * @return 值
     */
    public Object objectGetKey(String key) {
        return redisObjTemplate.opsForValue().get(key);
    }

    /**
     * 设置对象类型的数据
     *
     * @param key 键
     * @param value 值
     */
    public void objectSetValue(String key, Object value) {
        redisObjTemplate.opsForValue().set(key, value);
    }

    /**
     * 向list的头部插入一条数据
     *
     * @param key 键
     * @param value 值
     */
    public Long listLeftPush(String key, Object value) {
        return redisObjTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 向list的末尾插入一条数据
     *
     * @param key 键
     * @param value 值
     */
    public Long listRightPush(String key, Object value) {
        return redisObjTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 向list头部添加list数据
     *
     * @param key 键
     * @param value 值
     */
    public Long listLeftPushAll(String key, List<Object> value) {
        return redisObjTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * 向list末尾添加list数据
     *
     * @param key 键
     * @param value 值
     */
    public Long listRightPushAll(String key, List<Object> value) {
        return redisObjTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * 通过索引设置list元素的值
     *
     * @param key 键
     * @param index 索引
     * @param value 值
     */
    public void listIndexSet(String key, long index, Object value) {
        redisObjTemplate.opsForList().set(key, index, value);
    }

    /**
     * 获取列表指定范围内的list元素，正数则表示正向查找，负数则倒叙查找
     *
     * @param key 键
     * @param start 开始
     * @param end 结束
     * @return boolean
     */
    public Object listRange(String key, long start, long end) {
        return redisObjTemplate.opsForList().range(key, start, end);
    }

    /**
     * 从列表前端开始取出数据
     *
     * @param key 键
     * @return 结果数组对象
     */
    public Object listPopLeftKey(String key) {
        return redisObjTemplate.opsForList().leftPop(key);
    }

    /**
     * 从列表末尾开始遍历取出数据
     *
     * @param key 键
     * @return 结果数组
     */
    public Object listPopRightKey(String key) {
        return redisObjTemplate.opsForList().rightPop(key);
    }

    /**
     * 获取list长度
     *
     * @param key 键
     * @return 列表长度
     */
    public Long listLen(String key) {
        return redisObjTemplate.opsForList().size(key);
    }

    /**
     * 通过索引获取list中的元素
     *
     * @param key 键
     * @param index 索引（index>=0时，0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推）
     * @return 列表中的元素
     */
    public Object listIndex(String key, long index) {
        return redisObjTemplate.opsForList().index(key, index);
    }

    /**
     * 移除list元素
     *
     * @param key 键
     * @param count 移除数量（"负数"则从列表倒叙查找删除 count 个对应的值; "整数"则从列表正序查找删除 count 个对应的值;）
     * @param value 值
     * @return 成功移除的个数
     */
    public Long listRem(String key, long count, Object value) {
        return redisObjTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 截取指定范围内的数据, 移除不是范围内的数据
     *
     * @param key 操作的key
     * @param start 截取开始位置
     * @param end 截取激素位置
     */
    public void listTrim(String key, long start, long end) {
        redisObjTemplate.opsForList().trim(key, start, end);
    }

}
