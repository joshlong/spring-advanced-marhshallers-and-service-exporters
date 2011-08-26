/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.obm.messagepack.util;


import org.msgpack.MessagePackObject;
import org.msgpack.rpc.Request;
import org.msgpack.rpc.transport.MessageSendable;

/**
 * Shim to get in and see and expose the results of a call that uses a {@link java.util.concurrent.Future} in MessagePack
 */
public class ResponseArgumentCapturingRequest extends Request {
    public ResponseArgumentCapturingRequest(MessageSendable channel, int msgid, String method, MessagePackObject args) {
        super(channel, msgid, method, args);
    }

    public ResponseArgumentCapturingRequest(String method, MessagePackObject args) {
        super(method, args);
    }

    private Object result;

    @Override
    public void sendResult(Object result) {
        this.result = result;
        super.sendResult(result);
    }

    public Object getResult() {
        return result;
    }
}
