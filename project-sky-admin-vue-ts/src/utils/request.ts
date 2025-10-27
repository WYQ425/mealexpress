import axios, { AxiosAdapter } from 'axios'
import { Message, MessageBox } from 'element-ui'
import { UserModule } from '@/store/modules/user'
import {
  getRequestKey,
  pending,
  checkPending,
  removePending
} from './requestOptimize'
import router from '@/router'
const CancelToken = axios.CancelToken;

const resolvedBaseURL = process.env.VUE_APP_BASE_API || '/api/admin'
if (process.env.NODE_ENV !== 'production') {
  // eslint-disable-next-line no-console
  console.info('[axios] baseURL =', resolvedBaseURL)
}

const service = axios.create({
  baseURL: resolvedBaseURL,
  'timeout': 600000
})

// Request interceptors
service.interceptors.request.use(
  (config: any) => {
    // console.log(config, 'config')
    // config.data = config.params
    // Add X-Access-Token header to every request, you can add other custom headers here
    if (UserModule.token) {
      config.headers['token'] = UserModule.token
    } else if (UserModule.token && config.url != '/login') {
      window.location.href = '/login'
      return false
    }

    // config.headers['Access-Control-Allow-Origin'] = '*'
    // config.headers['Content-Type'] = 'application/json;'
    // get请求映射params参数
    if (config.method === 'get' && config.params) {
      let url = config.url + '?';
      for (const propName of Object.keys(config.params)) {
        const value = config.params[propName];
        var part = encodeURIComponent(propName) + '=';
        if (value !== null && typeof (value) !== 'undefined') {
          if (typeof value === 'object') {
            for (const key of Object.keys(value)) {
              let params = propName + '[' + key + ']';
              var subPart = encodeURIComponent(params) + '=';
              url += subPart + encodeURIComponent(value[key]) + '&';
            }
          } else {
            url += part + encodeURIComponent(value) + '&';
          }
        }
      }
      url = url.slice(0, -1);
      config.params = {};
      config.url = url;
    }
    // 计算当前请求key值
    const key = getRequestKey(config);
    // console.log(pending,key,checkPending(key),'checkPending(key)')
    if (checkPending(key)) {
      // 重复请求则取消当前请求
      const source = CancelToken.source();
      config.cancelToken = source.token;
      source.cancel('重复请求');
    } else {
      // 加入请求字典
      pending[key] = true;
    }
    return config
  },
  (error: any) => {
    Promise.reject(error)
  }
)

// Response interceptors
service.interceptors.response.use(
  (response: any) => {
    // console.log(response, 'response')
    if (response.data.status === 401) {
      router.push('/login')
      // const res = response.data
      // return response
    }
    // 请求响应中的 config.url 可能以 /api 前缀代理，规范化避免干扰去重逻辑
    if (response.config && typeof response.config.url === 'string' && response.config.url.startsWith('/api')) {
      response.config.url = response.config.url.slice('/api'.length)
    }
    // 请求完成，删除请求中状态
    const key = getRequestKey(response.config);
    removePending(key);
    // if (response.data.code === 0) {
    //   Message.error(response.data.msg)
    //   // if(response.data.msg === 'NOTLOGIN' || response.data.msg === '未登录'){
    //   //   router.push('/login')
    //   // }
    //   // return window.location.href = '/login'
    //   // window.location.href = '/login'
    //   // return false
    // } else
    if (response.data.code === 1) {
      // const res = response.data
      return response
    }
    return response
  },
  (error: any) => {
    // console.log(error.config, pending, 'error')
    if (error && error.response) {
      switch (error.response.status) {
        case 401:
          router.push('/login')
          break;
        case 405:
          error.message = '请求错误'
      }
    }
    if (error.config && typeof error.config.url === 'string' && error.config.url.startsWith('/api')) {
      error.config.url = error.config.url.slice('/api'.length)
    }
    // 请求完成，删除请求中状态
    const key = getRequestKey(error.config);
    removePending(key);
    // console.log(error, pending, 'error11')
    // Message({
    //   'message': error.message,
    //   'type': 'error',
    //   'duration': 5 * 1000
    // })
    // router.push('/login')
    return Promise.reject(error)
  }
)

export default service
