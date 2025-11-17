package dev.starryeye.point.application;

import dev.starryeye.point.application.command.PointReserveCommand;
import dev.starryeye.point.application.command.ReservedPointCancelCommand;
import dev.starryeye.point.application.command.ReservedPointConfirmCommand;
import dev.starryeye.point.domain.Point;
import dev.starryeye.point.domain.PointReservation;
import dev.starryeye.point.infrastructure.PointRepository;
import dev.starryeye.point.infrastructure.PointReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointReservationRepository pointReservationRepository;

    @Transactional
    public void tryReserve(PointReserveCommand command) {

        if (pointReservationRepository.existsByReservationId(command.reservationId())) {
            System.out.println("reservation already exists, reservationId: " + command.reservationId());
            return;
        }

        Point point = pointRepository.findByUserId(command.userId())
                .orElseThrow(() -> new RuntimeException("point not found, userId: " + command.userId()));

        point.reserveBalance(command.reserveBalance());
        PointReservation pointReservation = PointReservation.create(
                command.reservationId(),
                point.getId(),
                point.getUserId(),
                command.reserveBalance()
        );
        pointReservationRepository.save(pointReservation);
    }

    @Transactional
    public void confirmReserve(ReservedPointConfirmCommand command) {

        PointReservation reservation = pointReservationRepository.findByReservationId(command.reservationId())
                .orElseThrow(() -> new RuntimeException("reservation not found, reservationId: " + command.reservationId()));

        if (reservation.isNotReserved()) {
            System.out.println("there are cancelled or confirmed reservations, reservationId: " + command.reservationId());
            return;
        }

        Point point = pointRepository.findById(reservation.getPointId())
                .orElseThrow(() -> new RuntimeException("point not found, pointId: " + reservation.getPointId()));

        point.confirmReservedBalance(reservation.getReservedBalance());
        reservation.confirm();
    }

    @Transactional
    public void cancelReserve(ReservedPointCancelCommand command) {

        PointReservation reservation = pointReservationRepository.findByReservationId(command.reservationId())
                .orElseThrow(() -> new RuntimeException("reservation not found, reservationId: " + command.reservationId()));

        if (reservation.isNotReserved()) {
            System.out.println("there are cancelled or confirmed reservations, reservationId: " + command.reservationId());
            return;
        }

        Point point = pointRepository.findById(reservation.getPointId())
                .orElseThrow(() -> new RuntimeException("point not found, pointId: " + reservation.getPointId()));

        point.cancelReservedBalance(reservation.getReservedBalance());
        reservation.cancel();
    }
}
