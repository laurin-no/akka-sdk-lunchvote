package com.example.lunchvote.area.api;

import akka.Done;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.annotations.http.Put;
import akka.javasdk.client.ComponentClient;
import com.example.lunchvote.area.application.AreaEntity;
import com.example.lunchvote.area.domain.Area;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/area")
public class AreaEndpoint {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ComponentClient entityClient;

    public AreaEndpoint(ComponentClient client) {
        this.entityClient = client;
    }

    @Post()
    public CompletionStage<Done> introduceArea(IntroduceAreaRequest request) {
        logger.debug("{}", request);

        var command = new Area.Command.IntroduceArea(request.organizationId, request.areaId);

        var id = request.organizationId + "-" + request.areaId;

        return entityClient.forEventSourcedEntity(id)
                .method(AreaEntity::introduceArea)
                .invokeAsync(command);
    }

    @Put("/introduce-place")
    public CompletionStage<Done> introducePlace(IntroducePlaceRequest request) {
        logger.debug("{}", request);

        var userId = "user1"; // TODO this should come form the token

        var command = new Area.Command.IntroducePlace(request.organizationId, request.areaId, request.placeId, userId);
        var id = request.organizationId + "-" + request.areaId;

        return entityClient.forEventSourcedEntity(id)
                .method(AreaEntity::introducePlace)
                .invokeAsync(command);
    }

    @Put("/archive-place")
    public CompletionStage<Done> archivePlace(IntroducePlaceRequest request) {
        logger.debug("{}", request);

        var command = new Area.Command.ArchivePlace(request.organizationId, request.areaId, request.placeId);
        var id = request.organizationId + "-" + request.areaId;

        return entityClient.forEventSourcedEntity(id)
                .method(AreaEntity::archivePlace)
                .invokeAsync(command);
    }

    @Get("/{organizationId}/{areaId}")
    public CompletionStage<Area.State> getAreaInfo(String organizationId, String areaId) {
        var id = organizationId + "-" + areaId;

        return entityClient.forEventSourcedEntity(id)
                .method(AreaEntity::get)
                .invokeAsync();
    }


    public record IntroduceAreaRequest(String organizationId, String areaId) {
    }

    public record IntroducePlaceRequest(String organizationId, String areaId, String placeId) {
    }

    public record ArchivePlaceRequest(String organizationId, String areaId, String placeId) {
    }
}
