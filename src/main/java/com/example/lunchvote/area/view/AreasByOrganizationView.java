package com.example.lunchvote.area.view;

import com.example.lunchvote.area.AreaEntity;
import com.example.lunchvote.area.api.AreasResponse;
import com.example.lunchvote.area.domain.AreaEvent;
import kalix.javasdk.annotations.Query;
import kalix.javasdk.annotations.Subscribe;
import kalix.javasdk.annotations.Table;
import kalix.javasdk.annotations.ViewId;
import kalix.javasdk.view.View;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

@ViewId("view_areas_by_organization")
@Table("areas_by_organization")
@Subscribe.EventSourcedEntity(value = AreaEntity.class, ignoreUnknown = true)
public class AreasByOrganizationView extends View<AreaViewModel> {

    @GetMapping("/areas/{organization_id}")
    @Query("SELECT areaId as areas FROM areas_by_organization WHERE organizationId = :organization_id")
    public Flux<AreasResponse> getAreas(String organizationId) {
        return null;
    }

    public UpdateEffect<AreaViewModel> onEvent(AreaEvent.AreaIntroduced introduced) {
        return effects().updateState(new AreaViewModel(introduced.organizationId(), introduced.areaId()));
    }
}
