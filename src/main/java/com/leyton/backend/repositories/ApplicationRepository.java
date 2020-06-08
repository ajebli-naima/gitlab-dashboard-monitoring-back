package com.leyton.backend.repositories;

import com.leyton.backend.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application,Long> {

}
