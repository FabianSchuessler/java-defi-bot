# java-defi-bot [![Build Status](https://travis-ci.com/FabianSchuessler/java-defi-bot.svg?branch=master)](https://travis-ci.com/FabianSchuessler/java-defi-bot)

java-defi-bot is an application that aims to do beneficial actions on the Ethereum blockchain for the DeFi ecosystem while earning a profit for the user. These actions include maintaining the DAI peg, providing liquidity and liquidating undercollateralized assets.

## Getting started

### Prerequisites

- Java >= 14.0.1
- Infura Project ID or Ethereum Node
- Ethereum Wallet
- Maven

### Installing

```mvn install```

### Configuration

- Update the configuration file ```./config.properties```

```
infuraProjectId=
myEthereumAddress=
password=
transactionsRequireConfirmation=true
playSoundOnTransaction=true
uniswapBuyProfitPercentage=0.5
uniswapSellProfitPercentage=0.5
```

- Add your wallet file ```./wallets/{myEthereumAddress}```

__Make sure to never commit your private information!__

- Make git stop tracking your config file ```git update-index --assume-unchanged ./config.properties```

### Compile

```mvn clean compile assembly:single```

### Running the tests

- Unit Tests ```mvn clean test```
- Integration Tests

### Run 

- Either just run it in the IDE of your choice
- or make sure the compiled application has access to the config and wallet file```java -jar java-defi-bot-0.1-jar-with-dependencies.jar```

### Logs

You will find the logs in ```./logs```.

## Current features

- sells DAI, if DAI > $1.00 on Oasis ```oasisDex.checkIfSellDaiIsProfitableThenDoIt(balances);```
- buys DAI, if DAI < $1.00 on Oasis ```oasisDex.checkIfBuyDaiIsProfitableThenDoIt(balances);```
- sells DAI, if DAI > $1.00 on Uniswap ```uniswap.checkIfSellDaiIsProfitableThenDoIt(balances);```
- buys DAI, if DAI < $1.00 on Uniswap ```uniswap.checkIfBuyDaiIsProfitableThenDoIt(balances);```
- earns interest on Compound, if there is no market action ```compoundDai.lendDai(balances);```

## Contribute

Feel free to open merge requests.

## Developer Guide

- Code Style: [Google Java Format](https://github.com/google/google-java-format/blob/master/README.md)
- Add new smart contracts: [web3j](https://github.com/web3j/web3j)
- SLF4J + logback is used as defined in ```./src/main/resources/logback.xml```
- Update Maven Dependencies: ```mvn versions:use-latest-versions```
- Show updatable dependencies: ```mvn versions:display-dependency-updates```
- Show unused dependencies: ```mvn dependency:analyze```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
