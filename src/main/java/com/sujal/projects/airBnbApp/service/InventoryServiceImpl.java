package com.sujal.projects.airBnbApp.service;


import com.sujal.projects.airBnbApp.dto.*;
import com.sujal.projects.airBnbApp.entity.Hotel;
import com.sujal.projects.airBnbApp.entity.Inventory;
import com.sujal.projects.airBnbApp.entity.Room;
import com.sujal.projects.airBnbApp.entity.User;
import com.sujal.projects.airBnbApp.exception.ResourceNotFoundException;
import com.sujal.projects.airBnbApp.repository.HotelMinPriceRepository;
import com.sujal.projects.airBnbApp.repository.InventoryRepository;
import com.sujal.projects.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.sujal.projects.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for(; !today.isAfter(endDate);today=today.plusDays(1)){
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);
        }
    }


    //Delete inventory of future after todays date for a particular room
    @Override
    @Transactional
    public void deleteAllInventories(Room room) {
        log.info("Deleting inventories for room with id:{}",room.getId());
        inventoryRepository.deleteByRoom(room);

    }

    @Override
//    public Page<HotelDTO> searchHotels(HotelSearchRequestDTO hotelSearchRequestDTO) {
    public Page<HotelPriceDTO> searchHotels(HotelSearchRequestDTO hotelSearchRequestDTO) {
        log.info("Searching hotels for {} city from {} to {}",hotelSearchRequestDTO.getCity(),hotelSearchRequestDTO.getStartDate(),hotelSearchRequestDTO.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchRequestDTO.getPage(),hotelSearchRequestDTO.getSize());

        long dateCount = ChronoUnit.DAYS.between(hotelSearchRequestDTO.getStartDate(),hotelSearchRequestDTO.getEndDate())+1;

        //business logic -90 days
        Page<HotelPriceDTO> hotelsPage = hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequestDTO.getCity(),
                hotelSearchRequestDTO.getStartDate(),hotelSearchRequestDTO.getEndDate(), hotelSearchRequestDTO.getRoomsCount(),dateCount,pageable);

//        return hotelsPage.map(element -> modelMapper.map(element,HotelDTO.class));
//        return hotelsPage.map(element -> modelMapper.map(element,HotelPriceDTO.class));
        return hotelsPage;
    }

    @Override
    public List<InventoryDTO> getAllInventoryByRoom(Long roomId) {

        log.info("Getting all inventories of room for room with id: {}",roomId);
        Room room  = roomRepository.findById(roomId).orElseThrow(()-> new ResourceNotFoundException("Room not found with id: "+roomId));

        User user= getCurrentUser();

        if(!user.equals(room.getHotel().getOwner())){
            throw new AccessDeniedException("This user is not owner of this hotel with id: "+room.getHotel().getId());
        }

        List<Inventory> inventoryList = inventoryRepository.findByRoomOrderByDate(room);
        return inventoryList.stream()
                .map(inventory -> modelMapper.map(inventory, InventoryDTO.class))
                .collect(Collectors.toList());

    }

    @Transactional
    @Override
    public void updateInventory(Long roomId, UpdateInventoryRequestDTO updateInventoryRequestDTO) {
        log.info("Updating all inventory by room for room with id: {} between date range {}-{}",roomId,updateInventoryRequestDTO.getStartDate(),updateInventoryRequestDTO.getEndDate());

        Room room = roomRepository.findById(roomId).orElseThrow(()-> new ResourceNotFoundException("Room not found with id: "+roomId));

        User user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())){
            throw new AccessDeniedException("This user is not owner of this hotel with id: "+room.getHotel().getId());
        }
        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId,updateInventoryRequestDTO.getStartDate(),updateInventoryRequestDTO.getEndDate());

        inventoryRepository.updateInventory(roomId,updateInventoryRequestDTO.getStartDate(),updateInventoryRequestDTO.getEndDate(),
                updateInventoryRequestDTO.getSurgeFactor(),updateInventoryRequestDTO.getClosed());
    }


}
