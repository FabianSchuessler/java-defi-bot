package de.fs92.defi.uniswap;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.5.
 */
@SuppressWarnings("rawtypes")
public class UniswapContract extends Contract {
    private static final String BINARY = "Bin file was not provided";

    public static final String FUNC_SETUP = "setup";

    public static final String FUNC_ADDLIQUIDITY = "addLiquidity";

    public static final String FUNC_REMOVELIQUIDITY = "removeLiquidity";

    public static final String FUNC___DEFAULT__ = "__default__";

    public static final String FUNC_ETHTOTOKENSWAPINPUT = "ethToTokenSwapInput";

    public static final String FUNC_ETHTOTOKENTRANSFERINPUT = "ethToTokenTransferInput";

    public static final String FUNC_ETHTOTOKENSWAPOUTPUT = "ethToTokenSwapOutput";

    public static final String FUNC_ETHTOTOKENTRANSFEROUTPUT = "ethToTokenTransferOutput";

    public static final String FUNC_TOKENTOETHSWAPINPUT = "tokenToEthSwapInput";

    public static final String FUNC_TOKENTOETHTRANSFERINPUT = "tokenToEthTransferInput";

    public static final String FUNC_TOKENTOETHSWAPOUTPUT = "tokenToEthSwapOutput";

    public static final String FUNC_TOKENTOETHTRANSFEROUTPUT = "tokenToEthTransferOutput";

    public static final String FUNC_TOKENTOTOKENSWAPINPUT = "tokenToTokenSwapInput";

    public static final String FUNC_TOKENTOTOKENTRANSFERINPUT = "tokenToTokenTransferInput";

    public static final String FUNC_TOKENTOTOKENSWAPOUTPUT = "tokenToTokenSwapOutput";

    public static final String FUNC_TOKENTOTOKENTRANSFEROUTPUT = "tokenToTokenTransferOutput";

    public static final String FUNC_TOKENTOEXCHANGESWAPINPUT = "tokenToExchangeSwapInput";

    public static final String FUNC_TOKENTOEXCHANGETRANSFERINPUT = "tokenToExchangeTransferInput";

    public static final String FUNC_TOKENTOEXCHANGESWAPOUTPUT = "tokenToExchangeSwapOutput";

    public static final String FUNC_TOKENTOEXCHANGETRANSFEROUTPUT = "tokenToExchangeTransferOutput";

    public static final String FUNC_GETETHTOTOKENINPUTPRICE = "getEthToTokenInputPrice";

    public static final String FUNC_GETETHTOTOKENOUTPUTPRICE = "getEthToTokenOutputPrice";

    public static final String FUNC_GETTOKENTOETHINPUTPRICE = "getTokenToEthInputPrice";

    public static final String FUNC_GETTOKENTOETHOUTPUTPRICE = "getTokenToEthOutputPrice";

    public static final String FUNC_TOKENADDRESS = "tokenAddress";

    public static final String FUNC_FACTORYADDRESS = "factoryAddress";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final Event TOKENPURCHASE_EVENT = new Event("TokenPurchase", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>(true) {}));
    ;

    public static final Event ETHPURCHASE_EVENT = new Event("EthPurchase", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>(true) {}));
    ;

    public static final Event ADDLIQUIDITY_EVENT = new Event("AddLiquidity", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>(true) {}));
    ;

    public static final Event REMOVELIQUIDITY_EVENT = new Event("RemoveLiquidity", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>(true) {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected UniswapContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected UniswapContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected UniswapContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected UniswapContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<TokenPurchaseEventResponse> getTokenPurchaseEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TOKENPURCHASE_EVENT, transactionReceipt);
        ArrayList<TokenPurchaseEventResponse> responses = new ArrayList<TokenPurchaseEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokenPurchaseEventResponse typedResponse = new TokenPurchaseEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.eth_sold = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.tokens_bought = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TokenPurchaseEventResponse> tokenPurchaseEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, TokenPurchaseEventResponse>() {
            @Override
            public TokenPurchaseEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TOKENPURCHASE_EVENT, log);
                TokenPurchaseEventResponse typedResponse = new TokenPurchaseEventResponse();
                typedResponse.log = log;
                typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.eth_sold = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.tokens_bought = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<TokenPurchaseEventResponse> tokenPurchaseEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TOKENPURCHASE_EVENT));
        return tokenPurchaseEventFlowable(filter);
    }

    public List<EthPurchaseEventResponse> getEthPurchaseEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ETHPURCHASE_EVENT, transactionReceipt);
        ArrayList<EthPurchaseEventResponse> responses = new ArrayList<EthPurchaseEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EthPurchaseEventResponse typedResponse = new EthPurchaseEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.tokens_sold = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.eth_bought = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<EthPurchaseEventResponse> ethPurchaseEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, EthPurchaseEventResponse>() {
            @Override
            public EthPurchaseEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ETHPURCHASE_EVENT, log);
                EthPurchaseEventResponse typedResponse = new EthPurchaseEventResponse();
                typedResponse.log = log;
                typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.tokens_sold = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.eth_bought = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<EthPurchaseEventResponse> ethPurchaseEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ETHPURCHASE_EVENT));
        return ethPurchaseEventFlowable(filter);
    }

    public List<AddLiquidityEventResponse> getAddLiquidityEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ADDLIQUIDITY_EVENT, transactionReceipt);
        ArrayList<AddLiquidityEventResponse> responses = new ArrayList<AddLiquidityEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AddLiquidityEventResponse typedResponse = new AddLiquidityEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.provider = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.eth_amount = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.token_amount = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<AddLiquidityEventResponse> addLiquidityEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, AddLiquidityEventResponse>() {
            @Override
            public AddLiquidityEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ADDLIQUIDITY_EVENT, log);
                AddLiquidityEventResponse typedResponse = new AddLiquidityEventResponse();
                typedResponse.log = log;
                typedResponse.provider = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.eth_amount = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.token_amount = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<AddLiquidityEventResponse> addLiquidityEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ADDLIQUIDITY_EVENT));
        return addLiquidityEventFlowable(filter);
    }

    public List<RemoveLiquidityEventResponse> getRemoveLiquidityEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REMOVELIQUIDITY_EVENT, transactionReceipt);
        ArrayList<RemoveLiquidityEventResponse> responses = new ArrayList<RemoveLiquidityEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RemoveLiquidityEventResponse typedResponse = new RemoveLiquidityEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.provider = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.eth_amount = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.token_amount = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RemoveLiquidityEventResponse> removeLiquidityEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RemoveLiquidityEventResponse>() {
            @Override
            public RemoveLiquidityEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REMOVELIQUIDITY_EVENT, log);
                RemoveLiquidityEventResponse typedResponse = new RemoveLiquidityEventResponse();
                typedResponse.log = log;
                typedResponse.provider = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.eth_amount = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.token_amount = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RemoveLiquidityEventResponse> removeLiquidityEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REMOVELIQUIDITY_EVENT));
        return removeLiquidityEventFlowable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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
            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> setup(String token_addr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETUP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, token_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addLiquidity(BigInteger min_liquidity, BigInteger max_tokens, BigInteger deadline, BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDLIQUIDITY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(min_liquidity), 
                new org.web3j.abi.datatypes.generated.Uint256(max_tokens), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> removeLiquidity(BigInteger amount, BigInteger min_eth, BigInteger min_tokens, BigInteger deadline) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REMOVELIQUIDITY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount), 
                new org.web3j.abi.datatypes.generated.Uint256(min_eth), 
                new org.web3j.abi.datatypes.generated.Uint256(min_tokens), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> __default__(BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC___DEFAULT__, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> ethToTokenSwapInput(BigInteger min_tokens, BigInteger deadline, BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ETHTOTOKENSWAPINPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(min_tokens), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> ethToTokenTransferInput(BigInteger min_tokens, BigInteger deadline, String recipient, BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ETHTOTOKENTRANSFERINPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(min_tokens), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.Address(160, recipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> ethToTokenSwapOutput(BigInteger tokens_bought, BigInteger deadline, BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ETHTOTOKENSWAPOUTPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> ethToTokenTransferOutput(BigInteger tokens_bought, BigInteger deadline, String recipient, BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ETHTOTOKENTRANSFEROUTPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.Address(160, recipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> tokenToEthSwapInput(BigInteger tokens_sold, BigInteger min_eth, BigInteger deadline) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOKENTOETHSWAPINPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(min_eth), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> tokenToEthTransferInput(BigInteger tokens_sold, BigInteger min_eth, BigInteger deadline, String recipient) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOKENTOETHTRANSFERINPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(min_eth), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.Address(160, recipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> tokenToEthSwapOutput(BigInteger eth_bought, BigInteger max_tokens, BigInteger deadline) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOKENTOETHSWAPOUTPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(eth_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(max_tokens), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> tokenToEthTransferOutput(BigInteger eth_bought, BigInteger max_tokens, BigInteger deadline, String recipient) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOKENTOETHTRANSFEROUTPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(eth_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(max_tokens), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.Address(160, recipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> tokenToTokenSwapInput(BigInteger tokens_sold, BigInteger min_tokens_bought, BigInteger min_eth_bought, BigInteger deadline, String token_addr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOKENTOTOKENSWAPINPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(min_tokens_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(min_eth_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.Address(160, token_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> tokenToTokenTransferInput(BigInteger tokens_sold, BigInteger min_tokens_bought, BigInteger min_eth_bought, BigInteger deadline, String recipient, String token_addr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOKENTOTOKENTRANSFERINPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(min_tokens_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(min_eth_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.Address(160, recipient), 
                new org.web3j.abi.datatypes.Address(160, token_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> tokenToTokenSwapOutput(BigInteger tokens_bought, BigInteger max_tokens_sold, BigInteger max_eth_sold, BigInteger deadline, String token_addr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOKENTOTOKENSWAPOUTPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(max_tokens_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(max_eth_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.Address(160, token_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> tokenToTokenTransferOutput(BigInteger tokens_bought, BigInteger max_tokens_sold, BigInteger max_eth_sold, BigInteger deadline, String recipient, String token_addr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOKENTOTOKENTRANSFEROUTPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(max_tokens_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(max_eth_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.Address(160, recipient), 
                new org.web3j.abi.datatypes.Address(160, token_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> tokenToExchangeSwapInput(BigInteger tokens_sold, BigInteger min_tokens_bought, BigInteger min_eth_bought, BigInteger deadline, String exchange_addr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOKENTOEXCHANGESWAPINPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(min_tokens_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(min_eth_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.Address(160, exchange_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> tokenToExchangeTransferInput(BigInteger tokens_sold, BigInteger min_tokens_bought, BigInteger min_eth_bought, BigInteger deadline, String recipient, String exchange_addr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOKENTOEXCHANGETRANSFERINPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(min_tokens_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(min_eth_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.Address(160, recipient), 
                new org.web3j.abi.datatypes.Address(160, exchange_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> tokenToExchangeSwapOutput(BigInteger tokens_bought, BigInteger max_tokens_sold, BigInteger max_eth_sold, BigInteger deadline, String exchange_addr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOKENTOEXCHANGESWAPOUTPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(max_tokens_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(max_eth_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.Address(160, exchange_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> tokenToExchangeTransferOutput(BigInteger tokens_bought, BigInteger max_tokens_sold, BigInteger max_eth_sold, BigInteger deadline, String recipient, String exchange_addr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOKENTOEXCHANGETRANSFEROUTPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_bought), 
                new org.web3j.abi.datatypes.generated.Uint256(max_tokens_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(max_eth_sold), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.Address(160, recipient), 
                new org.web3j.abi.datatypes.Address(160, exchange_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> getEthToTokenInputPrice(BigInteger eth_sold) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETETHTOTOKENINPUTPRICE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(eth_sold)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getEthToTokenOutputPrice(BigInteger tokens_bought) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETETHTOTOKENOUTPUTPRICE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_bought)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getTokenToEthInputPrice(BigInteger tokens_sold) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOKENTOETHINPUTPRICE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokens_sold)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getTokenToEthOutputPrice(BigInteger eth_bought) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOKENTOETHOUTPUTPRICE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(eth_bought)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> tokenAddress() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOKENADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> factoryAddress() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FACTORYADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String _owner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _from), 
                new org.web3j.abi.datatypes.Address(160, _to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String _spender, BigInteger _value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> allowance(String _owner, String _spender) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _owner), 
                new org.web3j.abi.datatypes.Address(160, _spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> name() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<byte[]> symbol() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<BigInteger> decimals() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> totalSupply() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static UniswapContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new UniswapContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static UniswapContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new UniswapContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static UniswapContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new UniswapContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static UniswapContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new UniswapContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class TokenPurchaseEventResponse extends BaseEventResponse {
        public String buyer;

        public BigInteger eth_sold;

        public BigInteger tokens_bought;
    }

    public static class EthPurchaseEventResponse extends BaseEventResponse {
        public String buyer;

        public BigInteger tokens_sold;

        public BigInteger eth_bought;
    }

    public static class AddLiquidityEventResponse extends BaseEventResponse {
        public String provider;

        public BigInteger eth_amount;

        public BigInteger token_amount;
    }

    public static class RemoveLiquidityEventResponse extends BaseEventResponse {
        public String provider;

        public BigInteger eth_amount;

        public BigInteger token_amount;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String _from;

        public String _to;

        public BigInteger _value;
    }

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String _owner;

        public String _spender;

        public BigInteger _value;
    }
}
