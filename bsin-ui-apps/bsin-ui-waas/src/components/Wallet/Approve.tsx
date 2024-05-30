import { useEffect, useState } from 'react'
import { useWeb3React, Web3ReactProvider } from '@web3-react/core'
import type { WalletConnect } from '@web3-react/walletconnect'
import type { MetaMask } from '@web3-react/metamask'
import {
  Button
} from 'antd';
import Web3 from 'web3';

import { hooks as walletConnectHooks, walletConnect } from './walletConnect'
import { getName } from './utils'


import { InjectedConnector } from '@web3-react/injected-connector';

import approveABI from './approveABI';


function Approve() {
  // 指定网络  97 main: 56
  const injected = new InjectedConnector({
    supportedChainIds: [97],
  });

  const context = useWeb3React()
  const {
    connector,
    library,
    activate,
    account
  } = context;

  async function connect() {
    if (account) {
      console.log("钱包已连接")
    }
    try {
      await activate(injected);
    } catch (error) {
      console.log(error);

    }
  }

  async function doApprove() {
    if(!account){
      await activate(injected);
    }

    // 判断授权的账户跟商户配置账户是否一致

    console.log(account)
    // 新版本的web3-react的library变成了connector当前连接的
    const web3 = new Web3(Web3.givenProvider);
    var myContract = new web3.eth.Contract(
      approveABI,
      //TestTokenContractBuild.networks[networkId].address
      '0x228710cbF88C70F6bcA81F9718d4A593Efe18D9F'
    );

    myContract.methods.approve("0x9E738d6681F42FaC46a987015568BBe52b9a1c1d", web3.utils.toWei("10", 'ether')).send({ from: account, value: 0 })
      .then(function (receipt) {
        console.log(receipt)
        // receipt can also be a new contract instance, when coming from a "contract.deploy({...}).send()"
    });

    const token = await myContract.methods.balanceOf(account).call();

    console.log(token)

  }

  return (
    <>
      <Button onClick={doApprove} type="primary">授权</Button>
      {/* {isActive ? (
        <span>
          Connected with <b>{account}</b>
        </span>
      ) : (
        <span>Not connected</span>
      )} */}
    </>
  )
}

export default function ProviderExample() {

  return (
    <Web3ReactProvider getLibrary={(provider: any) => new Web3(provider)}>
      <Approve />
    </Web3ReactProvider>
  )
}