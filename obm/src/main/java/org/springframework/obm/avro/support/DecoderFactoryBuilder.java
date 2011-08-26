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
package org.springframework.obm.avro.support;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.InputStream;


/**
 * Convenience class to build a {@link org.apache.avro.io.Decoder}
 *
 * @author Josh Long
 */
public class DecoderFactoryBuilder {
    private final static ThreadLocal<Decoder> decoderThreadLocal = new ThreadLocal<Decoder>();

    private DecoderFactory decoderFactory = DecoderFactory.get();
    private boolean useBinary = true;
    private boolean validate = false;
    private boolean useJson = false;
    private String input;
    private Schema schema;
    private InputStream inputStream;

    public DecoderFactoryBuilder setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public DecoderFactoryBuilder setInput(String input) {
        this.input = input;
        return this;
    }

    public DecoderFactoryBuilder setSchema(Schema schema) {
        this.schema = schema;
        return this;
    }

    public DecoderFactoryBuilder setUseBinary(boolean useBinary) {
        this.useBinary = useBinary;
        return this;
    }

    public DecoderFactoryBuilder setUseJson(boolean useJson) {
        this.useJson = useJson;
        return this;
    }

    public DecoderFactoryBuilder setValidate(boolean validate) {
        this.validate = validate;
        return this;
    }


    protected Decoder buildThreadAwareDecoder() throws Exception {
        Decoder existingDecoder = decoderThreadLocal.get();
        decoderThreadLocal.set(build(existingDecoder));
        return decoderThreadLocal.get();
    }

    protected Decoder build(Decoder reuse) throws Exception {

        Decoder decoder = null;
        if (useBinary) {
            Assert.notNull(this.inputStream, "you've selected to use a binary decoder. " +
                                                     "Please provide the input stream to decode by " +
                                                     "setting the 'inputStream' property");
            decoder = decoderFactory.binaryDecoder(this.inputStream, (BinaryDecoder) reuse);
        } else if (useJson) {
            Assert.notNull(this.schema, "you've selected to use JSON. Please provide a target schema by setting the 'schema' property");
            Assert.isTrue(StringUtils.hasText(this.input) || this.inputStream != null, "there must be either an inputStream or an inputString to build a JsonDecoder");
            decoder = StringUtils.hasText(this.input) ?
                              decoderFactory.jsonDecoder(this.schema, this.input) :
                              decoderFactory.jsonDecoder(this.schema, this.inputStream);
        }

        Assert.notNull(decoder, "could not build a decoder. Did you set both 'useJson' and 'userBinary' to false?");

        if (validate) {
            Assert.notNull(this.schema, "you've selected to validate. Please provide a target schema by setting the 'schema' property");
            decoder = decoderFactory.validatingDecoder(this.schema, decoder);
        }
        return decoder;
    }

    public Decoder build() throws Exception {
        return buildThreadAwareDecoder();
    }

}
