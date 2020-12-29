package kz.aura.merp.customer.data.model


data class Feedback (
    val id: Long,
    val image: String,
    val feedbackText: String,
    val feedbackDate: String,
    val parentId: Long? = null,
    val customerFio: String,
    val staffFio: String
)
