package com.nova.repo;

import com.nova.models.Bonuses;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepoBonuses extends JpaRepository<Bonuses, Long> {
    List<Bonuses> findAllByUserId(Long userId);


}
