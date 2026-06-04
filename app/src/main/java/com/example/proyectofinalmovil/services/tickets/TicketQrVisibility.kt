package com.example.proyectofinalmovil.services.tickets

import com.example.proyectofinalmovil.services.mock.MockPurchase

object TicketQrVisibility {
    fun isVisible(purchase: MockPurchase, nowMillis: Long): Boolean {
        val payload = TicketQrPayload.fromPurchase(purchase)
        val expiresAt = purchase.qrExpiresAtMillis
        return payload.isNotBlank() &&
            purchase.status.equals("Activa", ignoreCase = true) &&
            (expiresAt == null || nowMillis <= expiresAt)
    }
}
