/**
 * 本地存储工具函数
 */

/**
 * 设置本地存储
 * @param key 存储键名
 * @param value 存储值
 */
export const setLocalStorageInfo = (key: string, value: any): void => {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (error) {
    console.error('设置本地存储失败:', error);
  }
};

/**
 * 设置会话存储
 * @param key 存储键名
 * @param value 存储值
 */
export const setSessionStorageInfo = (key: string, value: any): void => {
  try {
    sessionStorage.setItem(key, JSON.stringify(value));
  } catch (error) {
    console.error('设置会话存储失败:', error);
  }
};

/**
 * 获取本地存储
 * @param key 存储键名
 * @returns 存储值或null
 */
export const getLocalStorageInfo = (key: string): any => {
  try {
    const item = localStorage.getItem(key);
    return item ? JSON.parse(item) : null;
  } catch (error) {
    console.error('获取本地存储失败:', error);
    return null;
  }
};

/**
 * 获取会话存储
 * @param key 存储键名
 * @returns 存储值或null
 */
export const getSessionStorageInfo = (key: string): any => {
  try {
    const item = sessionStorage.getItem(key);
    return item ? JSON.parse(item) : null;
  } catch (error) {
    console.error('获取会话存储失败:', error);
    return null;
  }
};

/**
 * 删除本地存储
 * @param key 存储键名
 */
export const removeLocalStorageInfo = (key: string): void => {
  try {
    localStorage.removeItem(key);
  } catch (error) {
    console.error('删除本地存储失败:', error);
  }
};

/**
 * 删除会话存储
 * @param key 存储键名
 */
export const removeSessionStorageInfo = (key: string): void => {
  try {
    sessionStorage.removeItem(key);
  } catch (error) {
    console.error('删除会话存储失败:', error);
  }
};

/**
 * 清空本地存储
 */
export const clearLocalStorageInfo = (): void => {
  try {
    localStorage.clear();
  } catch (error) {
    console.error('清空本地存储失败:', error);
  }
};

/**
 * 清空会话存储
 */
export const clearSessionStorageInfo = (): void => {
  try {
    sessionStorage.clear();
  } catch (error) {
    console.error('清空会话存储失败:', error);
  }
};

/**
 * 设置Cookie
 * @param name Cookie名称
 * @param value Cookie值
 * @param days 过期天数
 */
export const setCookie = (name: string, value: string, days: number): void => {
  try {
    const date = new Date();
    date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
    const expires = `expires=${date.toUTCString()}`;
    document.cookie = `${name}=${value}; ${expires}; path=/`;
  } catch (error) {
    console.error('设置Cookie失败:', error);
  }
};

/**
 * 获取Cookie
 * @param name Cookie名称
 * @returns Cookie值或空字符串
 */
export const getCookie = (name: string): string => {
  try {
    const nameEQ = `${name}=`;
    const cookies = document.cookie.split(';');
    for (let i = 0; i < cookies.length; i++) {
      let cookie = cookies[i];
      while (cookie.charAt(0) === ' ') {
        cookie = cookie.substring(1, cookie.length);
      }
      if (cookie.indexOf(nameEQ) === 0) {
        return cookie.substring(nameEQ.length, cookie.length);
      }
    }
    return '';
  } catch (error) {
    console.error('获取Cookie失败:', error);
    return '';
  }
};

/**
 * 删除Cookie
 * @param name Cookie名称
 */
export const removeCookie = (name: string): void => {
  setCookie(name, '', -1);
};
