package com.example.demo.Model

import com.google.gson.annotations.SerializedName

class ModelPage {
    @SerializedName("page")
    var page: Int? = null
    @SerializedName("per_page")
    var perPage: Int? = null
    @SerializedName("total")
    var total: Int? = null
    @SerializedName("total_pages")
    var totalPages: Int? = null
    @SerializedName("data")
    var data: List<Data>? = null
    @SerializedName("ad")
    var ad: Ad? = null
}

class Data {

    @SerializedName("id")
    var id: Int? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("first_name")
    var firstName: String? = null
    @SerializedName("last_name")
    var lastName: String? = null
    @SerializedName("avatar")
    var avatar: String? = null
}

class Ad {

    @SerializedName("company")
    var company: String? = null
    @SerializedName("url")
    var url: String? = null
    @SerializedName("text")
    var text: String? = null
}