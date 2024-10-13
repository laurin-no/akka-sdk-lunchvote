package com.example.lunchvote.area.domain;

import kalix.javasdk.annotations.TypeName;

public sealed interface AreaEvent {

    @TypeName("area-introduced")
    record AreaIntroduced(String organizationId, String areaId) implements AreaEvent {
    }

    @TypeName("place-introduced")
    record PlaceIntroduced(String organizationId, String areaId, String placeId,
                           String introducedBy) implements AreaEvent {
    }

    @TypeName("place-archived")
    record PlaceArchived(String organizationId, String areaId, String placeId) implements AreaEvent {
    }
}
