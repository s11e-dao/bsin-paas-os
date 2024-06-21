## milvus

### 字段设计
- 方案一
>* 一个Tenant创建一个库-database
>* 一个merchant创建一个collection,用merchantNo作为collection的name,若名字不支持数字则
>* retrival
>* 用户的chat memory context存储
>* 字段设计

|  id |  customer_no |  ai_no | text |vector  |
|  ----  | ----  | ----  |----  |----  |
| 主键  | 客户ID |绑定的知识库 |chunk文本 | 向量数据 |


- 方案二
  一个Tenant创建一个库-database
>* 两个collection：merchantCollection和customerCollection
>* merchantCollection字段设计: 基于知识库聊天，ai_no 为知识库ID

|  id | type                                         | customer_no |  ai_no |  knowledge_base_file_no | text |vector  |
|  ----  |----------------------------------------------|--------------| ----  | ----  | ----  | ----  |
| 主键  | 类型： 1-知识库 2-chatHistory 3-chatHistorySummary | 客户ID         | 绑定的知识库ID | 绑定的知识库文件ID | chunk后的文本 | 向量数据 |


>* customerCollection 字段设计： 和copilot|agent|品牌官|数字分身(user to user), ai_no为 copilot_no|agent_no|品牌馆_no|customer_no
   存储chat history  
   存储summary history  
   summary中包含时间

|  id | type                                         | customer_no |  ai_no |  knowledge_base_file_no | text |vector  |
|  ----  |----------------------------------------------| ----  | ----  | ----  | ----  | ----  |
| 主键  | 类型： 1-知识库 2-chatHistory 3-chatHistorySummary | 客户ID| 绑定的知识库ID | 绑定的知识库文件ID | chunk后的文本 | 向量数据 |

