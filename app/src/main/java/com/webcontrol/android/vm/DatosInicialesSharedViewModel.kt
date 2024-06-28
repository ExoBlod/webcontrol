package com.webcontrol.android.vm

import androidx.databinding.adapters.AutoCompleteTextViewBindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webcontrol.android.data.db.repositories.RepositoryCheckList
import com.webcontrol.android.data.model.Antecedentes
import com.webcontrol.android.data.model.DatosInicialesWorker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class DatosInicialesSharedViewModel : ViewModel() {
    val antecedentes : MutableLiveData<List<Antecedentes>> = MutableLiveData()
    val user: MutableLiveData<DatosInicialesWorker> = MutableLiveData()
    val isValid : MutableLiveData<Boolean> = MutableLiveData()
    val customer: MutableLiveData<String> = MutableLiveData()

    fun getUser(): LiveData<DatosInicialesWorker> {
        return user
    }

    fun setUser(data: DatosInicialesWorker) {
        user.value = data
    }

    fun getAntecedentes(): LiveData<List<Antecedentes>> {
        return antecedentes
    }

    fun setAntecedentes(listAntecedentes: List<Antecedentes>){
        antecedentes.value = listAntecedentes
    }

    fun getIsValid(): LiveData<Boolean>{
        return isValid
    }

    fun setIsValid(isValid: Boolean){
        this.isValid.value = isValid
    }

    fun getCustomer(): LiveData<String>{
        return customer
    }

    fun setCustomer(customer: String){
        this.customer.value = customer
    }
}