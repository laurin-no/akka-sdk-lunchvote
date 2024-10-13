package com.example.lunchvote.area;

import com.example.lunchvote.area.domain.AreaEvent;
import com.example.lunchvote.area.domain.AreaState;
import kalix.javasdk.annotations.EventHandler;
import kalix.javasdk.annotations.Id;
import kalix.javasdk.annotations.TypeId;
import kalix.javasdk.eventsourcedentity.EventSourcedEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Id({"organizationId", "areaId"})
@TypeId("area")
@RequestMapping("/area/{organizationId}/{areaId}")
public class AreaEntity extends EventSourcedEntity<AreaState, AreaEvent> {

    @Override
    public AreaState emptyState() {
        return AreaState.empty();
    }

    // Command Handlers

    @GetMapping()
    public Effect<AreaState> getArea() {
        return effects().reply(currentState());
    }

    @PostMapping("/introduce")
    public Effect<String> introduceArea(@PathVariable String organizationId, @PathVariable String areaId) {
        var event = new AreaEvent.AreaIntroduced(organizationId, areaId);

        return effects()
                .emitEvent(event)
                .thenReply(newState -> "OK");
    }

    @PostMapping("/place/{placeId}/introduce")
    public Effect<String> introducePlace(
            @PathVariable String organizationId,
            @PathVariable String areaId,
            @PathVariable String placeId
    ) {
        var user = "user1"; // TODO should come from claim

        if (currentState().equals(emptyState())) {
            return effects().error("Area does not exist");
        } else if (currentState().places().containsKey(placeId)) {
            return effects().error("Place already exists");
        }

        var event = new AreaEvent.PlaceIntroduced(organizationId, areaId, placeId, user);

        return effects()
                .emitEvent(event)
                .thenReply(newState -> "OK");

    }

    @PostMapping("/place/{placeId}/archive")
    public Effect<String> archivePlace(
            @PathVariable String organizationId,
            @PathVariable String areaId,
            @PathVariable String placeId
    ) {

        if (currentState().equals(emptyState())) {
            return effects().error("Area does not exist");
        } else if (!currentState().places().containsKey(placeId)) {
            return effects().error("Place does not exist");
        }

        var event = new AreaEvent.PlaceArchived(organizationId, areaId, placeId);

        return effects()
                .emitEvent(event)
                .thenReply(newState -> "OK");
    }

    // Event Handlers

    @EventHandler
    public AreaState areaIntroduced(AreaEvent.AreaIntroduced areaIntroduced) {
        return currentState().onAreaIntroduced(areaIntroduced);
    }

    @EventHandler
    public AreaState placeIntroduced(AreaEvent.PlaceIntroduced placeIntroduced) {
        return currentState().onPlaceIntroduced(placeIntroduced);
    }

    @EventHandler
    public AreaState placeArchived(AreaEvent.PlaceArchived placeArchived) {
        return currentState().onPlaceArchived(placeArchived);
    }
}
