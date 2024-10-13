package com.example.lunchvote.area.domain;

import java.util.HashMap;
import java.util.Map;

public record AreaState(String organizationId, String areaId, Map<String, Place> places) {
    public record Place(String introducedBy) {
    }

    private static final String EMPTY_ID = "";

    public static AreaState empty() {
        return new AreaState(EMPTY_ID, EMPTY_ID, new HashMap<>());
    }

    // Event Handlers

    public AreaState onAreaIntroduced(AreaEvent.AreaIntroduced areaIntroduced) {
        return new AreaState(areaIntroduced.organizationId(), areaIntroduced.areaId(), new HashMap<>());
    }

    public AreaState onPlaceIntroduced(AreaEvent.PlaceIntroduced placeIntroduced) {
        var place = new Place(placeIntroduced.introducedBy());

        places.put(placeIntroduced.placeId(), place);

        return new AreaState(organizationId, areaId, places);
    }

    public AreaState onPlaceArchived(AreaEvent.PlaceArchived placeArchived) {
        places.remove(placeArchived.placeId());

        return new AreaState(organizationId, areaId, places);
    }

}
