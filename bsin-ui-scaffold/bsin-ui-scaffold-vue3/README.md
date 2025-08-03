# BSIN Vue3 脚手架开发指南

## 📋 项目概述

BSIN Vue3 脚手架是一个基于 Vue 3 + Vue Router 4 + Vuex 4 的现代化前端开发脚手架，支持 TypeScript、组合式 API 和微前端架构，提供完整的开发工具链。

## 🏗️ 架构特性

- 🚀 **Vue 3**：最新的 Vue 框架，支持组合式 API
- 🏗️ **TypeScript**：完整的 TypeScript 支持
- 🔧 **Vue Router 4**：现代化的路由管理
- 📦 **Vuex 4**：状态管理解决方案
- 🛡️ **微前端**：基于 qiankun 的微前端架构
- 🐳 **容器化**：支持 Docker 和 CI/CD

## 📁 项目结构

```
bsin-ui-scaffold-vue3/
├── src/                    # 源代码目录
│   ├── components/        # 公共组件
│   ├── views/             # 页面组件
│   ├── router/            # 路由配置
│   ├── store/             # 状态管理
│   ├── services/          # API 服务
│   ├── utils/             # 工具函数
│   ├── types/             # TypeScript 类型定义
│   ├── assets/            # 静态资源
│   ├── App.vue            # 根组件
│   └── main.ts            # 应用入口
├── public/                # 静态资源
├── config/                # 配置文件
├── Dockerfile             # 容器化配置
├── Jenkinsfile            # CI/CD 配置
├── vue.config.js          # Vue CLI 配置
├── package.json           # 依赖配置
└── tsconfig.json          # TypeScript 配置
```

## 🚀 快速开始

### 环境要求
- Node.js 16+
- npm 8+ 或 yarn 1.22+

### 使用步骤

1. **安装依赖**
   ```bash
npm install
```

2. **启动开发服务器**
   ```bash
   npm run serve
   # 或
   npm start
   ```

3. **构建生产版本**
   ```bash
   npm run build
   ```

4. **代码检查**
   ```bash
   npm run lint
   ```

## 📚 开发指南

### 1. 创建组件

```vue
<!-- src/components/ExampleComponent.vue -->
<template>
  <div class="example-component">
    <h1>{{ title }}</h1>
    <div class="content">
      <p>点击次数: {{ count }}</p>
      <button @click="increment" :disabled="loading">
        {{ loading ? '加载中...' : '点击我' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'

// 定义 props
interface Props {
  title?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: '示例组件'
})

// 响应式数据
const count = ref(0)
const loading = ref(false)

// 使用 Vuex store
const store = useStore()

// 方法
const increment = async () => {
  loading.value = true
  try {
    await store.dispatch('user/incrementCount')
    count.value++
  } catch (error) {
    console.error('操作失败:', error)
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  console.log('组件已挂载')
})
</script>

<style scoped>
.example-component {
  padding: 20px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
}

.content {
  margin-top: 16px;
}

button {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}
</style>
```

### 2. 配置路由

```typescript
// src/router/index.ts
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Home from '@/views/Home.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: {
      title: '首页',
      requiresAuth: false
    }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/About.vue'),
    meta: {
      title: '关于我们',
      requiresAuth: false
    }
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('@/views/User.vue'),
    meta: {
      title: '用户管理',
      requiresAuth: true
    },
    children: [
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/List.vue'),
        meta: {
          title: '用户列表'
        }
      },
      {
        path: 'detail/:id',
        name: 'UserDetail',
        component: () => import('@/views/user/Detail.vue'),
        meta: {
          title: '用户详情'
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - BSIN Admin` : 'BSIN Admin'
  
  // 权限检查
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next('/login')
      return
    }
  }
  
  next()
})

export default router
```

### 3. 状态管理

```typescript
// src/store/index.ts
import { createStore } from 'vuex'
import user from './modules/user'
import app from './modules/app'

export default createStore({
  modules: {
    user,
    app
  },
  
  // 全局状态
  state: {
    loading: false,
    error: null
  },
  
  mutations: {
    SET_LOADING(state, loading: boolean) {
      state.loading = loading
    },
    SET_ERROR(state, error: string | null) {
      state.error = error
    }
  },
  
  actions: {
    setLoading({ commit }, loading: boolean) {
      commit('SET_LOADING', loading)
    },
    setError({ commit }, error: string | null) {
      commit('SET_ERROR', error)
    }
  }
})
```

```typescript
// src/store/modules/user.ts
import { Module } from 'vuex'
import { userApi } from '@/services/api'

interface UserState {
  userInfo: any
  count: number
}

const user: Module<UserState, any> = {
  namespaced: true,
  
  state: {
    userInfo: null,
    count: 0
  },
  
  mutations: {
    SET_USER_INFO(state, userInfo: any) {
      state.userInfo = userInfo
    },
    INCREMENT_COUNT(state) {
      state.count++
    }
  },
  
  actions: {
    async fetchUserInfo({ commit }) {
      try {
        const userInfo = await userApi.getUserInfo()
        commit('SET_USER_INFO', userInfo)
        return userInfo
      } catch (error) {
        console.error('获取用户信息失败:', error)
        throw error
      }
    },
    
    incrementCount({ commit }) {
      commit('INCREMENT_COUNT')
    }
  },
  
  getters: {
    isLoggedIn: (state) => !!state.userInfo,
    userName: (state) => state.userInfo?.name || '未登录'
  }
}

export default user
```

### 4. API 服务

```typescript
// src/services/api.ts
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { message } from 'ant-design-vue'

// 创建 axios 实例
const api: AxiosInstance = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers = {
        ...config.headers,
        Authorization: `Bearer ${token}`
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response
    
    if (data.code === 200) {
      return data.data
    } else {
      message.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      // 未授权，跳转到登录页
      localStorage.removeItem('token')
      window.location.href = '/login'
    } else {
      message.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

// API 接口定义
export interface User {
  id: number
  name: string
  email: string
  avatar?: string
}

export const userApi = {
  // 获取用户信息
  getUserInfo: () => api.get<User>('/user/info'),
  
  // 获取用户列表
  getUsers: (params?: any) => api.get<User[]>('/users', { params }),
  
  // 创建用户
  createUser: (data: Partial<User>) => api.post<User>('/users', data),
  
  // 更新用户
  updateUser: (id: number, data: Partial<User>) => 
    api.put<User>(`/users/${id}`, data),
  
  // 删除用户
  deleteUser: (id: number) => api.delete(`/users/${id}`)
}

export default api
```

### 5. 微前端配置

```typescript
// src/main.ts
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import { registerMicroApps, start } from 'qiankun'

const app = createApp(App)

app.use(router)
app.use(store)

app.mount('#app')

// 微前端配置
registerMicroApps([
  {
    name: 'react-app',
    entry: '//localhost:3000',
    container: '#micro-app',
    activeRule: '/react-app'
  },
  {
    name: 'vue-app',
    entry: '//localhost:3001',
    container: '#micro-app',
    activeRule: '/vue-app'
  }
])

start()
```

### 6. 组合式函数

```typescript
// src/composables/useUser.ts
import { ref, computed } from 'vue'
import { useStore } from 'vuex'
import { userApi, User } from '@/services/api'

export function useUser() {
  const store = useStore()
  const loading = ref(false)
  const users = ref<User[]>([])

  const isLoggedIn = computed(() => store.getters['user/isLoggedIn'])
  const userName = computed(() => store.getters['user/userName'])

  const fetchUsers = async (params?: any) => {
    loading.value = true
    try {
      const data = await userApi.getUsers(params)
      users.value = data
      return data
    } catch (error) {
      console.error('获取用户列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const createUser = async (userData: Partial<User>) => {
    try {
      const user = await userApi.createUser(userData)
      users.value.push(user)
      return user
    } catch (error) {
      console.error('创建用户失败:', error)
      throw error
    }
  }

  return {
    loading,
    users,
    isLoggedIn,
    userName,
    fetchUsers,
    createUser
  }
}
```

## 🧪 测试

### 单元测试

```typescript
// tests/unit/ExampleComponent.spec.ts
import { mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import ExampleComponent from '@/components/ExampleComponent.vue'

describe('ExampleComponent', () => {
  let store: any

  beforeEach(() => {
    store = createStore({
      modules: {
        user: {
          namespaced: true,
          actions: {
            incrementCount: jest.fn()
          }
        }
      }
    })
  })

  it('renders correctly', () => {
    const wrapper = mount(ExampleComponent, {
      global: {
        plugins: [store]
      }
    })
    
    expect(wrapper.find('h1').text()).toBe('示例组件')
    expect(wrapper.find('button').text()).toBe('点击我')
  })

  it('increments count when button clicked', async () => {
    const wrapper = mount(ExampleComponent, {
      global: {
        plugins: [store]
      }
    })
    
    await wrapper.find('button').trigger('click')
    expect(store.dispatch).toHaveBeenCalledWith('user/incrementCount')
  })
})
```

## 🔧 配置说明

### Vue CLI 配置

```javascript
// vue.config.js
const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  
  // 开发服务器配置
  devServer: {
    port: 8080,
    proxy: {
      '/api': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  },
  
  // 构建配置
  configureWebpack: {
    resolve: {
      alias: {
        '@': require('path').resolve(__dirname, 'src')
      }
    }
  },
  
  // CSS 配置
  css: {
    loaderOptions: {
      less: {
        lessOptions: {
          javascriptEnabled: true
        }
      }
    }
  }
})
```

### TypeScript 配置

```json
// tsconfig.json
{
  "compilerOptions": {
    "target": "esnext",
    "module": "esnext",
    "strict": true,
    "jsx": "preserve",
    "moduleResolution": "node",
    "skipLibCheck": true,
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    "forceConsistentCasingInFileNames": true,
    "useDefineForClassFields": true,
    "sourceMap": true,
    "baseUrl": ".",
    "types": [
      "webpack-env"
    ],
    "paths": {
      "@/*": [
        "src/*"
      ]
    },
    "lib": [
      "esnext",
      "dom",
      "dom.iterable"
    ]
  },
  "include": [
    "src/**/*.ts",
    "src/**/*.tsx",
    "src/**/*.vue",
    "tests/**/*.ts",
    "tests/**/*.tsx"
  ],
  "exclude": [
    "node_modules"
  ]
}
```

## 🐳 容器化部署

### Docker 配置

```dockerfile
# Dockerfile
FROM node:16-alpine as build-stage

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

FROM nginx:stable-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Nginx 配置

```nginx
# nginx.conf
server {
    listen 80;
    server_name localhost;
    
    location / {
        root /usr/share/nginx/html;
        index index.html index.htm;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://backend:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### CI/CD 配置

```groovy
// Jenkinsfile
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Install') {
            steps {
                sh 'npm install'
            }
        }
        
        stage('Test') {
            steps {
                sh 'npm run test:unit'
            }
        }
        
        stage('Build') {
            steps {
                sh 'npm run build'
            }
        }
        
        stage('Deploy') {
            steps {
                sh 'docker build -t bsin-vue3-app .'
                sh 'docker push bsin-vue3-app'
            }
        }
    }
}
```

## 📚 参考资源

### 官方文档
- [Vue 3 官方文档](https://vuejs.org/)
- [Vue Router 4 官方文档](https://router.vuejs.org/)
- [Vuex 4 官方文档](https://vuex.vuejs.org/)
- [qiankun 微前端文档](https://qiankun.umijs.org/)

### 开发工具
- [VS Code](https://code.visualstudio.com/)
- [Vue DevTools](https://chrome.google.com/webstore/detail/vuejs-devtools/)
- [Chrome DevTools](https://developer.chrome.com/docs/devtools/)

---

**最后更新**: 2024年12月  
**版本**: 3.0.0-SNAPSHOT
