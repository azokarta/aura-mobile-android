package kz.aura.merp.customer.views

import kz.aura.merp.customer.models.Product

interface IProductsView : BaseView {
    fun onSuccessProducts(products: ArrayList<Product>) {}
    fun onSuccessProduct(product: Product) {}
}