package de.fs92.defi.compoundeth;

import io.reactivex.Flowable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.0.1.
 */
public class CompoundEthContract extends Contract {
    private static final String BINARY = "Bin file was not provided";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_RESERVEFACTORMANTISSA = "reserveFactorMantissa";

    public static final String FUNC_BORROWBALANCECURRENT = "borrowBalanceCurrent";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_EXCHANGERATESTORED = "exchangeRateStored";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_PENDINGADMIN = "pendingAdmin";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_BALANCEOFUNDERLYING = "balanceOfUnderlying";

    public static final String FUNC_GETCASH = "getCash";

    public static final String FUNC__SETCOMPTROLLER = "_setComptroller";

    public static final String FUNC_TOTALBORROWS = "totalBorrows";

    public static final String FUNC_REPAYBORROW = "repayBorrow";

    public static final String FUNC_COMPTROLLER = "comptroller";

    public static final String FUNC__REDUCERESERVES = "_reduceReserves";

    public static final String FUNC_INITIALEXCHANGERATEMANTISSA = "initialExchangeRateMantissa";

    public static final String FUNC_ACCRUALBLOCKNUMBER = "accrualBlockNumber";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_TOTALBORROWSCURRENT = "totalBorrowsCurrent";

    public static final String FUNC_REDEEMUNDERLYING = "redeemUnderlying";

    public static final String FUNC_TOTALRESERVES = "totalReserves";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_BORROWBALANCESTORED = "borrowBalanceStored";

    public static final String FUNC_ACCRUEINTEREST = "accrueInterest";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_BORROWINDEX = "borrowIndex";

    public static final String FUNC_LIQUIDATEBORROW = "liquidateBorrow";

    public static final String FUNC_SUPPLYRATEPERBLOCK = "supplyRatePerBlock";

    public static final String FUNC_SEIZE = "seize";

    public static final String FUNC__SETPENDINGADMIN = "_setPendingAdmin";

    public static final String FUNC_EXCHANGERATECURRENT = "exchangeRateCurrent";

    public static final String FUNC_GETACCOUNTSNAPSHOT = "getAccountSnapshot";

    public static final String FUNC_BORROW = "borrow";

    public static final String FUNC_REDEEM = "redeem";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_REPAYBORROWBEHALF = "repayBorrowBehalf";

    public static final String FUNC__ACCEPTADMIN = "_acceptAdmin";

    public static final String FUNC__SETINTERESTRATEMODEL = "_setInterestRateModel";

    public static final String FUNC_INTERESTRATEMODEL = "interestRateModel";

    public static final String FUNC_ADMIN = "admin";

    public static final String FUNC_BORROWRATEPERBLOCK = "borrowRatePerBlock";

    public static final String FUNC__SETRESERVEFACTOR = "_setReserveFactor";

    public static final String FUNC_ISCTOKEN = "isCToken";

    public static final Event ACCRUEINTEREST_EVENT = new Event("AccrueInterest",
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event MINT_EVENT = new Event("Mint",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event REDEEM_EVENT = new Event("Redeem",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event BORROW_EVENT = new Event("Borrow",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event REPAYBORROW_EVENT = new Event("RepayBorrow",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Address>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event LIQUIDATEBORROW_EVENT = new Event("LiquidateBorrow",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Address>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Address>() {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event NEWPENDINGADMIN_EVENT = new Event("NewPendingAdmin",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Address>() {
            }));
    ;

    public static final Event NEWADMIN_EVENT = new Event("NewAdmin",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Address>() {
            }));
    ;

    public static final Event NEWCOMPTROLLER_EVENT = new Event("NewComptroller",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Address>() {
            }));
    ;

    public static final Event NEWMARKETINTERESTRATEMODEL_EVENT = new Event("NewMarketInterestRateModel",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Address>() {
            }));
    ;

    public static final Event NEWRESERVEFACTOR_EVENT = new Event("NewReserveFactor",
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event RESERVESREDUCED_EVENT = new Event("ReservesReduced",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event FAILURE_EVENT = new Event("Failure",
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {
            }, new TypeReference<Address>(true) {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {
            }, new TypeReference<Address>(true) {
            }, new TypeReference<Uint256>() {
            }));
    ;

    @Deprecated
    protected CompoundEthContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CompoundEthContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected CompoundEthContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected CompoundEthContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<String> name() {
        final Function function = new Function(FUNC_NAME,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approve(String spender, BigInteger amount) {
        final Function function = new Function(
                FUNC_APPROVE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(spender),
                        new org.web3j.abi.datatypes.generated.Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mint(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_MINT,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<BigInteger> reserveFactorMantissa() {
        final Function function = new Function(FUNC_RESERVEFACTORMANTISSA,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> borrowBalanceCurrent(String account) {
        final Function function = new Function(
                FUNC_BORROWBALANCECURRENT,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> exchangeRateStored() {
        final Function function = new Function(FUNC_EXCHANGERATESTORED,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String src, String dst, BigInteger amount) {
        final Function function = new Function(
                FUNC_TRANSFERFROM,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(src),
                        new org.web3j.abi.datatypes.Address(dst),
                        new org.web3j.abi.datatypes.generated.Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> pendingAdmin() {
        final Function function = new Function(FUNC_PENDINGADMIN,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> balanceOfUnderlying(String owner) {
        final Function function = new Function(
                FUNC_BALANCEOFUNDERLYING,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getCash() {
        final Function function = new Function(FUNC_GETCASH,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> _setComptroller(String newComptroller) {
        final Function function = new Function(
                FUNC__SETCOMPTROLLER,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newComptroller)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalBorrows() {
        final Function function = new Function(FUNC_TOTALBORROWS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> repayBorrow(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_REPAYBORROW,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<String> comptroller() {
        final Function function = new Function(FUNC_COMPTROLLER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> _reduceReserves(BigInteger reduceAmount) {
        final Function function = new Function(
                FUNC__REDUCERESERVES,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(reduceAmount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> initialExchangeRateMantissa() {
        final Function function = new Function(FUNC_INITIALEXCHANGERATEMANTISSA,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> accrualBlockNumber() {
        final Function function = new Function(FUNC_ACCRUALBLOCKNUMBER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> balanceOf(String owner) {
        final Function function = new Function(FUNC_BALANCEOF,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> totalBorrowsCurrent() {
        final Function function = new Function(
                FUNC_TOTALBORROWSCURRENT,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> redeemUnderlying(BigInteger redeemAmount) {
        final Function function = new Function(
                FUNC_REDEEMUNDERLYING,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(redeemAmount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalReserves() {
        final Function function = new Function(FUNC_TOTALRESERVES,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> borrowBalanceStored(String account) {
        final Function function = new Function(FUNC_BORROWBALANCESTORED,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> accrueInterest() {
        final Function function = new Function(
                FUNC_ACCRUEINTEREST,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transfer(String dst, BigInteger amount) {
        final Function function = new Function(
                FUNC_TRANSFER,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(dst),
                        new org.web3j.abi.datatypes.generated.Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> borrowIndex() {
        final Function function = new Function(FUNC_BORROWINDEX,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> liquidateBorrow(String borrower, String cTokenCollateral, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_LIQUIDATEBORROW,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(borrower),
                        new org.web3j.abi.datatypes.Address(cTokenCollateral)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<BigInteger> supplyRatePerBlock() {
        final Function function = new Function(FUNC_SUPPLYRATEPERBLOCK,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> seize(String liquidator, String borrower, BigInteger seizeTokens) {
        final Function function = new Function(
                FUNC_SEIZE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(liquidator),
                        new org.web3j.abi.datatypes.Address(borrower),
                        new org.web3j.abi.datatypes.generated.Uint256(seizeTokens)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> _setPendingAdmin(String newPendingAdmin) {
        final Function function = new Function(
                FUNC__SETPENDINGADMIN,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newPendingAdmin)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> exchangeRateCurrent() {
        final Function function = new Function(
                FUNC_EXCHANGERATECURRENT,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>> getAccountSnapshot(String account) {
        final Function function = new Function(FUNC_GETACCOUNTSNAPSHOT,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        return new RemoteCall<Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple4<BigInteger, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(),
                                (BigInteger) results.get(1).getValue(),
                                (BigInteger) results.get(2).getValue(),
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> borrow(BigInteger borrowAmount) {
        final Function function = new Function(
                FUNC_BORROW,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(borrowAmount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> redeem(BigInteger redeemTokens) {
        final Function function = new Function(
                FUNC_REDEEM,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(redeemTokens)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> allowance(String owner, String spender) {
        final Function function = new Function(FUNC_ALLOWANCE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner),
                        new org.web3j.abi.datatypes.Address(spender)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> repayBorrowBehalf(String borrower, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_REPAYBORROWBEHALF,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(borrower)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> _acceptAdmin() {
        final Function function = new Function(
                FUNC__ACCEPTADMIN,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> _setInterestRateModel(String newInterestRateModel) {
        final Function function = new Function(
                FUNC__SETINTERESTRATEMODEL,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newInterestRateModel)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> interestRateModel() {
        final Function function = new Function(FUNC_INTERESTRATEMODEL,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> admin() {
        final Function function = new Function(FUNC_ADMIN,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> borrowRatePerBlock() {
        final Function function = new Function(FUNC_BORROWRATEPERBLOCK,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> _setReserveFactor(BigInteger newReserveFactorMantissa) {
        final Function function = new Function(
                FUNC__SETRESERVEFACTOR,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(newReserveFactorMantissa)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> isCToken() {
        final Function function = new Function(FUNC_ISCTOKEN,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public List<AccrueInterestEventResponse> getAccrueInterestEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ACCRUEINTEREST_EVENT, transactionReceipt);
        ArrayList<AccrueInterestEventResponse> responses = new ArrayList<AccrueInterestEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AccrueInterestEventResponse typedResponse = new AccrueInterestEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.interestAccumulated = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.borrowIndex = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.totalBorrows = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<AccrueInterestEventResponse> accrueInterestEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, AccrueInterestEventResponse>() {
            @Override
            public AccrueInterestEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ACCRUEINTEREST_EVENT, log);
                AccrueInterestEventResponse typedResponse = new AccrueInterestEventResponse();
                typedResponse.log = log;
                typedResponse.interestAccumulated = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.borrowIndex = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.totalBorrows = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<AccrueInterestEventResponse> accrueInterestEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ACCRUEINTEREST_EVENT));
        return accrueInterestEventFlowable(filter);
    }

    public List<MintEventResponse> getMintEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MINT_EVENT, transactionReceipt);
        ArrayList<MintEventResponse> responses = new ArrayList<MintEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MintEventResponse typedResponse = new MintEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.minter = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.mintAmount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.mintTokens = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<MintEventResponse> mintEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, MintEventResponse>() {
            @Override
            public MintEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MINT_EVENT, log);
                MintEventResponse typedResponse = new MintEventResponse();
                typedResponse.log = log;
                typedResponse.minter = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.mintAmount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.mintTokens = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<MintEventResponse> mintEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINT_EVENT));
        return mintEventFlowable(filter);
    }

    public List<RedeemEventResponse> getRedeemEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REDEEM_EVENT, transactionReceipt);
        ArrayList<RedeemEventResponse> responses = new ArrayList<RedeemEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RedeemEventResponse typedResponse = new RedeemEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.redeemer = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.redeemAmount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.redeemTokens = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RedeemEventResponse> redeemEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, RedeemEventResponse>() {
            @Override
            public RedeemEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REDEEM_EVENT, log);
                RedeemEventResponse typedResponse = new RedeemEventResponse();
                typedResponse.log = log;
                typedResponse.redeemer = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.redeemAmount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.redeemTokens = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RedeemEventResponse> redeemEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REDEEM_EVENT));
        return redeemEventFlowable(filter);
    }

    public List<BorrowEventResponse> getBorrowEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BORROW_EVENT, transactionReceipt);
        ArrayList<BorrowEventResponse> responses = new ArrayList<BorrowEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BorrowEventResponse typedResponse = new BorrowEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.borrower = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.borrowAmount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.accountBorrows = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.totalBorrows = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<BorrowEventResponse> borrowEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, BorrowEventResponse>() {
            @Override
            public BorrowEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(BORROW_EVENT, log);
                BorrowEventResponse typedResponse = new BorrowEventResponse();
                typedResponse.log = log;
                typedResponse.borrower = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.borrowAmount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.accountBorrows = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.totalBorrows = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<BorrowEventResponse> borrowEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BORROW_EVENT));
        return borrowEventFlowable(filter);
    }

    public List<RepayBorrowEventResponse> getRepayBorrowEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REPAYBORROW_EVENT, transactionReceipt);
        ArrayList<RepayBorrowEventResponse> responses = new ArrayList<RepayBorrowEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RepayBorrowEventResponse typedResponse = new RepayBorrowEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.payer = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.borrower = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.repayAmount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.accountBorrows = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.totalBorrows = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RepayBorrowEventResponse> repayBorrowEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, RepayBorrowEventResponse>() {
            @Override
            public RepayBorrowEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REPAYBORROW_EVENT, log);
                RepayBorrowEventResponse typedResponse = new RepayBorrowEventResponse();
                typedResponse.log = log;
                typedResponse.payer = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.borrower = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.repayAmount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.accountBorrows = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.totalBorrows = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RepayBorrowEventResponse> repayBorrowEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REPAYBORROW_EVENT));
        return repayBorrowEventFlowable(filter);
    }

    public List<LiquidateBorrowEventResponse> getLiquidateBorrowEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LIQUIDATEBORROW_EVENT, transactionReceipt);
        ArrayList<LiquidateBorrowEventResponse> responses = new ArrayList<LiquidateBorrowEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LiquidateBorrowEventResponse typedResponse = new LiquidateBorrowEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.liquidator = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.borrower = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.repayAmount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.cTokenCollateral = (String) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.seizeTokens = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LiquidateBorrowEventResponse> liquidateBorrowEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, LiquidateBorrowEventResponse>() {
            @Override
            public LiquidateBorrowEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LIQUIDATEBORROW_EVENT, log);
                LiquidateBorrowEventResponse typedResponse = new LiquidateBorrowEventResponse();
                typedResponse.log = log;
                typedResponse.liquidator = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.borrower = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.repayAmount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.cTokenCollateral = (String) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.seizeTokens = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LiquidateBorrowEventResponse> liquidateBorrowEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LIQUIDATEBORROW_EVENT));
        return liquidateBorrowEventFlowable(filter);
    }

    public List<NewPendingAdminEventResponse> getNewPendingAdminEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NEWPENDINGADMIN_EVENT, transactionReceipt);
        ArrayList<NewPendingAdminEventResponse> responses = new ArrayList<NewPendingAdminEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewPendingAdminEventResponse typedResponse = new NewPendingAdminEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldPendingAdmin = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newPendingAdmin = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NewPendingAdminEventResponse> newPendingAdminEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, NewPendingAdminEventResponse>() {
            @Override
            public NewPendingAdminEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NEWPENDINGADMIN_EVENT, log);
                NewPendingAdminEventResponse typedResponse = new NewPendingAdminEventResponse();
                typedResponse.log = log;
                typedResponse.oldPendingAdmin = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.newPendingAdmin = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<NewPendingAdminEventResponse> newPendingAdminEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWPENDINGADMIN_EVENT));
        return newPendingAdminEventFlowable(filter);
    }

    public List<NewAdminEventResponse> getNewAdminEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NEWADMIN_EVENT, transactionReceipt);
        ArrayList<NewAdminEventResponse> responses = new ArrayList<NewAdminEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewAdminEventResponse typedResponse = new NewAdminEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldAdmin = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newAdmin = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NewAdminEventResponse> newAdminEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, NewAdminEventResponse>() {
            @Override
            public NewAdminEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NEWADMIN_EVENT, log);
                NewAdminEventResponse typedResponse = new NewAdminEventResponse();
                typedResponse.log = log;
                typedResponse.oldAdmin = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.newAdmin = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<NewAdminEventResponse> newAdminEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWADMIN_EVENT));
        return newAdminEventFlowable(filter);
    }

    public List<NewComptrollerEventResponse> getNewComptrollerEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NEWCOMPTROLLER_EVENT, transactionReceipt);
        ArrayList<NewComptrollerEventResponse> responses = new ArrayList<NewComptrollerEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewComptrollerEventResponse typedResponse = new NewComptrollerEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldComptroller = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newComptroller = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NewComptrollerEventResponse> newComptrollerEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, NewComptrollerEventResponse>() {
            @Override
            public NewComptrollerEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NEWCOMPTROLLER_EVENT, log);
                NewComptrollerEventResponse typedResponse = new NewComptrollerEventResponse();
                typedResponse.log = log;
                typedResponse.oldComptroller = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.newComptroller = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<NewComptrollerEventResponse> newComptrollerEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWCOMPTROLLER_EVENT));
        return newComptrollerEventFlowable(filter);
    }

    public List<NewMarketInterestRateModelEventResponse> getNewMarketInterestRateModelEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NEWMARKETINTERESTRATEMODEL_EVENT, transactionReceipt);
        ArrayList<NewMarketInterestRateModelEventResponse> responses = new ArrayList<NewMarketInterestRateModelEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewMarketInterestRateModelEventResponse typedResponse = new NewMarketInterestRateModelEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldInterestRateModel = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newInterestRateModel = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NewMarketInterestRateModelEventResponse> newMarketInterestRateModelEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, NewMarketInterestRateModelEventResponse>() {
            @Override
            public NewMarketInterestRateModelEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NEWMARKETINTERESTRATEMODEL_EVENT, log);
                NewMarketInterestRateModelEventResponse typedResponse = new NewMarketInterestRateModelEventResponse();
                typedResponse.log = log;
                typedResponse.oldInterestRateModel = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.newInterestRateModel = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<NewMarketInterestRateModelEventResponse> newMarketInterestRateModelEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWMARKETINTERESTRATEMODEL_EVENT));
        return newMarketInterestRateModelEventFlowable(filter);
    }

    public List<NewReserveFactorEventResponse> getNewReserveFactorEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NEWRESERVEFACTOR_EVENT, transactionReceipt);
        ArrayList<NewReserveFactorEventResponse> responses = new ArrayList<NewReserveFactorEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewReserveFactorEventResponse typedResponse = new NewReserveFactorEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldReserveFactorMantissa = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newReserveFactorMantissa = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NewReserveFactorEventResponse> newReserveFactorEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, NewReserveFactorEventResponse>() {
            @Override
            public NewReserveFactorEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NEWRESERVEFACTOR_EVENT, log);
                NewReserveFactorEventResponse typedResponse = new NewReserveFactorEventResponse();
                typedResponse.log = log;
                typedResponse.oldReserveFactorMantissa = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.newReserveFactorMantissa = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<NewReserveFactorEventResponse> newReserveFactorEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWRESERVEFACTOR_EVENT));
        return newReserveFactorEventFlowable(filter);
    }

    public List<ReservesReducedEventResponse> getReservesReducedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(RESERVESREDUCED_EVENT, transactionReceipt);
        ArrayList<ReservesReducedEventResponse> responses = new ArrayList<ReservesReducedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ReservesReducedEventResponse typedResponse = new ReservesReducedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.admin = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.reduceAmount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.newTotalReserves = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ReservesReducedEventResponse> reservesReducedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, ReservesReducedEventResponse>() {
            @Override
            public ReservesReducedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(RESERVESREDUCED_EVENT, log);
                ReservesReducedEventResponse typedResponse = new ReservesReducedEventResponse();
                typedResponse.log = log;
                typedResponse.admin = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.reduceAmount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.newTotalReserves = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ReservesReducedEventResponse> reservesReducedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RESERVESREDUCED_EVENT));
        return reservesReducedEventFlowable(filter);
    }

    public List<FailureEventResponse> getFailureEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(FAILURE_EVENT, transactionReceipt);
        ArrayList<FailureEventResponse> responses = new ArrayList<FailureEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            FailureEventResponse typedResponse = new FailureEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.error = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.info = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.detail = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<FailureEventResponse> failureEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, FailureEventResponse>() {
            @Override
            public FailureEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(FAILURE_EVENT, log);
                FailureEventResponse typedResponse = new FailureEventResponse();
                typedResponse.log = log;
                typedResponse.error = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.info = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.detail = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<FailureEventResponse> failureEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(FAILURE_EVENT));
        return failureEventFlowable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    @Deprecated
    public static CompoundEthContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CompoundEthContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static CompoundEthContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CompoundEthContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static CompoundEthContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new CompoundEthContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static CompoundEthContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new CompoundEthContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class AccrueInterestEventResponse {
        public Log log;

        public BigInteger interestAccumulated;

        public BigInteger borrowIndex;

        public BigInteger totalBorrows;
    }

    public static class MintEventResponse {
        public Log log;

        public String minter;

        public BigInteger mintAmount;

        public BigInteger mintTokens;
    }

    public static class RedeemEventResponse {
        public Log log;

        public String redeemer;

        public BigInteger redeemAmount;

        public BigInteger redeemTokens;
    }

    public static class BorrowEventResponse {
        public Log log;

        public String borrower;

        public BigInteger borrowAmount;

        public BigInteger accountBorrows;

        public BigInteger totalBorrows;
    }

    public static class RepayBorrowEventResponse {
        public Log log;

        public String payer;

        public String borrower;

        public BigInteger repayAmount;

        public BigInteger accountBorrows;

        public BigInteger totalBorrows;
    }

    public static class LiquidateBorrowEventResponse {
        public Log log;

        public String liquidator;

        public String borrower;

        public BigInteger repayAmount;

        public String cTokenCollateral;

        public BigInteger seizeTokens;
    }

    public static class NewPendingAdminEventResponse {
        public Log log;

        public String oldPendingAdmin;

        public String newPendingAdmin;
    }

    public static class NewAdminEventResponse {
        public Log log;

        public String oldAdmin;

        public String newAdmin;
    }

    public static class NewComptrollerEventResponse {
        public Log log;

        public String oldComptroller;

        public String newComptroller;
    }

    public static class NewMarketInterestRateModelEventResponse {
        public Log log;

        public String oldInterestRateModel;

        public String newInterestRateModel;
    }

    public static class NewReserveFactorEventResponse {
        public Log log;

        public BigInteger oldReserveFactorMantissa;

        public BigInteger newReserveFactorMantissa;
    }

    public static class ReservesReducedEventResponse {
        public Log log;

        public String admin;

        public BigInteger reduceAmount;

        public BigInteger newTotalReserves;
    }

    public static class FailureEventResponse {
        public Log log;

        public BigInteger error;

        public BigInteger info;

        public BigInteger detail;
    }

    public static class TransferEventResponse {
        public Log log;

        public String from;

        public String to;

        public BigInteger amount;
    }

    public static class ApprovalEventResponse {
        public Log log;

        public String owner;

        public String spender;

        public BigInteger amount;
    }
}
