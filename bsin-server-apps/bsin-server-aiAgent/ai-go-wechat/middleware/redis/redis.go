package redis

import (
	config "bsinpass/go/conf"
	log "bsinpass/go/pkg/logging"
	"context"
	"errors"
	"fmt"
	"time"

	"github.com/redis/go-redis/v9"
)

var RedisClient *redis.Client

// Setup initialize the redis.Client instance
func Setup() {
	log.Info("connecting to redis:", config.RedisSetting.Addr, config.RedisSetting.Password, config.RedisSetting.DB)
	RedisClient = redis.NewClient(&redis.Options{
		Addr:     config.RedisSetting.Addr,
		Password: config.RedisSetting.Password,
		DB:       config.RedisSetting.DB,
	})
	// defer RedisClient.Close()
}

func Set(key string, value string, expiration time.Duration) error {
	ctx, cancel := context.WithTimeout(context.Background(), 500*time.Millisecond)
	defer cancel()
	err := RedisClient.Set(ctx, key, value, expiration).Err()
	return err
}

func Get(key string) (string, error) {
	ctx, cancel := context.WithTimeout(context.Background(), 500*time.Millisecond)
	defer cancel()
	val, err := RedisClient.Get(ctx, key).Result()
	if err == nil && val == "" {
		return val, errors.New(fmt.Sprintf("redis key %s is null", key))
	}
	return val, err
}

func Del(key string) error {
	ctx, cancel := context.WithTimeout(context.Background(), 500*time.Millisecond)
	defer cancel()
	err := RedisClient.Del(ctx, key).Err()
	return err
}

// 将一条数据添加到列表的头部（类似入栈
func LPush(key string, value string, expiration time.Duration) error {
	ctx, cancel := context.WithTimeout(context.Background(), 500*time.Millisecond)
	defer cancel()
	err := RedisClient.LPush(ctx, key, value, expiration).Err()
	return err
}

// 将一条或多条数据添加到列表的尾部
func RPush(key string, value string, expiration time.Duration) error {
	ctx, cancel := context.WithTimeout(context.Background(), 500*time.Millisecond)
	defer cancel()
	err := RedisClient.RPush(ctx, key, value, expiration).Err()
	return err
}

// 获取List中的元素：起始索引~结束索引，当结束索引 > llen(list)或=-1时，取出全部数据
// 遍历List，获取每一个元素
// 注意取出来的顺序！！！
func LRange(key string) ([]string, error) {
	ctx, cancel := context.WithTimeout(context.Background(), 500*time.Millisecond)
	defer cancel()
	val, err := RedisClient.LRange(ctx, key, 0, -1).Result()
	if err != nil {
		return val, errors.New(fmt.Sprintf("redis key %s is null", key))
	}
	return val, err
}
