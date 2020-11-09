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
package com.example.sbms.guitars.service;

import com.example.sbms.guitars.model.Filter;
import com.example.sbms.guitars.model.Guitar;
import com.example.sbms.guitars.model.Guitars;
import com.example.sbms.guitars.service.data.GuitarRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@EnableKafka
public class GetGuitars {
    private final KafkaTemplate<String, Guitars> kafkaTemplate;
    private final String guitarsProvidedTopic;
    private final GuitarRepository guitarRepository;

    public GetGuitars(KafkaTemplate<String, Guitars> kafkaTemplate, @Value("${spring.kafka.producer.properties.event.guitars-provided.topic}") String guitarsProvidedTopic, GuitarRepository guitarRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.guitarsProvidedTopic = guitarsProvidedTopic;
        this.guitarRepository = guitarRepository;
    }

    public List<Guitar> allByMakeAndModel() {
        return guitarRepository.findAll(Sort.by("make", "model"));
    }

    @KafkaListener(topics = "${spring.kafka.consumer.properties.event.guitars-requested.topic}", containerFactory = "filterKafkaListenerContainerFactory")
    public void receiveGuitarsRequest(@Payload Filter filter) {
        if (filter.forAll()) {
            this.buildAndSendMessage(new Guitars(true, guitarRepository.findAll(Sort.by("make", "model"))));
            return;
        }
        if (filter.forId()) {
            Optional<Guitar> guitar = guitarRepository.findById(filter.id());
            List<Guitar> guitars = new ArrayList<>();
            guitar.ifPresent(guitars::add);
            this.buildAndSendMessage(new Guitars(false, guitars));
            return;
        }
        // TODO: handle other filters
    }

    private void buildAndSendMessage(Guitars guitars) {
        Message<Guitars> message = MessageBuilder
                .withPayload(guitars)
                .setHeader(KafkaHeaders.TOPIC, guitarsProvidedTopic)
                .build();
        // TODO: handle errors
        kafkaTemplate.send(message);
    }
}
