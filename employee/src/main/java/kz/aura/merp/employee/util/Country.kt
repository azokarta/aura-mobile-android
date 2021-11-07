package kz.aura.merp.employee.util

enum class Country(val countryName: String, val phoneCode: String, val format: String) {
    KZ("Kazakhstan", "+7", "+7 (___) ___ __-__"),
    TR("Turkey", "+90", "+90 ___ ___ ____"),
    RU("Russia", "+7", "+7 (___) ___ __-__"),
    UZ("Uzbekistan", "+998", "+998 __ _______"),
    US("USA", "+1", "+1 ___ ___ ____"),
    KR("Kyrgyzstan", "+996", "+996 ____ _____"),
    AE("United Arab Emirates", "+971", "+971 __ ___ ____"),
    CN("China", "+86", "+86 ___ ____ ____"),
    AZ("Azerbaijan", "+994", "+994 __ ___ __ __"),
    TJ("Tajikistan", "+992", "+992 __ ___ ____"),
    MY("Malaysia", "+60", "+60 _-___ ____")
}