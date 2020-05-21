# Bug fixes
- Fix negative ratio:
    - ```HOLDING -55.39% DAI + CDAI AS A PERCENTAGE OVER TIME OF TOTAL ASSET VALUE```
    - ```HOLDING 110.44% DAI + CDAI AS A PERCENTAGE OVER TIME OF TOTAL ASSET VALUE```
- Have a look at MakerDao Auction Keep and their API usage about Price Feeds

# New Features
- Add deal for flip auctions
- Add [Uniswap V2](https://uniswap.org/blog/uniswap-v2/)
- Move wait time to config? private static final int WAIT_TIME = 60 * 60 * 1000; // 60 minutes
- Compare oasis and uniswap profit in Main
- Put BigNumberUtils into a BigInteger/BigDecimal Wrapper class
- **Finish flip auctions**
    - Bid on an auction if five minutes left and minimal profitable depending on tend or dend phase
- **Add maker.borrowAndLendDai(balances)**
- **Add dydx.lendDai()**
- Add eth gas estimation per transaction
- Add dydx buy and sell dai
    - [Protocol](https://docs.dydx.exchange/#/protocol) 
    - [Contracts](https://docs.dydx.exchange/#/contracts)
- Add maker.borrowAndSellDai(balances)
- Add maker.borrowDai(balances) ?
- Add manage maker debt
- Add maker.paybackBorrowedDai(balances)
- Add priority order of profit operations/ financial models
- Add compound.borrowAndLendDai(balances)
- Add compound.paybackBorrowedDai(balances)
- Add checking for internet, if no internet then wait for a period of time
- Add checking for global/emergency shutdown
- Add option to use Geth instead of Infura
- Add fallback option for web3j connectivity
- Improve method getGasLimit() in class GasProvider
    - Use ```web3j.ethEstimateGas(t).send();``` to estimate gas or
    - Add a differentiation between contracts to method getGasLimit() in class GasProvider
- Use ```web3j.ethEstimateGas(t).send();``` to estimate gas for calculateGasPriceAsAPercentageOfProfit() instead of constant
- Add checking next Oasis Offer if current offer amount too low
- Add support for multithreading: Multiple application with different web3j objects
- Add a timeout to the transaction confirmation in the command line
- Add do nothing during pending transactions
- Add unstuck pending transactions org.web3j.protocol.exceptions.TransactionException: Transaction receipt was not generated after 600 seconds for transaction: 0x71ac56e8dba69d789a8d5e80e081740a7a75fd1c887f4a8d28fd93f47fd69261
- **Add DSProxy**
    - [Defi Saver: Introduction to DSProxy](https://medium.com/defi-saver/a-short-introduction-to-makers-dsproxy-and-why-we-l-it-c88932595be)
    - [MakerDAO: DSProxy](https://docs.makerdao.com/daiUser.js/advanced-configuration/using-ds-proxy)
    - [DSProxy](https://github.com/dapphub/ds-proxy)
    - [Developer Guide: DSProxy](https://github.com/makerdao/developerguides/blob/master/devtools/working-with-dsproxy/working-with-dsproxy.md)
- [Add feeds to Medianizer ](https://www.reddit.com/r/MakerDAO/comments/b96kbg/what_is_the_external_source_of_the_dai_usd_peg/)
    - Coinbase
    - Gemini
    - Bitstamp
- Add polling mode if there is no wallet in the config
- Add flashloans
    - For fast unwinding vaults
- Add (maybe) PoolTogether
- Add an Infura class that used the Infura API and the probability of future transactions to manage the requests to Infura
- Add information about interest earned


# Performance Improvements
- Analyse why lose bids
- dont bid on single stuff, but general
- medianizer/gasprice
- dxproxy
- Have a look at the Median and the GasPrice update frequency to increase performance and make them dependent on their volatility
    - Move Medianizer.PRICE_UPDATE_INTERVAL to config.properties or make it variable

# Refactoring
- **Add test coverage to Uniswap and Oasis**
- **Fix bug profit provider bug**
- Use Mockito to mock balances and test methods
- Have a look at both uniswap profitable methods, maybe refactor into one?

```
12:02:55.317 TRACE Uniswap - Profit 2.1924
12:02:55.318 INFO  CompoundDai - CDAI CONVERSION NOT NECESSARY
12:02:55.545 TRACE Etherchain - ETHERCHAIN SUGGESTS GP 15 GWEI
12:02:55.755 TRACE ETHGasStation - ETHERGASSTATION SUGGESTS GP 150 GWEI
12:02:55.755 TRACE GasProvider - GP PERCENTAGE OF PROFIT 0.1
12:02:55.755 TRACE GasProvider - EST. TRANSACTION FEE 20.3935 DAI
12:02:55.755 TRACE GasProvider - PROFIT SUGGESTS GP 31006.81311057 GWEI
```
- **Fix ETH SOLD bug**
```
12:02:46.701 TRACE Uniswap - ETH SOLD 0.0-9192729000000000
```
- Create TimeUtils and put all the unixTime and timeZone stuff into it
- Fix all to dos
- Implement all empty tests
- Use rules/extension to avoid code duplication in the setup of tests
- Refactor profitable methods to make them more readable
- Test profitable methods
- Fix all sonarlints (log4j2)
- Fix handling of gas price too low:
- Check if Uniswap 0.3% fee is taken into account
- Check existing price feeds for new APIs
- Add telegram notifications about trades and balances
- [Add logging layout](http://logback.qos.ch/manual/layouts.html) 

# Think about
- Check out web3j beginners page: event feed
- Converting data classes to records: https://dzone.com/articles/introducing-java-record, https://blog.jetbrains.com/idea/2020/03/java-14-and-intellij-idea/
- [Using Quiknode](https://www.quiknode.io/)
- [Running own Ethereum node](https://docs.ethhub.io/using-ethereum/running-an-ethereum-node/)
- [Buy own server to run Ethereum node](https://medium.com/coinmonks/running-ethereum-full-nodes-a-guide-for-the-barely-motivated-a8a13e7a0d31)
- **Convert project to Kotlin**

# Current limitations
- Where do you run it (AWS or Raspberry Pi)?
- How to you connect to the Ethereum Nethwork (Infura or Geth)?
- How do you get price feeds (Need API keys)?

# Open Questions
- Will the new OasisDex contract expire like the last one?

# Others
- write peak total usd balance into java properties and date of it
- redeploy and check for errors/ null pointer exception
- java properties: TOTAL ARBITRAGE P&L, TOTAL MISSED PROFITS, TOTAL P&L DURING EXECUTION    -55.15 USD
- interest wins or loses by compound, can get transaction costs out of transaction receipts and compare in and output of daiUser into compound
- partial redeem of compound
- cdai trading on compound
- examine in and out of compound
- CompoundEth.checkBorrowDaiOpportunity();
- logg all transaction meta data: amount + eth/daiUser