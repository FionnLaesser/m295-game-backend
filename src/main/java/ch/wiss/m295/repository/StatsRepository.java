package ch.wiss.m295.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.wiss.m295.entity.Stats;
// Repository für Stats-History (INSERT-only, Read-only Abfragen).
public interface StatsRepository extends JpaRepository<Stats, Integer> {
    List<Stats> findByUserIdOrderBySavedAtDesc(Integer userId);
}
