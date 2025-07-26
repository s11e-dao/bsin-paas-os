# BSIN React UmiJS 4 脚手架开发指南

## 📋 项目概述

BSIN React UmiJS 4 脚手架是一个基于 React 18 + UmiJS 4 + Ant Design 5 的企业级前端开发脚手架，支持微前端架构和 TypeScript，提供完整的开发工具链。

## 🏗️ 架构特性

- 🚀 **微前端架构**：基于 qiankun 的微前端解决方案
- 🏗️ **TypeScript**：完整的 TypeScript 支持
- 🔧 **Ant Design 5**：企业级 UI 组件库
- 📦 **E2E 测试**：基于 Cypress 的端到端测试
- 🛡️ **代码质量**：ESLint + Prettier 代码规范

## 📁 项目结构

```
bsin-ui-scaffold-react-umi4/
├── src/                    # 源代码目录
│   ├── pages/             # 页面组件
│   │   ├── Home/          # 首页
│   │   ├── Params/        # 全局参数
│   │   ├── UserCenter/    # 用户中心
│   │   └── uncontainer/   # 非基座运行
│   ├── layouts/           # 布局组件
│   │   └── index.tsx      # 主布局
│   ├── wrappers/          # 路由包装器
│   │   └── auth.tsx       # 权限控制
│   ├── components/        # 公共组件
│   ├── services/          # API 服务
│   ├── utils/             # 工具函数
│   ├── types/             # TypeScript 类型定义
│   └── app.tsx            # 应用入口
├── config/                # 配置文件
│   ├── routes.ts          # 路由配置
│   └── config.ts.example  # 配置示例
├── cypress/               # E2E 测试配置
├── .umirc.ts              # UmiJS 主配置
├── cypress.config.ts      # Cypress 配置
├── package.json           # 依赖配置
├── tsconfig.json          # TypeScript 配置
└── .gitignore             # Git 忽略文件
```

## 🚀 快速开始

### 环境要求
- Node.js 16+
- npm 8+ 或 yarn 1.22+ 或 pnpm 7+

### 使用步骤

1. **安装依赖**
   ```bash
   npm install
   # 或
   yarn install
   # 或
   pnpm install
   ```

2. **启动开发服务器**
   ```bash
   npm run dev
   # 或
   yarn dev
   # 或
   pnpm dev
   ```

3. **构建生产版本**
   ```bash
   npm run build
   ```

4. **预览生产版本**
   ```bash
   npm run preview
   ```

## 📚 开发指南

### 1. 创建页面

```typescript
// src/pages/example/index.tsx
import React from 'react';
import { PageContainer } from '@ant-design/pro-components';
import { Card, Button, message } from 'antd';

const ExamplePage: React.FC = () => {
  const handleClick = () => {
    message.success('点击成功！');
  };

  return (
    <PageContainer>
      <Card title="示例页面">
        <Button type="primary" onClick={handleClick}>
          点击我
        </Button>
      </Card>
    </PageContainer>
  );
};

export default ExamplePage;
```

### 2. 配置路由

#### 2.1 路由配置文件结构

项目使用分离式的路由配置，主要包含以下文件：

```typescript
// config/routes.ts - 路由配置
const routes = [
  {
    path: '/',
    component: '@/layouts/index',
    routes: [
      {
        path: '/',
        redirect: '/home',
      },
      {
        name: '首页',
        path: '/home',
        component: '@/pages/Home/index',
        wrappers: ['@/wrappers/auth'],
      },
      {
        name: '全局参数',
        path: '/params',
        component: '@/pages/Params/index',
        wrappers: ['@/wrappers/auth'],
      },
      {
        name: '用户中心',
        path: '/userCenter',
        component: '@/pages/UserCenter/index',
        wrappers: ['@/wrappers/auth'],
      },
      {
        name: '非基座运行',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
      }
    ]
  }
];

export default routes;
```

#### 2.2 主配置文件

```typescript
// .umirc.ts - 主配置文件
import routes from './config/routes'

export default {
  esbuildMinifyIIFE: true,
  // 开启request
  request: {
    dataField: '', //空为拿到后端的原始数据
  },
  define: {
    // 后台访问地址
    'process.env.baseUrl': 'http://127.0.0.1:8097/gateway',
    'process.env.ipfsApiUrl': 'https://ipfsadmin.s11edao.com/api/v0',
    'process.env.ipfsGatewauUrl': 'https://ipfs.s11edao.com/ipfs/',
    'process.env.fileUrl': 'http://file.s11edao.com/jiujiu/',
    'process.env.bsinFileUploadUrl': 'http://127.0.0.1:8097/bsinFileUpload',
    'process.env.storeMethod': '3',
    'process.env.biganH5Url': 'http://localhost:8080/',
    'process.env.tenantAppType': 'ai',
    'process.env.webScoketUrl': 'ws://192.168.1.6:8126/websocket',
  },
  model: {},
  qiankun: {
    master: {
      apps: [
        {
          name: 'bsin-ui-upms',
          entry: 'http://127.0.0.1:8001',
        },
      ],
      routes: [
        {
          path: '/bsin-ui-upms',
          microApp: 'bsin-ui-upms',
        },
      ],
    },
    slave: {},
  },
  base: '/bsin-ui-asset-management',
  routes,
  hash: true,
  history: {
    type: 'hash',
  },
  locale: {
    default: 'zh-CN',
    baseSeparator: '-',
  },
};
```

#### 2.3 路由配置说明

- **path**: 路由路径
- **name**: 路由名称（用于菜单显示）
- **component**: 组件路径
- **wrappers**: 路由包装器（用于权限控制、布局等）
- **redirect**: 重定向路径
- **microApp**: 微前端子应用名称

#### 2.4 当前路由结构

```
/
├── /home                    # 首页
├── /params                  # 全局参数
├── /userCenter              # 用户中心
├── /uncontainer             # 非基座运行
└── /bsin-ui-upms           # 微前端子应用 - 权限管理
```

#### 2.5 路由开发最佳实践

**1. 路由命名规范**
```typescript
// 推荐：使用小写字母和短横线
{
  path: '/user-center',
  name: '用户中心',
}

// 不推荐：使用驼峰命名
{
  path: '/userCenter',
  name: '用户中心',
}
```

**2. 权限控制**
```typescript
// 使用 wrappers 进行权限控制
{
  path: '/admin',
  component: '@/pages/Admin',
  wrappers: ['@/wrappers/auth', '@/wrappers/admin'],
}
```

**3. 动态路由**
```typescript
// 支持参数传递的动态路由
{
  path: '/user/:id',
  component: '@/pages/User/Detail',
  hideInMenu: true, // 动态路由通常不在菜单中显示
}
```

**4. 路由懒加载**
```typescript
// 使用动态导入实现懒加载
{
  path: '/heavy-page',
  component: () => import('@/pages/HeavyPage'),
}
```

**5. 微前端路由**
```typescript
// 微前端子应用路由配置
{
  path: '/sub-app',
  microApp: 'sub-app-name',
  microAppProps: {
    autoSetLoading: true,
    className: 'sub-app-container',
  },
}
```

### 3. 创建布局组件

```typescript
// src/layouts/BasicLayout.tsx
import React from 'react';
import { ProLayout } from '@ant-design/pro-components';
import { Link, useLocation } from 'umi';

const BasicLayout: React.FC = ({ children }) => {
  const location = useLocation();

  const menuData = [
    {
      path: '/example',
      name: '示例页面',
      icon: 'HomeOutlined',
    },
    {
      path: '/user',
      name: '用户管理',
      icon: 'UserOutlined',
      children: [
        {
          path: '/user/list',
          name: '用户列表',
        },
        {
          path: '/user/detail',
          name: '用户详情',
        },
      ],
    },
  ];

  return (
    <ProLayout
      title="BSIN Admin"
      menuData={menuData}
      location={location}
      menuItemRender={(item, dom) => (
        <Link to={item.path || '/'}>{dom}</Link>
      )}
    >
      {children}
    </ProLayout>
  );
};

export default BasicLayout;
```

### 4. API 服务

```typescript
// src/services/api.ts
import { request } from 'umi';

export interface User {
  id: number;
  name: string;
  email: string;
}

export const userApi = {
  // 获取用户列表
  getUsers: (params?: any) =>
    request<User[]>('/api/users', {
      method: 'GET',
      params,
    }),

  // 获取用户详情
  getUser: (id: number) =>
    request<User>(`/api/users/${id}`, {
      method: 'GET',
    }),

  // 创建用户
  createUser: (data: Partial<User>) =>
    request<User>('/api/users', {
      method: 'POST',
      data,
    }),

  // 更新用户
  updateUser: (id: number, data: Partial<User>) =>
    request<User>(`/api/users/${id}`, {
      method: 'PUT',
      data,
    }),

  // 删除用户
  deleteUser: (id: number) =>
    request(`/api/users/${id}`, {
      method: 'DELETE',
    }),
};
```

### 5. 微前端配置

#### 5.1 微前端架构说明

项目基于 qiankun 微前端框架，支持主应用和子应用的集成。

#### 5.2 主应用配置

```typescript
// .umirc.ts
export default {
  qiankun: {
    master: {
      // 注册权限管理信息
      apps: [
        {
          name: 'bsin-ui-upms', // 唯一 id
          entry: 'http://127.0.0.1:8001', // html entry
          // entry: 'http://copilotupms.s11edao.com',
        },
      ],
      routes: [
        {
          path: '/bsin-ui-upms',
          microApp: 'bsin-ui-upms',
        },
      ],
    },
    slave: {},
  },
  base: '/bsin-ui-asset-management',
};
```

#### 5.3 子应用配置

```typescript
// 子应用需要配置 qiankun slave
export default {
  qiankun: {
    slave: {},
  },
};
```

#### 5.4 微前端路由集成

```typescript
// config/routes.ts
const routes = [
  {
    path: '/',
    component: '@/layouts/index',
    routes: [
      // ... 其他路由
      {
        name: '权限管理',
        path: '/bsin-ui-upms',
        microApp: 'bsin-ui-upms',
      },
    ],
  },
];
```

#### 5.5 微前端生命周期

```typescript
// 子应用生命周期配置
export const qiankun = {
  // 应用加载之前
  async bootstrap() {
    console.log('react app bootstraped');
  },
  // 应用 render 之前触发
  async mount(props) {
    console.log('props from main framework', props);
  },
  // 应用卸载之后触发
  async unmount(props) {
    console.log('unmount', props);
  },
};
```

### 6. 状态管理

```typescript
// src/models/user.ts
import { useState } from 'react';
import { userApi, User } from '@/services/api';

export default () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchUsers = async (params?: any) => {
    setLoading(true);
    try {
      const data = await userApi.getUsers(params);
      setUsers(data);
    } catch (error) {
      console.error('获取用户列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  return {
    users,
    loading,
    fetchUsers,
  };
};
```

## 🧪 测试

### 单元测试

```typescript
// src/pages/example/index.test.tsx
import { render, screen, fireEvent } from '@testing-library/react';
import ExamplePage from './index';

describe('ExamplePage', () => {
  it('should render correctly', () => {
    render(<ExamplePage />);
    expect(screen.getByText('示例页面')).toBeInTheDocument();
  });

  it('should show success message when button clicked', () => {
    render(<ExamplePage />);
    const button = screen.getByText('点击我');
    fireEvent.click(button);
    expect(screen.getByText('点击成功！')).toBeInTheDocument();
  });
});
```

### E2E 测试

```typescript
// cypress/e2e/example.cy.ts
describe('Example Page', () => {
  it('should display the example page', () => {
    cy.visit('/example');
    cy.get('h1').should('contain', '示例页面');
  });

  it('should show success message when button clicked', () => {
    cy.visit('/example');
    cy.get('button').click();
    cy.get('.ant-message-success').should('be.visible');
  });
});
```

## 🔧 配置说明

### UmiJS 配置

#### 核心配置项

```typescript
// .umirc.ts
export default {
  // 构建优化
  esbuildMinifyIIFE: true,
  
  // 请求配置
  request: {
    dataField: '', // 空为拿到后端的原始数据
  },
  
  // 环境变量定义
  define: {
    'process.env.baseUrl': 'http://127.0.0.1:8097/gateway',
    'process.env.ipfsApiUrl': 'https://ipfsadmin.s11edao.com/api/v0',
    'process.env.ipfsGatewauUrl': 'https://ipfs.s11edao.com/ipfs/',
    'process.env.fileUrl': 'http://file.s11edao.com/jiujiu/',
    'process.env.bsinFileUploadUrl': 'http://127.0.0.1:8097/bsinFileUpload',
    'process.env.storeMethod': '3',
    'process.env.biganH5Url': 'http://localhost:8080/',
    'process.env.tenantAppType': 'ai',
    'process.env.webScoketUrl': 'ws://192.168.1.6:8126/websocket',
  },
  
  // 微前端配置
  qiankun: {
    master: {
      apps: [
        {
          name: 'bsin-ui-upms',
          entry: 'http://127.0.0.1:8001',
        },
      ],
      routes: [
        {
          path: '/bsin-ui-upms',
          microApp: 'bsin-ui-upms',
        },
      ],
    },
    slave: {},
  },
  
  // 应用基础路径
  base: '/bsin-ui-asset-management',
  
  // 路由配置
  routes,
  
  // 路由模式
  hash: true,
  history: {
    type: 'hash',
  },
  
  // 国际化配置
  locale: {
    default: 'zh-CN',
    baseSeparator: '-',
  },
};
```

#### 环境变量说明

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| `baseUrl` | 后台访问地址 | `http://127.0.0.1:8097/gateway` |
| `ipfsApiUrl` | IPFS API 地址 | `https://ipfsadmin.s11edao.com/api/v0` |
| `ipfsGatewauUrl` | IPFS Gateway 地址 | `https://ipfs.s11edao.com/ipfs/` |
| `fileUrl` | 本地服务器文件前缀 | `http://file.s11edao.com/jiujiu/` |
| `bsinFileUploadUrl` | 文件上传地址 | `http://127.0.0.1:8097/bsinFileUpload` |
| `storeMethod` | 存储方式 | `3` (IPFS + OSS) |
| `biganH5Url` | Bigan H5 地址 | `http://localhost:8080/` |
| `tenantAppType` | 租户应用类型 | `ai` |
| `webScoketUrl` | WebSocket 地址 | `ws://192.168.1.6:8126/websocket` |

#### 存储方式说明

- `1`: IPFS 存储（需要同时指定 backup 存储平台）
- `2`: 阿里云 OSS 存储
- `3`: IPFS + 阿里云 OSS 双重存储
- `4`: 服务器本地存储
- `5`: IPFS + 服务器本地存储

### TypeScript 配置

```json
// tsconfig.json
{
  "compilerOptions": {
    "target": "esnext",
    "module": "esnext",
    "moduleResolution": "node",
    "importHelpers": true,
    "jsx": "react-jsx",
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    "sourceMap": true,
    "baseUrl": "./",
    "strict": true,
    "paths": {
      "@/*": ["src/*"],
      "@@/*": ["src/.umi/*"]
    }
  },
  "include": [
    "src/**/*",
    "config/**/*",
    ".umirc.ts"
  ]
}
```

## 📦 构建和部署

### 构建命令

```bash
# 开发环境
npm run dev

# 生产环境构建
npm run build

# 预览生产版本
npm run preview

# E2E 测试
npm run test:local
```

### Docker 部署

```dockerfile
# Dockerfile
FROM node:16-alpine as builder

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 📚 参考资源

### 官方文档
- [UmiJS 官方文档](https://umijs.org/)
- [React 官方文档](https://react.dev/)
- [Ant Design 官方文档](https://ant.design/)
- [qiankun 微前端文档](https://qiankun.umijs.org/)

### 开发工具
- [VS Code](https://code.visualstudio.com/)
- [Chrome DevTools](https://developer.chrome.com/docs/devtools/)
- [React Developer Tools](https://chrome.google.com/webstore/detail/react-developer-tools/)

---

**最后更新**: 2024年12月  
**版本**: 3.0.0-SNAPSHOT

