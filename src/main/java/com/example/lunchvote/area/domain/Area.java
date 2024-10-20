package com.example.lunchvote.area.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface Area {

    record State(String organizationId, String areaId,
                        Map<String, Place> places) {

        public record Place(String introducedBy) {}

        // command handlers
        // TODO validation could be moved here

        public Optional<Event> onCommand(Command.IntroduceArea command) {
            if (this.isEmpty()) {
                return Optional.of(new Event.AreaIntroduced(command.organizationId, command.areaId));
            } else {
                return Optional.empty();
            }
        }

        public Optional<Event> onCommand(Command.IntroducePlace command) {
            if (places.containsKey(command.placeId)) {
                return Optional.empty();
            } else {
                return Optional.of(new Event.PlaceIntroduced(organizationId, areaId, command.placeId, command.userId));
            }
        }

        public Optional<Event> onCommand(Command.ArchivePlace command) {
            if (!places.containsKey(command.placeId)) {
                return Optional.empty();
            }
            else {
                return Optional.of(new Event.PlaceArchived(organizationId, areaId, command.placeId));
            }
        }


        // event handlers

        public State onEvent(Event.AreaIntroduced event) {
            return new State(event.organizationId, event.areaId, new HashMap<>());
        }

        public State onEvent(Event.PlaceIntroduced event) {
            var place = new Place(event.introducedBy);

            places.put(event.placeId, place);

            return new State(organizationId, areaId, places);
        }

        public State onEvent(Event.PlaceArchived event) {
            places.remove(event.placeId);

            return new State(organizationId, areaId, places);
        }

        // utils

        public static State empty() {
            return new State(null, null, null);
        }

        public boolean isEmpty() {
            return this.equals(empty());
        }
    }


    sealed interface Command {

        record IntroduceArea(
                String organizationId,
                String areaId
        ) implements Command {
        }

        record IntroducePlace(
                String organizationId,
                String areaId,
                String placeId,
                String userId
        ) implements Command {
        }

        record ArchivePlace(
                String organizationId,
                String areaId,
                String placeId
        ) implements Command {
        }
    }

    sealed interface Event {

        record AreaIntroduced(
                String organizationId,
                String areaId
        ) implements Event {
        }

        record PlaceIntroduced(
                String organizationId,
                String areaId,
                String placeId,
                String introducedBy
        ) implements Event {
        }

        record PlaceArchived(String organizationId,
                             String areaId,
                             String placeId
        ) implements Event {
        }
    }

}
