package me.flyray.bsin.infrastructure.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;


public class RedisClientUtil {
    private static final Logger log = LoggerFactory.getLogger(RedisClientUtil.class);

    
    /**
     * 获取指定key的值,如果key不存在返回null
     * 返回值：返回 key 的值，如果 key 不存在时，返回 nil
     * @param key
     * @return
     */
    public static String get(String key) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.get(key);
        } catch (Exception e){
            log.error("get命令操作失败，请求参数：{}", key,e);
        }
        return null;
    }


    /**
     * 设置key的值为value
     * 返回值：操作成功完成时返回 OK
     * @param key
     * @return
     */
    public static String set(String key, String value) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.set(key, value);
        } catch (Exception e){
            log.error("set命令操作失败，参数key：{}，参数value：{}", key, value,e);
        }
        return null;
    }


    /**
     * 删除指定的key，返回值：被删除 key 的数量
     * 返回值：被删除 key 的数量
     * @param key
     * @return
     */
    public static Long del(String key) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            Long result = jedis.del(key);
            return jedis.del(key);
        } catch (Exception e){
            log.error("del命令操作失败，参数key：{}", key,e);
        }
        return 0L;
    }


    /**
     * 通过key向指定的value值追加值
     * 返回值：追加指定值之后， key中字符串的长度
     * @param key
     * @return
     */
    public static Long append(String key, String value) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.append(key, value);
        } catch (Exception e){
            log.error("append命令操作失败，参数key：{}，参数value：{}", key, value,e);
        }
        return 0L;
    }

    /**
     * 判断key是否存在
     * 返回值：true/false
     * @param key
     * @return
     */
    public static Boolean exists(String key) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.exists(key);
        } catch (Exception e){
            log.error("exists命令操作失败，参数key：{}", key,e);
        }
        return false;
    }


    /**
     * 设置key的超时时间为seconds
     * 返回值：若 key 存在返回 1 ，否则返回 0
     * @param key
     * @return
     */
    public static Long expire(String key, long seconds) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.expire(key, seconds);
        } catch (Exception e){
            log.error("expire命令操作失败，参数key：{}，参数seconds：{}", key, seconds,e);
        }
        return 0L;
    }

    /**
     * 返回 key 的剩余过期时间（单位秒）
     * 返回值：当 key 不存在时，返回 -2 。 当 key 存在但没有设置剩余生存时间时，返回 -1 。 否则，以秒为单位，返回 key 的剩余生存时间
     * @param key
     * @return
     */
    public static Long ttl(String key) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.ttl(key);
        } catch (Exception e){
            log.error("ttl命令操作失败，参数key：{}", key,e);
        }
        return 0L;
    }


    /**
     * 设置指定key的值为value,当key不存在时才设置
     * 返回值：设置成功返回 1，设置失败返回 0
     * @param key
     * @return
     */
    public static Long setnx(String key, String value) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.setnx(key, value);
        } catch (Exception e){
            log.error("setnx命令操作失败，参数key：{}，参数value：{}", key, value,e);
        }
        return 0L;
    }

    /**
     * 设置指定key的值为value,并设置过期时间
     * 返回值：设置成功时返回 OK
     * @param key
     * @return
     */
    public static String setex(String key, String value, long seconds) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.setex(key, seconds, value);
        } catch (Exception e){
            log.error("setex命令操作失败，参数key：{}，参数value：{}", key, value,e);
        }
        return null;
    }

    /**
     * 通过key 和offset 从指定的位置开始将原先value替换
     * 返回值：被修改后的字符串长度
     * @param key
     * @return
     */
    public static Long setrange(String key, int offset, String value) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.setrange(key, offset, value);
        } catch (Exception e){
            log.error("setrange命令操作失败，参数key：{}，参数value：{}，参数offset：{}", key, value, offset,e);
        }
        return null;
    }


    /**
     * 通过批量的key获取批量的value
     * 返回值：一个包含所有给定 key 的值的列表。
     * @param keys
     * @return
     */
    public static List<String> mget(String... keys) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.mget(keys);
        } catch (Exception e){
            log.error("mget命令操作失败，参数key：{}", keys.toString(),e);
        }
        return null;
    }

    /**
     * 批量的设置key:value,也可以一个
     * 返回值：总是返回 OK
     * @param keysValues
     * @return
     */
    public static String mset(String... keysValues) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.mset(keysValues);
        } catch (Exception e){
            log.error("mset命令操作失败，参数key：{}", keysValues.toString(),e);
        }
        return null;
    }


    /**
     * 设置key的值,并返回一个旧值
     * 返回值：返回给定 key 的旧值，当 key 没有旧值时，即 key 不存在时，返回 nil
     * @param key
     * @return
     */
    public static String getSet(String key, String value) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.getSet(key, value);
        } catch (Exception e){
            log.error("getSet命令操作失败，参数key：{}，参数value：{}", key, value,e);
        }
        return null;
    }

    /**
     * 通过下标和 key 获取指定下标位置的 value
     * 返回值：截取得到的子字符串
     * @param key
     * @return
     */
    public static String getrange(String key, int startOffset, int endOffset) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.getrange(key, startOffset, endOffset);
        } catch (Exception e){
            log.error("getrange命令操作失败，参数key：{}，参数startOffset：{}，参数offset：{}", key, startOffset, endOffset,e);
        }
        return null;
    }


    /**
     * 通过key 对value进行加值+1操作,当value不是int类型时会返回错误,当key不存在是则value为1
     * 返回值：执行INCR命令之后 key 的值
     * @param key
     * @return
     */
    public static Long incr(String key) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.incr(key);
        } catch (Exception e){
            log.error("incr命令操作失败，参数key：{}", key, e);
        }
        return 0L;
    }


    /**
     * 通过key给指定的value加值
     * 返回值：执行INCR命令之后 key 的值
     * @param key
     * @return
     */
    public static Long incrBy(String key, long increment) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.incrBy(key, increment);
        } catch (Exception e){
            log.error("incrBy命令操作失败，参数key：{}，参数increment：{}", key, increment,e);
        }
        return 0L;
    }

    /**
     * 对key的值做减减操作
     * 返回值：执行INCR命令之后 key 的值
     * @param key
     * @return
     */
    public static Long decr(String key) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.decr(key);
        } catch (Exception e){
            log.error("decr命令操作失败，参数key：{}", key, e);
        }
        return 0L;
    }

    /**
     * 对key的值做减减操作,减去指定的值
     * 返回值：执行INCR命令之后 key 的值
     * @param key
     * @return
     */
    public static Long decrBy(String key, long decrement) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.decrBy(key, decrement);
        } catch (Exception e){
            log.error("decrBy命令操作失败，参数key：{}，参数decrement：{}", key, decrement,e);
        }
        return 0L;
    }


    /**
     * 通过key获取value值的长度
     * 返回值：value值的长度
     * @param key
     * @return
     */
    public static Long strlen(String key) {
        try (Jedis jedis = RedisPoolUtils.getJedis()) {
            return jedis.strlen(key);
        } catch (Exception e){
            log.error("strlen命令操作失败，参数key：{}", key, e);
        }
        return 0L;
    }
}
