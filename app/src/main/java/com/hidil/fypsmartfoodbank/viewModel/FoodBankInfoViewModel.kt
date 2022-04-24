package com.hidil.fypsmartfoodbank.viewModel

import androidx.lifecycle.ViewModel

class FoodBankInfoViewModel : ViewModel() {

    var itemAmount: ArrayList<Int> = ArrayList()

    init {
        itemAmount = ArrayList()
    }

    fun getAmount(amount: ArrayList<Int>) {
        itemAmount = amount
    }

    fun changeItemAmount(position: Int, isAdd: Boolean) {
        if (isAdd) {
            itemAmount[position]++
        } else {
            itemAmount[position]--
        }
    }



}