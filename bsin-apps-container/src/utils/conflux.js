import { Conflux } from 'js-conflux-sdk';

const conflux = new Conflux({
  url: 'https://test.confluxrpc.com',
  networkId: 1,
  defaultGasPrice: 22872, // The default gas price of your following transactions
  defaultGas: 15000000, // The default gas of your following transactions
  logger: console,
});



export default conflux;
