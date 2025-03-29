package com.sujal.projects.airBnbApp.repository;

import com.sujal.projects.airBnbApp.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {


}
