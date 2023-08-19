package com.example.myservices.model

data class Reservation(
    var documentId: String?, // حقل معرّف المستند
    val nameUser: String?,
    val serviceName: String?,
    val servicePrice: String?,
    val typeService: String?,
    val image: String?,
    val bookingDate: String?
) {
    // مُنشئ ثانوي بدون وسائط
    constructor() : this(null, null, null, null, null, null, null)
}
