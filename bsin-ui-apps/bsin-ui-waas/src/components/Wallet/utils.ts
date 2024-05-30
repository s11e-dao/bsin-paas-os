import { Network } from '@web3-react/network'
import type { Connector } from '@web3-react/types'
import { WalletConnect as WalletConnect } from '@web3-react/walletconnect'
import { MetaMask } from '@web3-react/metamask'

export function getName(connector: Connector) {
  if (connector instanceof WalletConnect) return 'WalletConnect'
  if (connector instanceof MetaMask) return 'MetaMask'
  return 'Unknown'
}