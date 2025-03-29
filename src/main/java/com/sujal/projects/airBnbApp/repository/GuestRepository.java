package com.sujal.projects.airBnbApp.repository;

import com.sujal.projects.airBnbApp.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guest,Long> {
}
