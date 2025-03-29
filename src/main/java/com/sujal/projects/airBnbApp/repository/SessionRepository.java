package com.sujal.projects.airBnbApp.repository;


import com.sujal.projects.airBnbApp.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity,Long> {
}
