package de.fs92.defi.medianizer;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes12;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Bytes4;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint96;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
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
public class MedianizerContract extends Contract {
    private static final String BINARY = "60806040819052600580546c0100000000000000000000000060016001606060020a03199092168217606060020a60c060020a03191617909155805433600160a060020a03199091168117909155907fce241d7ca1f669fee44b6fc00b8eba2df3bb514eed0f6f668f8f89096e81ed9490600090a2610fd4806100836000396000f3006080604052600436106101115763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166313af403581146101165780631504460f1461013957806318178358146101515780631a43c338146101665780632801617e146101945780632966d1b9146101b55780632db78d93146101d65780634c8fe5261461021457806357de26a41461022957806359e02dd714610250578063651dd0de146102655780636ba5ef0d146102a35780637a9e5e4b146102c45780638da5cb5b146102e5578063ac4c25b2146102fa578063beb38b431461030f578063bf7e214f1461033d578063e0a1fdad14610352578063f2c5925d14610374578063f889794514610396575b600080fd5b34801561012257600080fd5b50610137600160a060020a03600435166103c7565b005b34801561014557600080fd5b50610137600435610435565b34801561015d57600080fd5b506101376104c3565b34801561017257600080fd5b5061017b6104cf565b6040805192835290151560208301528051918290030190f35b3480156101a057600080fd5b50610137600160a060020a03600435166108a3565b3480156101c157600080fd5b50610137600160a060020a036004351661091d565b3480156101e257600080fd5b506101f7600160a060020a036004351661094b565b60408051600160a060020a03199092168252519081900360200190f35b34801561022057600080fd5b506101f7610963565b34801561023557600080fd5b5061023e61096f565b60408051918252519081900360200190f35b34801561025c57600080fd5b5061017b61098f565b34801561027157600080fd5b50610287600160a060020a0319600435166109a4565b60408051600160a060020a039092168252519081900360200190f35b3480156102af57600080fd5b506101376001606060020a03600435166109bf565b3480156102d057600080fd5b50610137600160a060020a0360043516610a83565b3480156102f157600080fd5b50610287610ae8565b34801561030657600080fd5b50610137610af7565b34801561031b57600080fd5b50610137600160a060020a031960043516602435600160a060020a0316610b83565b34801561034957600080fd5b50610287610cfe565b34801561035e57600080fd5b50610137600160a060020a031960043516610d0d565b34801561038057600080fd5b50610137600160a060020a031960043516610d18565b3480156103a257600080fd5b506103ab610dbd565b604080516001606060020a039092168252519081900360200190f35b6103e56103e033600035600160e060020a031916610ddc565b610ee5565b60018054600160a060020a031916600160a060020a0383811691909117918290556040519116907fce241d7ca1f669fee44b6fc00b8eba2df3bb514eed0f6f668f8f89096e81ed9490600090a250565b60408051348082526020820183815236938301849052600435936024359384938693339360008035600160e060020a031916949092606082018484808284376040519201829003965090945050505050a461048e6104cf565b6001805491151560a060020a0274ff000000000000000000000000000000000000000019909216919091179055600255505050565b6104cd6000610435565b565b600080606060008060008060008060008060006001600560009054906101000a900460a060020a0260a060020a9004036001606060020a0316604051908082528060200260200182016040528015610531578160200160208202803883390190505b50995060009850600197505b60055460a060020a908102046001606060020a03908116908916101561078557600160a060020a031960a060020a890216600090815260036020526040902054600160a060020a03161561077a5760a060020a8802600160a060020a0319166000908152600360205260408082205481517f59e02dd70000000000000000000000000000000000000000000000000000000081528251600160a060020a03909216936359e02dd79360048084019491939192918390030190829087803b15801561060657600080fd5b505af115801561061a573d6000803e3d6000fd5b505050506040513d604081101561063057600080fd5b5080516020909101519097509550851561077a576001606060020a038916158061067d57508960018a036001606060020a031681518110151561066f57fe5b602090810290910101518710155b156106a957868a8a6001606060020a031681518110151561069a57fe5b60209081029091010152610773565b600094505b89856001606060020a03168151811015156106c557fe5b6020908102909101015187106106e0576001909401936106ae565b8893505b846001606060020a0316846001606060020a031611156107505789600185036001606060020a031681518110151561071857fe5b906020019060200201518a856001606060020a031681518110151561073957fe5b6020908102909101015260001993909301926106e4565b868a866001606060020a031681518110151561076857fe5b602090810290910101525b6001909801975b60019097019661053d565b6005546001606060020a036c010000000000000000000000009091048116908a1610156107ba5760025460009b509b50610895565b6001891615156108595789600160026001606060020a038c1604036001606060020a03168151811015156107ea57fe5b6020908102909101015191508960026001606060020a038b16046001606060020a031681518110151561081957fe5b6020908102909101015190506108406108328383610ef1565b671bc16d674ec80000610f13565b6fffffffffffffffffffffffffffffffff16925061088d565b8960026001606060020a036000198c0116046001606060020a031681518110151561088057fe5b9060200190602002015192505b8260019b509b505b505050505050505050509091565b60006108be6103e033600035600160e060020a031916610ddc565b5060055460a060020a908102819004600101026108e6600160a060020a031982161515610ee5565b6005546108f99060a060020a0283610b83565b600580546bffffffffffffffffffffffff191660a060020a90920491909117905550565b600160a060020a0381166000908152600460205260408120546109489160a060020a90910290610b83565b50565b60046020526000908152604090205460a060020a0281565b60055460a060020a0281565b600080600061097c61098f565b9150915061098981610ee5565b50919050565b60025460015460ff60a060020a909104169091565b600360205260009081526040902054600160a060020a031681565b60408051348082526020820183815236938301849052600435936024359384938693339360008035600160e060020a031916949092606082018484808284376040519201829003965090945050505050a4610a296103e033600035600160e060020a031916610ddc565b6001606060020a0383161515610a3e57600080fd5b5050600580546001606060020a039092166c010000000000000000000000000277ffffffffffffffffffffffff00000000000000000000000019909216919091179055565b610a9c6103e033600035600160e060020a031916610ddc565b60008054600160a060020a031916600160a060020a03838116919091178083556040519116917f1abebea81bfa2637f28358c371278fb15ede7ea8dd28d2e03b112ff6d936ada491a250565b600154600160a060020a031681565b60408051348082526020820183815236938301849052600435936024359384938693339360008035600160e060020a031916949092606082018484808284376040519201829003965090945050505050a4610b616103e033600035600160e060020a031916610ddc565b50506001805474ff000000000000000000000000000000000000000019169055565b60408051348082526020820183815236938301849052600435936024359384938693339360008035600160e060020a031916949092606082018484808284376040519201829003965090945050505050a4610bed6103e033600035600160e060020a031916610ddc565b600160a060020a031984161515610c0357600080fd5b600160a060020a03831615801590610c425750600160a060020a03831660009081526004602052604090205460a060020a02600160a060020a03191615155b15610c4c57600080fd5b600160a060020a03198416600090815260036020908152604080832054600160a060020a039081168452600490925290912080546bffffffffffffffffffffffff19169055831615610ccc57600160a060020a038316600090815260046020526040902080546bffffffffffffffffffffffff191660a060020a86041790555b5050600160a060020a031991821660009081526003602052604090208054600160a060020a0390921691909216179055565b600054600160a060020a031681565b610948816000610b83565b60408051348082526020820183815236938301849052600435936024359384938693339360008035600160e060020a031916949092606082018484808284376040519201829003965090945050505050a4610d826103e033600035600160e060020a031916610ddc565b600160a060020a031983161515610d9857600080fd5b5050600580546bffffffffffffffffffffffff191660a060020a909204919091179055565b6005546c0100000000000000000000000090046001606060020a031681565b6000600160a060020a038316301415610df757506001610edf565b600154600160a060020a0384811691161415610e1557506001610edf565b600054600160a060020a03161515610e2f57506000610edf565b60008054604080517fb7009613000000000000000000000000000000000000000000000000000000008152600160a060020a038781166004830152306024830152600160e060020a0319871660448301529151919092169263b700961392606480820193602093909283900390910190829087803b158015610eb057600080fd5b505af1158015610ec4573d6000803e3d6000fd5b505050506040513d6020811015610eda57600080fd5b505190505b92915050565b80151561094857600080fd5b8082016fffffffffffffffffffffffffffffffff8084169082161015610edf57fe5b6000610f806fffffffffffffffffffffffffffffffff8316600281046fffffffffffffffffffffffffffffffff16670de0b6b3a76400006fffffffffffffffffffffffffffffffff16866fffffffffffffffffffffffffffffffff160201811515610f7a57fe5b04610f87565b9392505050565b806fffffffffffffffffffffffffffffffff81168114610fa357fe5b9190505600a165627a7a7230582061d805eeaa2402ce57500ce0d10230203245788337e6f80195e4bba641671fd20029";

    public static final String FUNC_SETOWNER = "setOwner";

    public static final String FUNC_poke = "poke";

    public static final String FUNC_COMPUTE = "compute";

    public static final String FUNC_set = "set";

    public static final String FUNC_unset = "unset";

    public static final String FUNC_INDEXES = "indexes";

    public static final String FUNC_NEXT = "next";

    public static final String FUNC_READ = "read";

    public static final String FUNC_PEEK = "peek";

    public static final String FUNC_VALUES = "values";

    public static final String FUNC_SETMIN = "setMin";

    public static final String FUNC_SETAUTHORITY = "setAuthority";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_VOID = "void";

    public static final String FUNC_AUTHORITY = "authority";

    public static final String FUNC_SETNEXT = "setNext";

    public static final String FUNC_MIN = "min";

    public static final Event LOGNOTE_EVENT = new Event("LogNote", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes4>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<DynamicBytes>() {}));
    ;

    public static final Event LOGSETAUTHORITY_EVENT = new Event("LogSetAuthority", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    public static final Event LOGSETOWNER_EVENT = new Event("LogSetOwner", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected MedianizerContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MedianizerContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected MedianizerContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected MedianizerContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> setOwner(String owner_) {
        final Function function = new Function(
                FUNC_SETOWNER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> poke(byte[] param0) {
        final Function function = new Function(
                FUNC_poke, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> poke() {
        final Function function = new Function(
                FUNC_poke, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple2<byte[], Boolean>> compute() {
        final Function function = new Function(FUNC_COMPUTE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple2<byte[], Boolean>>(function,
                new Callable<Tuple2<byte[], Boolean>>() {
                    @Override
                    public Tuple2<byte[], Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<byte[], Boolean>(
                                (byte[]) results.get(0).getValue(), 
                                (Boolean) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> set(String wat) {
        final Function function = new Function(
                FUNC_set, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, wat)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> unset(String wat) {
        final Function function = new Function(
                FUNC_unset, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, wat)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<byte[]> indexes(String param0) {
        final Function function = new Function(FUNC_INDEXES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes12>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<byte[]> next() {
        final Function function = new Function(FUNC_NEXT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes12>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<byte[]> read() {
        final Function function = new Function(FUNC_READ, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<Tuple2<byte[], Boolean>> peek() {
        final Function function = new Function(FUNC_PEEK, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple2<byte[], Boolean>>(function,
                new Callable<Tuple2<byte[], Boolean>>() {
                    @Override
                    public Tuple2<byte[], Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<byte[], Boolean>(
                                (byte[]) results.get(0).getValue(), 
                                (Boolean) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> values(byte[] param0) {
        final Function function = new Function(FUNC_VALUES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes12(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> setMin(BigInteger min_) {
        final Function function = new Function(
                FUNC_SETMIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint96(min_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setAuthority(String authority_) {
        final Function function = new Function(
                FUNC_SETAUTHORITY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, authority_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> _void() {
        final Function function = new Function(
                FUNC_VOID, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> set(byte[] pos, String wat) {
        final Function function = new Function(
                FUNC_set, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes12(pos), 
                new org.web3j.abi.datatypes.Address(160, wat)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> authority() {
        final Function function = new Function(FUNC_AUTHORITY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> unset(byte[] pos) {
        final Function function = new Function(
                FUNC_unset, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes12(pos)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setNext(byte[] next_) {
        final Function function = new Function(
                FUNC_SETNEXT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes12(next_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> min() {
        final Function function = new Function(FUNC_MIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint96>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public List<LogNoteEventResponse> getLogNoteEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGNOTE_EVENT, transactionReceipt);
        ArrayList<LogNoteEventResponse> responses = new ArrayList<LogNoteEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogNoteEventResponse typedResponse = new LogNoteEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sig = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.guy = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.foo = (byte[]) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.bar = (byte[]) eventValues.getIndexedValues().get(3).getValue();
            typedResponse.wad = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.fax = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogNoteEventResponse> logNoteEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, LogNoteEventResponse>() {
            @Override
            public LogNoteEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGNOTE_EVENT, log);
                LogNoteEventResponse typedResponse = new LogNoteEventResponse();
                typedResponse.log = log;
                typedResponse.sig = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.guy = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.foo = (byte[]) eventValues.getIndexedValues().get(2).getValue();
                typedResponse.bar = (byte[]) eventValues.getIndexedValues().get(3).getValue();
                typedResponse.wad = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.fax = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogNoteEventResponse> logNoteEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGNOTE_EVENT));
        return logNoteEventFlowable(filter);
    }

    public List<LogSetAuthorityEventResponse> getLogSetAuthorityEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGSETAUTHORITY_EVENT, transactionReceipt);
        ArrayList<LogSetAuthorityEventResponse> responses = new ArrayList<LogSetAuthorityEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogSetAuthorityEventResponse typedResponse = new LogSetAuthorityEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.authority = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogSetAuthorityEventResponse> logSetAuthorityEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, LogSetAuthorityEventResponse>() {
            @Override
            public LogSetAuthorityEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGSETAUTHORITY_EVENT, log);
                LogSetAuthorityEventResponse typedResponse = new LogSetAuthorityEventResponse();
                typedResponse.log = log;
                typedResponse.authority = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogSetAuthorityEventResponse> logSetAuthorityEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGSETAUTHORITY_EVENT));
        return logSetAuthorityEventFlowable(filter);
    }

    public List<LogSetOwnerEventResponse> getLogSetOwnerEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGSETOWNER_EVENT, transactionReceipt);
        ArrayList<LogSetOwnerEventResponse> responses = new ArrayList<LogSetOwnerEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogSetOwnerEventResponse typedResponse = new LogSetOwnerEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogSetOwnerEventResponse> logSetOwnerEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, LogSetOwnerEventResponse>() {
            @Override
            public LogSetOwnerEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGSETOWNER_EVENT, log);
                LogSetOwnerEventResponse typedResponse = new LogSetOwnerEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogSetOwnerEventResponse> logSetOwnerEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGSETOWNER_EVENT));
        return logSetOwnerEventFlowable(filter);
    }

    @Deprecated
    public static MedianizerContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MedianizerContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static MedianizerContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MedianizerContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static MedianizerContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new MedianizerContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static MedianizerContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new MedianizerContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<MedianizerContract> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(MedianizerContract.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<MedianizerContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MedianizerContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<MedianizerContract> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(MedianizerContract.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<MedianizerContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MedianizerContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class LogNoteEventResponse extends BaseEventResponse {
        public byte[] sig;

        public String guy;

        public byte[] foo;

        public byte[] bar;

        public BigInteger wad;

        public byte[] fax;
    }

    public static class LogSetAuthorityEventResponse extends BaseEventResponse {
        public String authority;
    }

    public static class LogSetOwnerEventResponse extends BaseEventResponse {
        public String owner;
    }
}
