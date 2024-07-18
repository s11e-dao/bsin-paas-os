# json格式介绍

[toc]

### 介绍
drools 文件对应的json格式介绍

#### 前置条件获取事实的方法
通过前置配置可以获取到dubbo服务的参数，根据前端请求的参数拼装请求报文

```json
{
  "before": {
    
  }
}

```

#### 条件类型

```json

{
  "conditions": [
    {
      "logic": "",
      "expression": {
        "left": {
          "type": "bracket",
          "expression": {
            "left": {
              "type": "bracket",
              "expression": {
                "left": {
                  "type": "value",
                  "value": "1"
                },
                "operator": "+",
                "right": {
                  "type": "value",
                  "value": "5"
                }
              }
            },
            "operator": "*",
            "right": {
              "type": "value",
              "value": "3"
            }
          }
        },
        "operator": ">",
        "right": {
          "type": "value",
          "value": "10"
        }
      }
    },
    {
      "logic": "&&",
      "expression": {
        "left": {
          "type": "value",
          "value": "$map.get('sex')"
        },
        "operator": "==",
        "right": {
          "type": "value",
          "value": "\"女\""
        }
      }
    }
  ]
}

```

#### 动作类型

```json

{
  "actions": [
    {
      "type": "globalMap.put",
      "key": "userScore",
      "value": "100"
    },
    {
      "type": "update",
      "map": "$map"
    },
    {
      "type": "print",
      "message": "触发规则：decision_rule_1_11"
    }
  ]
}

```


