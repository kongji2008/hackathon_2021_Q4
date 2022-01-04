package com.everbridge.hackathon.extauth;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.google.rpc.Code;
import com.google.rpc.Status;

import io.envoyproxy.envoy.config.core.v3.HeaderValue;
import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.envoyproxy.envoy.service.auth.v3.OkHttpResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
public class ExtAuthGrpcService extends AuthorizationGrpc.AuthorizationImplBase {

    public void check(CheckRequest request, StreamObserver<CheckResponse> responseObserver) {
        CheckResponse response = generateOkResponse();
        log.info(response.toString());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private CheckResponse generateOkResponse() {
        OkHttpResponse ok = OkHttpResponse.newBuilder()
                .addHeaders(HeaderValueOption.newBuilder().setAppend(BoolValue.of(false))
                        .setHeader(HeaderValue.newBuilder().setKey("X-RL-ORG-ID").setValue("1000001").build()))
                .addHeaders(HeaderValueOption.newBuilder().setAppend(BoolValue.of(false))
                        .setHeader(HeaderValue.newBuilder().setKey("X-RL-PRODUCT").setValue("notification").build()))
                .addHeaders(HeaderValueOption.newBuilder().setAppend(BoolValue.of(false))
                        .setHeader(HeaderValue.newBuilder().setKey("X-RL-METHOD").setValue("GET").build()))
                .build();

        Struct quota = Struct.newBuilder()
                .putFields("requests_per_unit", Value.newBuilder().setNumberValue(3).build())
                .putFields("unit", Value.newBuilder().setStringValue("MINUTE").build())
                .build();

        return CheckResponse.newBuilder().setOkResponse(ok)
                .setDynamicMetadata(Struct.newBuilder()
                        .putFields("rl_quota", Value.newBuilder().setStructValue(quota).build())
                        .build())
                .setStatus(Status.newBuilder().setCode(Code.OK.getNumber()).build())
                .build();
    }
}
