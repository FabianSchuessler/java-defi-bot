# java-defi-bot [![Build Status](https://travis-ci.com/FabianSchuessler/java-defi-bot.svg?branch=master)](https://travis-ci.com/FabianSchuessler/java-defi-bot) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=FabianSchuessler_java-defi-bot&metric=coverage)](https://sonarcloud.io/dashboard?id=FabianSchuessler_java-defi-bot)

java-defi-bot is an application that aims to do beneficial actions on the Ethereum blockchain for the [DeFi](https://defipulse.com/) ecosystem while earning a profit for the user. These actions include maintaining the DAI peg, providing liquidity and liquidating undercollateralized assets.

## Getting started

### Prerequisites

- Java such as [Oracle Java](https://www.oracle.com/de/java/technologies/javase-downloads.html)
```
    $ echo $JAVA_HOME
    C:\Program Files\Java\jdk-14.0.1
```
- Connection to Ethereum Network
    - [Infura Project ID](https://infura.io/)
    
    Alternatives:
    - [own Ethereum Node](https://docs.ethhub.io/using-ethereum/running-an-ethereum-node/) 
    - [Quiknode](https://www.quiknode.io/) 
    - AWS
    
- Ethereum Wallet as keystore file including password from services such as [MyEtherWallet](https://www.myetherwallet.com/)
- [Maven](https://maven.apache.org/download.cgi)
```
    $ mvn -version
    Apache Maven 3.6.3
```
### Installing

- ```mvn install```

### Configuration

- Update the configuration file ```./config.properties```

```
infuraProjectId=
password=
wallet=
transactionsRequireConfirmation=true
playSoundOnTransaction=true
uniswapBuyProfitPercentage=0.5
uniswapSellProfitPercentage=0.5
minimumEthereumReserveUpperLimit=0.20
minimumEthereumReserveLowerLimit=0.10
minimumEthereumNecessaryForSale=1.0
minimumDaiNecessaryForSaleAndLending=250.0
minimumFlipAuctionProfit=50.0
minimumGasPrice=1000000000
maximumGasPrice=30000000000
testProperty=true
```

__Make sure to never commit your ```config.properties``` file!__

- Make git stop tracking your config file ```git update-index --skip-worktree ./config.properties```
- Make git start tracking your config file again ```git update-index --no-skip-worktree ./config.properties```

### Compile

- ```mvn clean compile assembly:single```

### Running the tests

- All Tests ```mvn clean test```
- Unit Tests ```mvn clean test -DskipITs```
- Integration Tests ```mvn clean failsafe:integration-test```

### Run 

- Either just run it in the IDE of your choice
- or execute the compiled application ```java -jar java-defi-bot-0.1-jar-with-dependencies.jar``` and make sure it has access to the ```config.properties``` file

### Logs

You will find the logs in ```./logs```.

## Current features

- sells DAI, if DAI > $1.00 on Oasis ```oasis.checkIfSellDaiIsProfitableThenDoIt(balances);```
- buys DAI, if DAI < $1.00 on Oasis ```oasis.checkIfBuyDaiIsProfitableThenDoIt(balances);```
- sells DAI, if DAI > $1.00 on Uniswap ```uniswap.checkIfSellDaiIsProfitableThenDoIt(balances);```
- buys DAI, if DAI < $1.00 on Uniswap ```uniswap.checkIfBuyDaiIsProfitableThenDoIt(balances);```
- earns interest on Compound, if there is no market action ```compoundDai.lendDai(balances);```
- bids on flip auctions ```flipper.checkIfThereAreProfitableFlipAuctions(balances);```


## Contribute

Feel free to open merge requests.

## Developer Guide

- Code Style: [Google Java Format](https://github.com/google/google-java-format/blob/master/README.md)
- Add new smart contracts: [web3j](https://github.com/web3j/web3j)
- Logging: SLF4J + logback is used as defined in ```./src/main/resources/logback.xml```
- Update Maven Dependencies: ```mvn versions:use-latest-versions```
- Show updatable dependencies: ```mvn versions:display-dependency-updates```
- Show unused dependencies: ```mvn dependency:analyze```
- [MakerDAO Documentation](https://docs.makerdao.com/)
- [Web3j Documentation](https://docs.web3j.io/)
- [Compound Documentation](https://compound.finance/docs)
- [DAI Stats](https://daistats.com)
- [ETH Gas Station](https://ethgasstation.info)
- [DAI Peg: DAI Descipher](http://dai.descipher.io)
- [DAI Peg: DAI Stablecoin](https://dai.stablecoin.science/)
- [Loanscan](https://loanscan.io/)
- [Flip auctions](https://daiauctions.com/flip)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
