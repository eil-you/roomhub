package com.roomhub.model;

public enum ReservationStatus {
    PENDING, // 결제 대기
    CONFIRMED, // 예약 완료 (재고 감소 완료)
    CANCELLED, // 예약 취소 (재고 복구 필요)
    COMPLETED; // 이용 완료
}