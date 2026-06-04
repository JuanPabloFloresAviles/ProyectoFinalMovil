package com.example.proyectofinalmovil.services.tickets

import com.example.proyectofinalmovil.services.mock.MockPurchase

object TicketQrPayload {
    fun fromPurchase(purchase: MockPurchase): String {
        return purchase.qrCode.ifBlank { purchase.folio }
    }
}
