import { useEffect, useState } from 'react'
import { useWeb3React, Web3ReactHooks, Web3ReactProvider } from '@web3-react/core'
import type { WalletConnect } from '@web3-react/walletconnect'
import type { MetaMask } from '@web3-react/metamask'

import { hooks as walletConnectHooks, walletConnect } from './walletConnect'
import { hooks as metaMaskHooks, metaMask } from './metaMask'
import { getName } from './utils'

const { useChainId, useAccounts, useIsActivating, useIsActive, useProvider, useENSNames } = metaMaskHooks

const connectors: [MetaMask | WalletConnect][] = [
  [metaMask, metaMaskHooks],
  [walletConnect, walletConnectHooks]
]

import { InjectedConnector } from '@web3-react/injected-connector';


function Child() {
  const injected = new InjectedConnector({
    supportedChainIds: [1, 3, 4, 5, 42, 97, 56],
  });

  const context = useWeb3React()
  const {
    connector,
    isActive,
    account
  } = context;

  async function connect() {
    if(account){
      console.log("钱包已连接")
    }
    try {
      await connector.activate(injected)
    } catch (error) {
      console.log(error);
      
    }
  }

  console.log(`Priority Connector is: ${getName(connector)}`)
  return (
    <>
      <button onClick={connect}>同意</button>
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

  const chainId = useChainId()
  const accounts = useAccounts()
  const isActivating = useIsActivating()

  const isActive = useIsActive()

  const provider = useProvider()
  const ENSNames = useENSNames(provider)

  const [error, setError] = useState(undefined)

  // attempt to connect eagerly on mount
  useEffect(() => {
    void metaMask.connectEagerly().catch(() => {
      console.debug('Failed to connect eagerly to metamask')
    })
  }, [])

  return (
    <Web3ReactProvider connectors={connectors}>
      <Child />
    </Web3ReactProvider>
  )
}