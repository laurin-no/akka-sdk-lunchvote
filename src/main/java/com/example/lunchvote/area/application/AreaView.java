package com.example.lunchvote.area.application;

import akka.javasdk.annotations.ComponentId;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import com.example.lunchvote.area.domain.Area;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

@ComponentId("area-view")
public class AreaView extends View {
    @Query("""
            SELECT * FROM area_view WHERE organizationId = :organizationId
            """)
    public QueryStreamEffect<AreaRow> getAreasByOrganizationId(String organizationId) {
        return queryStreamResult();
    }

    public record AreaRow(String organizationId, String areaId) {

    }

    @Consume.FromEventSourcedEntity(AreaEntity.class)
    public static class AreaRowUpdater extends TableUpdater<AreaRow> {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        public Effect<AreaRow> onEvent(Area.Event event) {
            logger.info("RowId: {}\n_RowState: {}\n_Event: {}",
                    rowState() == null ? "N/A" : rowState().organizationId() + "-" + rowState().areaId,
                    rowState(),
                    event);

            return switch (event) {
                case Area.Event.AreaIntroduced areaIntroduced ->
                        effects().updateRow(new AreaRow(areaIntroduced.organizationId(), areaIntroduced.areaId()));
                case Area.Event.PlaceArchived placeArchived -> effects().ignore();
                case Area.Event.PlaceIntroduced placeIntroduced -> effects().ignore();
            };
        }
    }
}
