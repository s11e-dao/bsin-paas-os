# json格式介绍

[toc]

### 介绍
drools 文件对应的json格式介绍

### 条件类型

```json

{
  "conditions": [
    {
      "logic": "&&",
      "type": "equals",
      "left": [
        {
          "arithmeticn": "",
          "type": "括号",
          "content": [
            {
              "arithmeticn": "",
              "type": "值类型",
              "valus": "3"
            },
            {
              "arithmeticn": "*",
              "type": "值类型",
              "valus": "3"
            }
          ]
        },
        {
          "arithmeticn": "*",
          "type": "值类型",
          "valus": "3"
        }
      ],
      "right": [
        {
          "type": "值类型",
          "valus": "女"
        }
      ]
    },
    {
      "logic": "&&",
      "type": "equals",
      "left": [
        {
          "arithmeticn": "",
          "type": "括号",
          "content": [
            {
              "arithmeticn": "",
              "type": "值类型",
              "valus": "3"
            },
            {
              "arithmeticn": "*",
              "type": "值类型",
              "valus": "3"
            }
          ]
        },
        {
          "arithmeticn": "*",
          "type": "值类型",
          "valus": "3"
        }
      ],
      "right": [
        {
          "right": "女"
        }
      ]
    }
  ]
}

```

### 动作类型

