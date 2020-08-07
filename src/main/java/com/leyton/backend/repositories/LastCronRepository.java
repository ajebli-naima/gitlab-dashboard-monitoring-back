package com.leyton.backend.repositories;

import com.leyton.backend.entities.LastCron;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LastCronRepository extends JpaRepository<LastCron, Long> {

}
