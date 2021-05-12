package kz.aura.merp.employee.util

enum class CountryCode(val country: String, val phoneCode: String, val format: String) {
    KZ("Kazakhstan", "+7", "+7(###)###-##-##"),
    TR("Turkey", "+90", "+90 ### ### ####"),
    RU("Russia", "+7", "+7(###)###-##-##"),
    UZ("Uzbekistan", "+998", "+998 ## ######"),
    US("USA", "+1", "+1 ### ### ####"),
    KR("Kyrgyzstan", "+996", "+996 #### #####"),
    AE("United Arab Emirates", "+971", "+971 ## ### ####"),
    CN("China", "+86", "+86 ### #### ####"),
    AZ("Azerbaijan", "+994", "+994 ## ### ## ##"),
    TJ("Tajikistan", "+992", "+992 ## ### ####"),
    MY("Malaysia", "+60", "+60 #-### ####")
}