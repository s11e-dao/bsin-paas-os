import { notification } from 'antd';

class ConfluxFluent {

  constructor (conflux) {
    if (typeof conflux === 'undefined') {
      notification.open({
        message: '您还没安装fluent钱包插件，请点击链链安装',
        onClick: () => {
          window.location.href="https://fluentwallet.com/";
        },
      });
      return
    }
    if (!conflux.isConfluxPortal) {
      console.debug('Unknown Conflux.')
    }
    this.conflux = conflux
  }

  async enable () {
    this.accounts = await window.conflux.request({method: "cfx_requestAccounts"})
  }

  getAccount () {
    if (!this.accounts) {
      throw new Error('Please enable Conflux Portal first')
    }
    return this.accounts[0]
  }

  
  async sendTransaction (params) {
    return new Promise((resolve, reject) => {
      this.conflux.request({
          method: 'cfx_sendTransaction',
          params,
        })
        .then((result) => {
          // The result varies by RPC method.
          // For example, this method will return a transaction hash hexadecimal string on success.
        })
        .catch((error) => {
          // If the request fails, the Promise will reject with an error.
        });
    })
  }

  async getBalance (params) {
    return new Promise((resolve, reject) => {
      this.conflux.request({
          method: 'balanceOf',
          params,
        })
        .then((result) => {
          console.log(result)
          // The result varies by RPC method.
          // For example, this method will return a transaction hash hexadecimal string on success.
        })
        .catch((error) => {
          // If the request fails, the Promise will reject with an error.
        });
    })
  }

  async sendTransaction (params) {
    return new Promise((resolve, reject) => {
      this.conflux.sendAsync({
        method: 'cfx_sendTransaction',
        params: [params],
        from: params.from,
        gasPrice: '0x09184e72a000', // customizable by user during ConfluxFluent confirmation.
        gas: '0x2710',  // customizable by user during ConfluxFluent confirmation.
        value: '0x00',
      }, (err, data) => {
        if (err) {
          reject(err)
        } else {
          resolve(data)
        }
      })
    })
  }
}

export default new ConfluxFluent(window.conflux)
