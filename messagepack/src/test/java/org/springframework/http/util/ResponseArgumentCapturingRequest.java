package org.springframework.http.util;


import org.msgpack.MessagePackObject;
import org.msgpack.rpc.Request;
import org.msgpack.rpc.transport.MessageSendable;

public class ResponseArgumentCapturingRequest extends Request  {
	public ResponseArgumentCapturingRequest(MessageSendable channel, int msgid, String method, MessagePackObject args) {
		super(channel, msgid, method, args);
	}

	public ResponseArgumentCapturingRequest(String method, MessagePackObject args) {
		super(method, args);
	}

	private Object result;
	@Override
	public void sendResult(Object result) {
		this.result = result ;
        super.sendResult(result);
	}

	public Object getResult() {
		return result;
	}
}
