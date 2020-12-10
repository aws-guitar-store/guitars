/*
 * MIT License
 *
 * Copyright (c) 2020 Rarysoft Enterprises
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.example.sbms.guitars.api;

import com.example.sbms.guitars.api.model.Filter;
import com.example.sbms.guitars.domain.model.Guitar;
import com.example.sbms.guitars.domain.model.Guitars;
import com.example.sbms.guitars.domain.service.GetGuitars;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EnableBinding({ Processor.class })
@Component
public class GuitarsController {
    private final GetGuitars getGuitars;

    public GuitarsController(GetGuitars getGuitars) {
        this.getGuitars = getGuitars;
    }

    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public Message<Guitars> receiveGuitarsRequest(Message<Filter> filterRequest) {
        Filter filter = filterRequest.getPayload();
        if (filter.forAll()) {
            return buildReplyMessage(new Guitars(getGuitars.allByMakeAndModel()), filterRequest.getHeaders());
        }
        if (filter.forId()) {
            Optional<Guitar> guitar = getGuitars.byId(filter.id());
            List<Guitar> guitars = new ArrayList<>();
            guitar.ifPresent(guitars::add);
            return buildReplyMessage(new Guitars(guitars), filterRequest.getHeaders());
        }
        // TODO: handle other filters
        return buildReplyMessage(new Guitars(), filterRequest.getHeaders());
    }

    private <T> Message<T> buildReplyMessage(T payload, MessageHeaders headers) {
        return MessageBuilder
                .withPayload(payload)
                .copyHeaders(headers)
                .build();
    }
}
