package de.fs92.defi.flipper;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Bytes4;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint48;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple8;
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
public class FlipperContract extends Contract {
    private static final String BINARY = "6080604052670e92596fd629000060045560058054612a3065ffffffffffff199091161765ffffffffffff60301b19166802a300000000000000179055600060065534801561004d57600080fd5b50604051611ae9380380611ae98339818101604052604081101561007057600080fd5b508051602091820151600280546001600160a01b039093166001600160a01b031990931692909217909155600355336000908152908190526040902060019055611a2a806100bf6000396000f3fe608060405234801561001057600080fd5b506004361061010b5760003560e01c806365fae35e116100a2578063c5ce281e11610071578063c5ce281e14610334578063c959c42b1461033c578063cfc4af5514610359578063cfdd330214610361578063fc7b6aee146103695761010b565b806365fae35e146102ba5780637d780d82146102e05780639c52a7f1146102e8578063bf353dbb1461030e5761010b565b80634423c5f1116100de5780634423c5f1146101ca5780634b43ed12146102455780634e8b1dd51461026e5780635ff3a382146102915761010b565b806326e027f11461011057806329ae81141461012f578063351de6001461015257806336569e77146101a6575b600080fd5b61012d6004803603602081101561012657600080fd5b5035610386565b005b61012d6004803603604081101561014557600080fd5b5080359060200135610682565b610194600480360360a081101561016857600080fd5b506001600160a01b038135811691602081013590911690604081013590606081013590608001356107e1565b60405190815260200160405180910390f35b6101ae610aaf565b6040516001600160a01b03909116815260200160405180910390f35b6101e7600480360360208110156101e057600080fd5b5035610abe565b60405197885260208801969096526001600160a01b0394851660408089019190915265ffffffffffff9485166060890152929093166080870152831660a0860152911660c084015260e0830191909152610100909101905180910390f35b61012d6004803603606081101561025b57600080fd5b5080359060208101359060400135610b1e565b610276611075565b60405165ffffffffffff909116815260200160405180910390f35b61012d600480360360608110156102a757600080fd5b5080359060208101359060400135611083565b61012d600480360360208110156102d057600080fd5b50356001600160a01b031661154d565b6101946115ff565b61012d600480360360208110156102fe57600080fd5b50356001600160a01b0316611605565b6101946004803603602081101561032457600080fd5b50356001600160a01b03166116b4565b6101946116c8565b61012d6004803603602081101561035257600080fd5b50356116ce565b61027661182e565b610194611843565b61012d6004803603602081101561037f57600080fd5b5035611849565b336000908152602081905260409020546001146103e25760405162461bcd60e51b8152602060048201526016602482015275119b1a5c1c195c8bdb9bdd0b585d5d1a1bdc9a5e995960521b604482015260640160405180910390fd5b6000818152600160205260408120600201546001600160a01b031614156104455760405162461bcd60e51b8152602060048201526013602482015272119b1a5c1c195c8bd9dd5e4b5b9bdd0b5cd95d606a1b604482015260640160405180910390fd5b600081815260016020526040902060050154600082815260016020526040902054106104b75760405162461bcd60e51b815260206004820152601a60248201527f466c69707065722f616c72656164792d64656e742d7068617365000000000000604482015260640160405180910390fd5b600254600354600083815260016020526001600160a01b0390921691636111be2e91903090339060409020600101546040516001600160e01b031960e087901b16815260048101949094526001600160a01b039283166024850152911660448301526064820152608401600060405180830381600087803b15801561053b57600080fd5b505af115801561054f573d6000803e3d6000fd5b5050600254600084815260016020526001600160a01b03909116925063bb35783b915033906040902060020154600085815260016020526001600160a01b039091169060409020546040516001600160e01b031960e086901b1681526001600160a01b0393841660048201529190921660248201526044810191909152606401600060405180830381600087803b1580156105e957600080fd5b505af11580156105fd573d6000803e3d6000fd5b505050600082815260016020526040915020600080825560018201819055600282018190556003820180546001600160a01b031990811690915560048301805490911690556005909101555961012081016040526020815260e0602082015260e060006040830137602435600435336001600160e01b03196000351661012085a45050565b336000908152602081905260409020546001146106de5760405162461bcd60e51b8152602060048201526016602482015275119b1a5c1c195c8bdb9bdd0b585d5d1a1bdc9a5e995960521b604482015260640160405180910390fd5b816262656760e81b14156106f65760048190556107a6565b81621d1d1b60ea1b1415610722576005805465ffffffffffff191665ffffffffffff83161790556107a6565b816274617560e81b141561075a57600580546bffffffffffff0000000000001916600160301b65ffffffffffff8416021790556107a6565b60405162461bcd60e51b815260206004820152601f60248201527f466c69707065722f66696c652d756e7265636f676e697a65642d706172616d00604482015260640160405180910390fd5b5961012081016040526020815260e0602082015260e060006040830137602435600435336001600160e01b03196000351661012085a4505050565b3360009081526020819052604081205460011461083d5760405162461bcd60e51b8152602060048201526016602482015275119b1a5c1c195c8bdb9bdd0b585d5d1a1bdc9a5e995960521b604482015260640160405180910390fd5b600019600654106108875760405162461bcd60e51b815260206004820152601060248201526f466c69707065722f6f766572666c6f7760801b604482015260640160405180910390fd5b50600680546001908101918290556000828152602091909152829060409020556000818152600160205283906040902060010181905550336001600083815260200190815260200160002060020180546001600160a01b0319166001600160a01b0392909216919091179055600554610910904290600160301b900465ffffffffffff166119b0565b6000828152600160205260409020600201805465ffffffffffff92909216600160d01b026001600160d01b039092169190911790556000818152600160205286906040902060030180546001600160a01b0319166001600160a01b03929092169190911790556000818152600160205285906040902060040180546001600160a01b0319166001600160a01b039290921691909117905560008181526001602052849060409020600501556002546003546001600160a01b0390911690636111be2e903330876040516001600160e01b031960e087901b16815260048101949094526001600160a01b039283166024850152911660448301526064820152608401600060405180830381600087803b158015610a2b57600080fd5b505af1158015610a3f573d6000803e3d6000fd5b50505050846001600160a01b0316866001600160a01b03167fc84ce3a1172f0dec3173f04caaa6005151a4bfe40d4c9f3ea28dba5f719b2a7a838686896040518085815260200184815260200183815260200182815260200194505050505060405180910390a395945050505050565b6002546001600160a01b031681565b60016020528060005260406000208054600182015460028301546003840154600485015460059095015493955091936001600160a01b038083169465ffffffffffff600160a01b8504811695600160d01b90950416938216929091169088565b6000838152600160205260408120600201546001600160a01b03161415610b815760405162461bcd60e51b8152602060048201526013602482015272119b1a5c1c195c8bd9dd5e4b5b9bdd0b5cd95d606a1b604482015260640160405180910390fd5b6000838152600160205242906040902060020154600160a01b900465ffffffffffff161180610bce5750600083815260016020526040902060020154600160a01b900465ffffffffffff16155b610c1e5760405162461bcd60e51b815260206004820152601c60248201527f466c69707065722f616c72656164792d66696e69736865642d74696300000000604482015260640160405180910390fd5b6000838152600160205242906040902060020154600160d01b900465ffffffffffff1611610c925760405162461bcd60e51b815260206004820152601c60248201527f466c69707065722f616c72656164792d66696e69736865642d656e6400000000604482015260640160405180910390fd5b6000838152600160205260409020600101548214610cf65760405162461bcd60e51b815260206004820152601860248201527f466c69707065722f6c6f742d6e6f742d6d61746368696e670000000000000000604482015260640160405180910390fd5b600083815260016020526040902060050154811115610d5b5760405162461bcd60e51b815260206004820152601760248201527f466c69707065722f6869676865722d7468616e2d746162000000000000000000604482015260640160405180910390fd5b6000838152600160205260409020548111610db55760405162461bcd60e51b8152602060048201526016602482015275233634b83832b917b134b216b737ba16b434b3b432b960511b604482015260640160405180910390fd5b60045460008481526001602052610dd1919060409020546119d1565b610de382670de0b6b3a76400006119d1565b101580610e00575060008381526001602052604090206005015481145b610e505760405162461bcd60e51b815260206004820152601d60248201527f466c69707065722f696e73756666696369656e742d696e637265617365000000604482015260640160405180910390fd5b600254600084815260016020526001600160a01b039091169063bb35783b9033906040902060020154600087815260016020526001600160a01b039091169060409020546040516001600160e01b031960e086901b1681526001600160a01b0393841660048201529190921660248201526044810191909152606401600060405180830381600087803b158015610ee657600080fd5b505af1158015610efa573d6000803e3d6000fd5b5050600254600086815260016020526001600160a01b03909116925063bb35783b915033906040902060040154600087815260016020526001600160a01b0390911690604090205485036040516001600160e01b031960e086901b1681526001600160a01b0393841660048201529190921660248201526044810191909152606401600060405180830381600087803b158015610f9657600080fd5b505af1158015610faa573d6000803e3d6000fd5b505050600084815260016020523391506040902060020180546001600160a01b0319166001600160a01b0392909216919091179055600083815260016020528190604090205560055461100690429065ffffffffffff166119b0565b600084815260016020526040902060020160146101000a81548165ffffffffffff021916908365ffffffffffff1602179055505961012081016040526020815260e0602082015260e060006040830137602435600435336001600160e01b03196000351661012085a450505050565b60055465ffffffffffff1681565b6000838152600160205260408120600201546001600160a01b031614156110e65760405162461bcd60e51b8152602060048201526013602482015272119b1a5c1c195c8bd9dd5e4b5b9bdd0b5cd95d606a1b604482015260640160405180910390fd5b6000838152600160205242906040902060020154600160a01b900465ffffffffffff1611806111335750600083815260016020526040902060020154600160a01b900465ffffffffffff16155b6111835760405162461bcd60e51b815260206004820152601c60248201527f466c69707065722f616c72656164792d66696e69736865642d74696300000000604482015260640160405180910390fd5b6000838152600160205242906040902060020154600160d01b900465ffffffffffff16116111f75760405162461bcd60e51b815260206004820152601c60248201527f466c69707065722f616c72656164792d66696e69736865642d656e6400000000604482015260640160405180910390fd5b60008381526001602052604090205481146112585760405162461bcd60e51b815260206004820152601860248201527f466c69707065722f6e6f742d6d61746368696e672d6269640000000000000000604482015260640160405180910390fd5b60008381526001602052604090206005015481146112bc5760405162461bcd60e51b815260206004820152601960248201527f466c69707065722f74656e642d6e6f742d66696e697368656400000000000000604482015260640160405180910390fd5b60008381526001602052604090206001015482106113185760405162461bcd60e51b8152602060048201526015602482015274233634b83832b917b637ba16b737ba16b637bbb2b960591b604482015260640160405180910390fd5b6000838152600160205261133c906040902060010154670de0b6b3a76400006119d1565b611348600454846119d1565b111561139a5760405162461bcd60e51b815260206004820152601d60248201527f466c69707065722f696e73756666696369656e742d6465637265617365000000604482015260640160405180910390fd5b600254600084815260016020526001600160a01b039091169063bb35783b90339060409020600201546001600160a01b0316846040516001600160e01b031960e086901b1681526001600160a01b0393841660048201529190921660248201526044810191909152606401600060405180830381600087803b15801561141f57600080fd5b505af1158015611433573d6000803e3d6000fd5b5050600254600354600087815260016020526001600160a01b039092169350636111be2e92509030906040902060030154600088815260016020526001600160a01b039091169087906040902060010154036040516001600160e01b031960e087901b16815260048101949094526001600160a01b039283166024850152911660448301526064820152608401600060405180830381600087803b1580156114da57600080fd5b505af11580156114ee573d6000803e3d6000fd5b505050600084815260016020523391506040902060020180546001600160a01b0319166001600160a01b0392909216919091179055600083815260016020528290604090206001015560055461100690429065ffffffffffff166119b0565b336000908152602081905260409020546001146115a95760405162461bcd60e51b8152602060048201526016602482015275119b1a5c1c195c8bdb9bdd0b585d5d1a1bdc9a5e995960521b604482015260640160405180910390fd5b6001600160a01b0381166000908152602081905260019060409020555961012081016040526020815260e0602082015260e060006040830137602435600435336001600160e01b03196000351661012085a45050565b60045481565b336000908152602081905260409020546001146116615760405162461bcd60e51b8152602060048201526016602482015275119b1a5c1c195c8bdb9bdd0b585d5d1a1bdc9a5e995960521b604482015260640160405180910390fd5b6001600160a01b0381166000908152602081905260408120555961012081016040526020815260e0602082015260e060006040830137602435600435336001600160e01b03196000351661012085a45050565b600060205280600052604060002054905081565b60035481565b600081815260016020526040902060020154600160a01b900465ffffffffffff161580159061174757506000818152600160205242906040902060020154600160a01b900465ffffffffffff16108061174757506000818152600160205242906040902060020154600160d01b900465ffffffffffff16105b61178e5760405162461bcd60e51b8152602060048201526014602482015273119b1a5c1c195c8bdb9bdd0b599a5b9a5cda195960621b604482015260640160405180910390fd5b600254600354600083815260016020526001600160a01b0390921691636111be2e919030906040902060020154600086815260016020526001600160a01b039091169060409020600101546040516001600160e01b031960e087901b16815260048101949094526001600160a01b039283166024850152911660448301526064820152608401600060405180830381600087803b1580156105e957600080fd5b600554600160301b900465ffffffffffff1681565b60065481565b6000818152600160205242906040902060020154600160d01b900465ffffffffffff16106118b45760405162461bcd60e51b8152602060048201526014602482015273119b1a5c1c195c8bdb9bdd0b599a5b9a5cda195960621b604482015260640160405180910390fd5b600081815260016020526040902060020154600160a01b900465ffffffffffff16156119265760405162461bcd60e51b815260206004820152601a60248201527f466c69707065722f6269642d616c72656164792d706c61636564000000000000604482015260640160405180910390fd5b600554611943904290600160301b900465ffffffffffff166119b0565b6000828152600160205260409020600201601a6101000a81548165ffffffffffff021916908365ffffffffffff1602179055505961012081016040526020815260e0602082015260e060006040830137602435600435336001600160e01b03196000351661012085a45050565b80820165ffffffffffff80841690821610156119cb57600080fd5b92915050565b60008115806119ec575050808202828282816119e957fe5b04145b6119cb57600080fdfea265627a7a723158208c5e96ec6d7de700503c7fe7185bf5e99dc3831481aaa6da47b3b9f261a6990764736f6c63430005110032";

    public static final String FUNC_BEG = "beg";

    public static final String FUNC_BIDS = "bids";

    public static final String FUNC_DEAL = "deal";

    public static final String FUNC_DENT = "dent";

    public static final String FUNC_DENY = "deny";

    public static final String FUNC_FILE = "file";

    public static final String FUNC_ILK = "ilk";

    public static final String FUNC_KICK = "kick";

    public static final String FUNC_KICKS = "kicks";

    public static final String FUNC_RELY = "rely";

    public static final String FUNC_TAU = "tau";

    public static final String FUNC_TEND = "tend";

    public static final String FUNC_TICK = "tick";

    public static final String FUNC_TTL = "ttl";

    public static final String FUNC_VAT = "vat";

    public static final String FUNC_WARDS = "wards";

    public static final String FUNC_YANK = "yank";

    public static final Event KICK_EVENT = new Event("Kick", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event LOGNOTE_EVENT = new Event("LogNote", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes4>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<DynamicBytes>() {}));
    ;

    @Deprecated
    protected FlipperContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FlipperContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected FlipperContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected FlipperContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<KickEventResponse> getKickEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(KICK_EVENT, transactionReceipt);
        ArrayList<KickEventResponse> responses = new ArrayList<KickEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            KickEventResponse typedResponse = new KickEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.usr = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.gal = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.lot = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.bid = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.tab = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<KickEventResponse> kickEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, KickEventResponse>() {
            @Override
            public KickEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(KICK_EVENT, log);
                KickEventResponse typedResponse = new KickEventResponse();
                typedResponse.log = log;
                typedResponse.usr = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.gal = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.lot = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.bid = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.tab = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<KickEventResponse> kickEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(KICK_EVENT));
        return kickEventFlowable(filter);
    }

    public List<LogNoteEventResponse> getLogNoteEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGNOTE_EVENT, transactionReceipt);
        ArrayList<LogNoteEventResponse> responses = new ArrayList<LogNoteEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogNoteEventResponse typedResponse = new LogNoteEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sig = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.usr = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.arg1 = (byte[]) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.arg2 = (byte[]) eventValues.getIndexedValues().get(3).getValue();
            typedResponse.data = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogNoteEventResponse> logNoteEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogNoteEventResponse>() {
            @Override
            public LogNoteEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGNOTE_EVENT, log);
                LogNoteEventResponse typedResponse = new LogNoteEventResponse();
                typedResponse.log = log;
                typedResponse.sig = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.usr = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.arg1 = (byte[]) eventValues.getIndexedValues().get(2).getValue();
                typedResponse.arg2 = (byte[]) eventValues.getIndexedValues().get(3).getValue();
                typedResponse.data = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogNoteEventResponse> logNoteEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGNOTE_EVENT));
        return logNoteEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> beg() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BEG, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>> bids(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BIDS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint48>() {}, new TypeReference<Uint48>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>>(function,
                new Callable<Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>>() {
                    @Override
                    public Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (String) results.get(5).getValue(), 
                                (String) results.get(6).getValue(), 
                                (BigInteger) results.get(7).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> deal(BigInteger id) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(id)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> dent(BigInteger id, BigInteger lot, BigInteger bid) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(id), 
                new org.web3j.abi.datatypes.generated.Uint256(lot), 
                new org.web3j.abi.datatypes.generated.Uint256(bid)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> deny(String usr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DENY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, usr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> file(byte[] what, BigInteger data) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_FILE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(what), 
                new org.web3j.abi.datatypes.generated.Uint256(data)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<byte[]> ilk() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ILK, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> kick(String usr, String gal, BigInteger tab, BigInteger lot, BigInteger bid) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_KICK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, usr), 
                new org.web3j.abi.datatypes.Address(160, gal), 
                new org.web3j.abi.datatypes.generated.Uint256(tab), 
                new org.web3j.abi.datatypes.generated.Uint256(lot), 
                new org.web3j.abi.datatypes.generated.Uint256(bid)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> kicks() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_KICKS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> rely(String usr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RELY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, usr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> tau() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TAU, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint48>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> tend(BigInteger id, BigInteger lot, BigInteger bid) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TEND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(id), 
                new org.web3j.abi.datatypes.generated.Uint256(lot), 
                new org.web3j.abi.datatypes.generated.Uint256(bid)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> tick(BigInteger id) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TICK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(id)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> ttl() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TTL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint48>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> vat() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_VAT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> wards(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_WARDS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> yank(BigInteger id) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_YANK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(id)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static FlipperContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FlipperContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static FlipperContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FlipperContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static FlipperContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new FlipperContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static FlipperContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new FlipperContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<FlipperContract> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String vat_, byte[] ilk_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, vat_), 
                new org.web3j.abi.datatypes.generated.Bytes32(ilk_)));
        return deployRemoteCall(FlipperContract.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<FlipperContract> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String vat_, byte[] ilk_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, vat_), 
                new org.web3j.abi.datatypes.generated.Bytes32(ilk_)));
        return deployRemoteCall(FlipperContract.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<FlipperContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String vat_, byte[] ilk_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, vat_), 
                new org.web3j.abi.datatypes.generated.Bytes32(ilk_)));
        return deployRemoteCall(FlipperContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<FlipperContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String vat_, byte[] ilk_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, vat_), 
                new org.web3j.abi.datatypes.generated.Bytes32(ilk_)));
        return deployRemoteCall(FlipperContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class KickEventResponse extends BaseEventResponse {
        public String usr;

        public String gal;

        public BigInteger id;

        public BigInteger lot;

        public BigInteger bid;

        public BigInteger tab;
    }

    public static class LogNoteEventResponse extends BaseEventResponse {
        public byte[] sig;

        public String usr;

        public byte[] arg1;

        public byte[] arg2;

        public byte[] data;
    }
}
