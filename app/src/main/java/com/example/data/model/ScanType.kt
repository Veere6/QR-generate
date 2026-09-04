package com.example.data.model

enum class ScanType {
    QR_CODE,
    BARCODE
}

enum class BarcodeFormatType {
    EAN_13,
    EAN_8,
    UPC_A,
    UPC_E,
    CODE_39,
    CODE_128,
    ITF,
    PDF417,
    AZTEC,
    DATA_MATRIX,
    QR_CODE,
    UNKNOWN
}

enum class ParsedCategory {
    URL,
    WIFI,
    PHONE,
    SMS,
    EMAIL,
    CONTACT,
    LOCATION,
    UPI,
    CALENDAR,
    CRYPTO,
    PRODUCT,
    TEXT
}
