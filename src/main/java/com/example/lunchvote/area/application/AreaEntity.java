package com.example.lunchvote.area.application;

import akka.Done;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.ComponentId;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.example.lunchvote.area.domain.Area;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ComponentId("area")
@Acl(allow = @Acl.Matcher(service = "*"))
public class AreaEntity extends EventSourcedEntity<Area.State, Area.Event> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String entityId;

    public AreaEntity(EventSourcedEntityContext context) {
        entityId = context.entityId();
    }

    public Effect<Done> introduceArea(Area.Command.IntroduceArea command) {
        logger.info("EntityUd: {}\n_State: {}\n_Command: {}", entityId, currentState(), command);

        // validate command
        if (isBlank(command.areaId()) || isBlank(command.organizationId())) {
            return effects().error("AreaId and OrganizationId are required");
        }

        var event = currentState().onCommand(command);

        if (event.isEmpty()) {
            return effects().reply(Done.getInstance()); // area already exists
        } else {
            return effects()
                    .persist(event.get())
                    .thenReply(newState -> Done.getInstance());
        }
    }

    public Effect<Done> introducePlace(Area.Command.IntroducePlace command) {
        logger.info("EntityUd: {}\n_State: {}\n_Command: {}", entityId, currentState(), command);

        // validate command
        if (currentState().isEmpty()) {
            return effects().error("Area '%s' for organization '%s' does not exist.".formatted(command.areaId(), command.organizationId()));
        }
        if (isBlank(command.placeId())) {
            return effects().error("PlaceId is required");
        }
        if (currentState().places().containsKey(command.placeId())) {
            return effects().error("Place '%s' already exists".formatted(command.placeId()));
        }

        var event = currentState().onCommand(command);

        if (event.isEmpty()) {
            return effects().reply(Done.getInstance());
        } else {
            return effects()
                    .persist(event.get())
                    .thenReply(newState -> Done.getInstance());
        }
    }

    public Effect<Done> archivePlace(Area.Command.ArchivePlace command) {
        logger.info("EntityUd: {}\n_State: {}\n_Command: {}", entityId, currentState(), command);

        // validate command
        if (currentState().isEmpty()) {
            return effects().error("Area '%s' for organization '%s' does not exist.".formatted(command.areaId(), command.organizationId()));
        }
        if (isBlank(command.placeId())) {
            return effects().error("PlaceId is required");
        }
        if (!currentState().places().containsKey(command.placeId())) {
            return effects().error("Place '%s' does not exist".formatted(command.placeId()));
        }

        var event = currentState().onCommand(command);

        if (event.isEmpty()) {
            return effects().reply(Done.getInstance());
        } else {
            return effects()
                    .persist(event.get())
                    .thenReply(newState -> Done.getInstance());
        }
    }

    public ReadOnlyEffect<Area.State> get() {
        if (currentState().isEmpty()) {
            return effects().error("Area '%s' does not exist".formatted(entityId));
        }
        return effects().reply(currentState());
    }

    @Override
    public Area.State emptyState() {
        return Area.State.empty();
    }

    @Override
    public Area.State applyEvent(Area.Event event) {
        return switch (event) {
            case Area.Event.AreaIntroduced areaIntroduced -> currentState().onEvent(areaIntroduced);
            case Area.Event.PlaceArchived placeArchived -> currentState().onEvent(placeArchived);
            case Area.Event.PlaceIntroduced placeIntroduced -> currentState().onEvent(placeIntroduced);
            default -> currentState();
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
