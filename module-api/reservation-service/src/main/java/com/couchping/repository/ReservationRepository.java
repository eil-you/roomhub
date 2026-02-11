package com.couchping.repository;

import com.couchping.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // ?뱀젙 ?ъ슜?먯쓽 ?덉빟 ?댁뿭 議고쉶
    List<Reservation> findAllByUserId(Long userId);

    // ?뱀젙 諛⑹쓽 ?덉빟 ?댁뿭 議고쉶
    List<Reservation> findAllByRoomId(Long roomId);
}
