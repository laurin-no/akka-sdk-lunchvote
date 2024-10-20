package com.example.lunchvote.area.api;

import akka.NotUsed;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.example.lunchvote.area.application.AreaView;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/area-view")
public class AreaViewEndpoint {
    private final ComponentClient entityClient;
    private final Materializer materializer;

    public AreaViewEndpoint(ComponentClient client, Materializer mat) {
        this.entityClient = client;
        this.materializer = mat;
    }

    // TODO can we stream this response?
    @Get("/by-organization/{organizationId}")
    public CompletionStage<List<AreaView.AreaRow>> getAreasByOrganization(String organizationId) {
        return entityClient.forView()
                .stream(AreaView::getAreasByOrganizationId)
                .source(organizationId)
                .runWith(Sink.seq(), materializer.system());
    }

}
