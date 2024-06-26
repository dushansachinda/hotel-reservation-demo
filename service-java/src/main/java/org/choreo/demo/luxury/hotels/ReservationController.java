package org.choreo.demo.luxury.hotels;

import org.choreo.demo.luxury.hotels.dto.ReservationDto;
import org.choreo.demo.luxury.hotels.dto.ReservationRequest;
import org.choreo.demo.luxury.hotels.dto.UpdateReservationRequest;
import org.choreo.demo.luxury.hotels.model.Reservation;
import org.choreo.demo.luxury.hotels.model.RoomType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/roomTypes")
    public ResponseEntity<List<RoomType>> getRoomTypes(@RequestParam(defaultValue = "2024-02-15T15:00:38.122Z") String checkinDate,
                                                           @RequestParam(defaultValue = "2024-02-16T15:00:38.122Z") String checkoutDate,
                                                       @RequestParam(defaultValue = "1") int guestCapacity) {
        // Implement logic to fetch room types
        try {
            List<RoomType> roomTypes = reservationService.getAvailableRoomTypes(checkinDate, checkoutDate, guestCapacity);
            return ResponseEntity.ok(roomTypes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/")
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationRequest reservationRequest) {
        // Implement logic to create a reservation
        if (!reservationService.isAvailable(reservationRequest.getCheckinDate(), reservationRequest.getCheckoutDate(), reservationRequest.getRoomType())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Reservation reservation = reservationService.save(reservationRequest);
        ReservationDto dto = ReservationDto.from(reservation);

        logger.info("reservation created " + dto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping("/{reservation_id}")
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable("reservation_id") Long reservationId,
                                                         @RequestBody UpdateReservationRequest updateReservationRequest) {
        Reservation reservation = reservationService.update(reservationId, updateReservationRequest);
        ReservationDto dto = ReservationDto.from(reservation);
        logger.info("updated the reservation: " + dto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/{reservation_id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("reservation_id") Long reservationId) {
        reservationService.delete(reservationId);
        logger.info("deleted the reservation: " + reservationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ReservationDto>> getUserReservations(@PathVariable("userId") String userId) {
        List<Reservation> reservations = reservationService.findByUserId(userId);
        List<ReservationDto> list = reservations.stream().map(ReservationDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
