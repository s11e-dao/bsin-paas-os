## prompt解读
### 1. 什么是system，user，assistant？
要与OpenAI ChatGPT模型进行对话，您必须在消息数组中提供一个或多个消息。每个消息必须与system、user或assistant中的一个角色相关联。角色表示消息作者的角色。根据角色，ChatGPT了解它应该如何行为以及谁在发起调用。让我们讨论每个角色
- system(系统角色)
>* 它设定了 AI 的行为和角色，和背景。有助于通过分配特定行为给聊天助手来创建对话的上下文或范围，并非必需，但它有助于在内部为对话设置模型行为
>* 常常用于开始对话，给出一个对话的大致方向，或者设置对话的语气和风格。
>* 例如，可以把它设置为：“你是一个助理”或“你是一名历史教师”。这个消息可以帮助设定对话的语境，以便 AI 更好地理解其在对话中的角色。
>* 也可以更加详细地进行设置。比如说，你需要一个导游，可以把它设置为：“我想让你做一个导游。我会把我的位置写给你，你会推荐一个靠近我的位置的地方。在某些情况下，我还会告诉您我将访问的地方类型。您还会向我推荐靠近我的第一个位置的类似类型的地方。”
>* [推荐获得8万星的GitHub提示词大全](https://github.com/f/awesome-chatgpt-prompts)

- user(用户角色)
>* 代表实际的最终用户，他正在向ChatGPT发送提示
>* 就是我们输入的问题或请求。
>* 比如说“北京王府井附近有什么值得去的地方？”

- assistant(助手角色)
>* 代表响应最终用户提示的实体:这个角色表示消息是助手（聊天模型）的响应。"assistant"角色用于在当前请求中设置模型的先前响应，以保持对话的连贯性。
>* 在使用 API 的过程中，你不需要直接生成 assistant 消息，因为它们是由 API 根据 system 和 user 消息自动生成的。
~~~
 curl -s https://api.openai.com/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR-API-KEY" \
  -d '{
    "model": "gpt-3.5-turbo-16k",
    "messages": [
          {
             "role": "system",
             "content": "您是足球专家"
          },
          {
             "role": "user",
             "content": "谁赢得了2018年的FIFA世界杯？"
          },
          {
             "role": "assistant",
             "content": "法国赢得了2018年的FIFA世界杯。"
          },
          {
             "role": "user",
             "content": "下一届FIFA世界杯什么时候举行？"
          }
        ]
  }'
# 此外，在消息数组中，您可以将角色设置为“assistant”以用于最后的消息对象，它可以是自定义消息。如果消息的角色是“assistant”，ChatGPT会验证最后一条消息的内容的正确性
{
  "id": "chatcmpl-7WryqleJdvZzz4vtdQs4erG3Kx7mT",
  "object": "chat.completion",
  "created": 1688068352,
  "model": "gpt-3.5-turbo-16k-0613",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "下一届FIFA世界杯计划于2026年举行，将由加拿大、墨西哥和美国联合举办。"
      },
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 51,
    "completion_tokens": 31,
    "total_tokens": 82
  }
}
~~~


## [完整上下文组成](https://weixin110.qq.com/cgi-bin/mmspamsupport-bin/newredirectconfirmcgi?click=d869f19ce80395c5ecf47159bc069657&bankey=a7ee9f43125a2d93ff83e301c92f6e54&midpagecode=67377a2adb44e17c1b0adb24b5cf2bd12c34d9b56e06ccd6dd4c291b423b5bd7ff6dabdc557c992f5d60d892b6870f746be01453da89926dc75a288449d956755aaf6f63daac23999ef9e61a2873e167c5996d9fecd36ae7c558cfc258943c0c&bancode=5e0617703374d062718cd2fd0cd24e3b97aeb23466f8ccc110a0e571e360143a350a9be702fb7317bc46dd7ac53e6e7b47e0c3b77e09d959babcf21222de4bd2&exportkey=n_ChQIAhIQ4cvgHstrfpVa5UfYBEQHOxL4AQIE97dBBAEAAAAAAJEbJ8yHEwwAAAAOpnltbLcz9gKNyK89dVj0tf91%2FwksZjTHS%2BXUHkgDWRPXOMqiVKAbBgR5CWAy8u5Ee4KbCKApjoy8LPSTSSHHLG3TEP%2FKuhh8LXxLUQLKOUk%2FHOXpNRJu%2BZWfGL4EiSMMUIu830cfImsJpiP42RBc3kfslpTYeGzZX4xOootArfo2xzwbfDmpAz0j%2F2fq5O8tUy9TQsqMq1ibL4BJzCVoxv0aQYncw8BWIWacVlV00zGwYIfFnjav0uptllVqsvUspaZjfbcRMkHHxnwnDl5hHvTlEHOCRwDJR8lT8Y6ZfDf%2B&pass_ticket=CpLYArdMRQFFHtA4eh9lXIZKyZg9IbLj7R8CZ2CkUUQ1xlhnXs0RRJW3PFbxp8xU7BUVXloPQ7sQGAfvvTB1Kg%3D%3D&wechat_real_lang=zh_CN&wx_header=0)
最终发送给 LLM 大模型的数据是一个messages数组，内容和顺序如下：
~~~
"messages": [
      {
         "role": "system",
         "说明"： "由systemPromptTemplate和systemRole组成，若限定词不为空，接上限定词",
         "content": "你是{{systemRole}}" + “{{determiner}}”
      }, 
      {
         "role": "system",
         "说明"："knowledgeBase",
         "content": "你的回答需要满足以下要求:\n{{knowledgeBase}}\n"
      },
      {
         "role": "system",
         "说明"："chatHistorySummary",
         "content": "以下是你与用户过往的对话记录总结，仅供参考:\n{{chatHistorySummary}}\n"
      },
      
      // K条聊天记录 
      {
         "role": "user",
         "说明"："用户的上一个输入",
         "content": "lasht question"
      }
      {
         "role": "assistant",
         "说明"："AI的上一个回复",
         "content": "last answer"
      }
      
      
      {
         "role": "user",
         "说明"："用户的输入",
         "content": "{{question}}"
      }
    ]
~~~

- 系统提示词-systemRole:
被放置在上下文数组的最前面，role 为 system，用于引导模型。具体用法参考各搜索引擎的教程~
~~~
You are a helpful {{systemRole}}.
~~~

- 限定词-determiner:
    与系统提示词类似，role 也是 system 类型，只不过位置会被放置在问题前，拥有更强的引导作用。
~~~
你的回答需要满足以下要求:
{{determiner}}
~~~

- 引用内容:知识库
role 也是 **system** 类型，主要是由【知识库搜索】模块生成，也可以由 HTTP 模块从外部引入。数据结构示例如下
~~~
你的回答可以参考以下内容:
{{knowledgeBase}}
~~~

- 聊天记录summary:
role为 assistant?? system
~~~
以下是你与用户过往的对话记录总结，仅供参考: 
{{chatHistorySummary}}
~~~

- chatBufferWindow聊天记录:
需要将最近K条历史聊天记录逐条加入到message中
~~~
 curl -s https://api.openai.com/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR-API-KEY" \
  -d '{
    "model": "gpt-3.5-turbo-16k",
    "messages": [
          {
             "role": "system",
             "content": "您是足球专家"
          },
          {
                 "role": "user",
                 "content": "谁赢得了2018年的FIFA世界杯？"
          },
          {
                 "role": "assistant",
                 "content": "法国赢得了2018年的FIFA世界杯。"
          },
          {
                 "role": "user",
                 "content": "下一届FIFA世界杯什么时候举行？"
          }
        ]
  }'
~~~


- 问题:
role为user，加入到最后一条记录
~~~
{{question}}
~~~

