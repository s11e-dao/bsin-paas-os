---
title:  'API接口报文文档'
date: 2023-12-25 09:09:57
permalink: /pages/1b12f2

---
API
[toc]

## 公共请求头
请求头报文示例

字段       |字段类型       |必输项 |字段说明
------------|-----------|-----------|-----------
Authorization       | string        | 否 | 登录接口不需要
version       | string        | 是 | 版本号
timestamp       | string        | 是 | 时间戳
appKey       | string        | 是 | 应用key
sign       | string        | 是 | 签名信息


## 响应公共报文
请求报文示例
```
{
  "data": {
    "token": ""
  },
  "code": 0,
  "message": "SUCCESS"
}
或
{
  "data": [
    {
        "token": "c"
    },
    {
        "token": ""
    },
  ],
  "code": 0,
  "message": "SUCCESS",
  "pagination": null
}
```
code为0是成功，非0是失败

## 报文的请求头
Authorization：是通过getAccessToken接口获取到的accessToken
将 Authorization 放到请求头作为请求参数


## 测试环境地址
> http://ip:9195/



## 接口签名

那么数据保有方为了控制调用权限，会要求第三方开发者在自己的网站进行注册，并为其分配唯一的appKey 、 appSecert和预定义的加密方式
appKey 是为了保证该调用请求是平台授权过的调用方发出的，保证请求方唯一性，如 test01，如果发现 appKey 不再注册库中则认为该请求方不合法；appSecert 是组成签名的一部分，增加暴力解密的难度的，通常是一段密文，如 SECERT_A；
预定义加密方式是双方约定好的加密方式，一般为散列非可逆加密，如 md5Hex、SHA1；

### 签名生成规则：

1、将请求报文的参数移除sign字段外，按首字母排序，并按照“参数参数值”的模式将字符拼接成字符串，参数为空的字段移除
```
namespidermanmovieSpider-Man
```
2. 将秘钥SECERT_A加入上述字符串的前后成，生成新的字符串

namespidermanmovieSpider-ManSECERT_A

3. 按约定方式（如 md5Hex）加密

2995c83bbc12b147c9b5645396e5700e6af92b7f

4. 拼接所有参数，组成API请求

```
{
    "appId":"1540917598076669953",
    "tenantId":"1540917104662941696"
}

```
示例
```
public class Test1 {
  public static void main(String[] args) {
    Map<String, String> map = Maps.newHashMapWithExpectedSize(3);
    //timestamp为毫秒数的字符串形式 String.valueOf(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli())
    map.put("timestamp","1660658725000");  //值应该为毫秒数的字符串形式
    map.put("path", "/http/order/save");
    map.put("version", "1.0.0");

    List<String> storedKeys = Arrays.stream(map.keySet()
                    .toArray(new String[]{}))
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());
    final String sign = storedKeys.stream()
            .map(key -> String.join("", key, map.get(key)))
            .collect(Collectors.joining()).trim()
            .concat("2D47C325AE5B4A4C926C23FD4395C719");
    System.out.println(sign);

    System.out.println(DigestUtils.md5DigestAsHex(sign.getBytes()).toUpperCase());
  }
}
```



## 登录请求示例
### 功能描述
用户输入登录信息登录应用
### 请求说明
> 请求方式：POST<br>
请求URL ：http://ip:9195/uc/login

### 请求参数
字段       |字段类型       |必输项 |字段说明
------------|-----------|-----------|-----------
phone       | string        | 是 | 用户名称|手机号
password       | string        | 是 | 密码
areaCode       | string        | 是 | 手机区号

> 请求示例
```
{
    "phone":"1657030464275",
    "password":"1657030464275",
    "areaCode":"1657030464275",
}
```

### 返回参数
字段       |字段类型       |字段说明
------------|-----------|-----------

### 返回示例
```json  
{
    "code": 0,
    "message": "操作成功",
    "data": {
      "phone":"1657030464275",
      "password":"1657030464275",
      "areaCode":"1657030464275",
      "token": "46089459af9e0850376041e2abf35202"
    }
}
```


##  错误状态码
状态码       |说明
------------|-----------
3001       |其他认证错误信息！
3002       |用户不存在！
3003       |用户名或密码有误！
