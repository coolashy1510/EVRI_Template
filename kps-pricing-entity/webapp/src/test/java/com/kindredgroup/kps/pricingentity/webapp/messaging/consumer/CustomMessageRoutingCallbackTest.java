//package com.kindredgroup.kps.pricingentity.webapp.messaging.consumer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;
//import org.jetbrains.annotations.NotNull;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.EnumSource;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.springframework.messaging.support.GenericMessage;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//class CustomMessageRoutingCallbackTest {
//
//    CustomMessageRoutingCallback messageRoutingCallback;
//    ObjectMapper objectMapper = new ObjectMapper();
//
//    @NotNull
//    private static Map<String, Object> getHeaders(String messageType) {
//        Map<String, Object> headers = new HashMap<>();
//        headers.put("messageType", messageType.getBytes());
//        return headers;
//    }
//
//    @BeforeEach
//    void setup() {
//        messageRoutingCallback = new CustomMessageRoutingCallback();
//    }
//
//    @ParameterizedTest
//    @EnumSource(MessageTypeToBinding.class)
//    void routingResult(MessageTypeToBinding messageTypeToBinding) throws JsonProcessingException {
//        String routing = messageRoutingCallback.routingResult(
//                new GenericMessage<>(objectMapper.writeValueAsBytes(new FeedProvidedImpl(FeedProvider.BET_GENIUS)),
//                        getHeaders(messageTypeToBinding.name())));
//        assertEquals(messageTypeToBinding.getBinding(), routing);
//    }
//
//    @Test
//    void routingResul2t() throws JsonProcessingException {
//        Map<String, Object> headers = getHeaders(MessageTypeToBinding.Contest.name());
//        GenericMessage<byte[]> message = new GenericMessage<>(
//                objectMapper.writeValueAsBytes(new FeedProvidedImpl(FeedProvider.QUANT)), headers);
//        assertThrows(IllegalArgumentException.class, () -> messageRoutingCallback.routingResult(message));
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"", "whatever"})
//    void failRoutingResult(String messageType) {
//        GenericMessage<String> message = new GenericMessage<>("any payload", getHeaders(messageType));
//        assertThrows(IllegalArgumentException.class, () -> messageRoutingCallback.routingResult(message));
//    }
//
//    @Test
//    void failRoutingResultMissingHeader() {
//        GenericMessage<String> message = new GenericMessage<>("any payload", new HashMap<>());
//        assertThrows(IllegalArgumentException.class, () -> messageRoutingCallback.routingResult(message));
//    }
//}
