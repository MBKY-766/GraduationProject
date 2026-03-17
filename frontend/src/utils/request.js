import axios from 'axios'

// 创建Axios实例
const service = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL, // 后端接口前缀（从环境变量读取）
    timeout: 30000 // 检测接口超时时间延长到30秒（AI推理需要时间）
})

// 请求拦截器（添加token）
service.interceptors.request.use(
    (config) => {
        // 从本地存储获取token，添加到请求头
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = 'Bearer ' + token
        }

        // 【修改2】文件上传时自动处理Content-Type（避免手动设置冲突）
        if (config.data instanceof FormData) {
            // 移除手动设置的Content-Type，让浏览器自动添加boundary
            delete config.headers['Content-Type']
        }

        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器（统一处理错误）
service.interceptors.response.use(
    (response) => {
        const res = response.data
        // 假设后端返回code=200为成功
        if (res.code !== 200) {
            // 提示错误信息（用Element Plus的ElMessage）
            import('element-plus').then(({ ElMessage }) => {
                ElMessage.error(res.msg || '请求失败')
            })
            return Promise.reject(new Error(res.msg || '请求失败'))
        } else {
            return res
        }
    },
    (error) => {
        // 优化错误提示：区分不同错误类型
        let errorMsg = '服务器错误'
        if (error.code === 'ECONNABORTED') {
            errorMsg = '请求超时，请检查后端服务是否正常'
        } else if (error.response) {
            errorMsg = `请求失败（${error.response.status}）：${error.message}`
        }

        import('element-plus').then(({ ElMessage }) => {
            ElMessage.error(errorMsg || '服务器错误')
        })
        return Promise.reject(error)
    }
)

export default service