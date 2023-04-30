package com.jtk.ps.api.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jtk.ps.api.model.EventStore;

@Repository
public interface EventStoreRepository extends JpaRepository<EventStore, Long>{
    
    @Query(value = "select distinct  YEAR(event_time) as tahun from event_store es" + 
    "where event_data_id = :eventDataId" +
    "and YEAR(event_time) <= :year" +
    "and es.event_type = :eventType" +
    "order by event_time desc " + 
    "limit 1", nativeQuery = true)
    Integer getLastYearOfUpdate(Integer eventDataId, String eventType, Integer year);

    @Query(value = "select * from event_store where event_data_id = :eventDataId and YEAR(event_time) <= :year and event_type = :eventType order by event_time desc limit 1", nativeQuery = true)
    EventStore getLastUpdateEvent(@Param("eventDataId") Integer eventDataId,@Param("eventType") String eventType,@Param("year") Integer year);
}
