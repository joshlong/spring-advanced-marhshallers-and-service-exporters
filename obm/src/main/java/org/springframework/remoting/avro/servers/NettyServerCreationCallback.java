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
package org.springframework.remoting.avro.servers;

import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.Responder;
import org.apache.avro.ipc.Server;

import java.net.InetSocketAddress;

/**
 * <p/>
 * implementation of {@link org.springframework.remoting.avro.AvroExporter} that builds a {@link NettyServer}.
 * <p/>
 * This should be your default choice to expose services in the {@link org.springframework.remoting.avro.AvroExporter} tree.
 *
 * @author Josh Long
 */
public class NettyServerCreationCallback implements ServerCreationCallback {

    @Override
    public Server buildServer(InetSocketAddress address, Responder responder) throws Exception {
        return new NettyServer(responder, address);
    }
}
