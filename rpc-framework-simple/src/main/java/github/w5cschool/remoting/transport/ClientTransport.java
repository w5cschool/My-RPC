package github.w5cschool.remoting.transport;

import github.w5cschool.extension.SPI;
import github.w5cschool.remoting.dto.RpcRequest;

/**
 * send RpcRequest。
 *
 */
@SPI
public interface ClientTransport {
    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest message body
     * @return data from server
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
