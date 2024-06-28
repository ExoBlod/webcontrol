package com.webcontrol.android.di

import com.webcontrol.android.angloamerican.ui.credentialLicencia.QuellavecoCredentialLicenciaViewModel
import com.webcontrol.android.angloamerican.ui.credentialVirtual.QuellavecoCredentialVirtualViewModel
import com.webcontrol.android.ui.MainViewModel
import com.webcontrol.android.ui.credential.credentialBambas.BambasCredentialViewModel
import com.webcontrol.android.ui.credential.credentialLaPoderosa.PoderosaCredentialViewModel
import com.webcontrol.android.ui.lectorqrPHC.CredentialPHCQrViewModel
import com.webcontrol.android.ui.preacceso.CabeceraViewModel
import com.webcontrol.android.ui.preacceso.ParkingViewModel
import com.webcontrol.android.ui.preacceso.PassengerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { CabeceraViewModel(get(), get(), get(), get()) }
    viewModel { PassengerViewModel(get(), get(), get(), get()) }
    viewModel { MainViewModel(get(), get(), get(), get()) }
    viewModel { ParkingViewModel(get()) }
    viewModel { QuellavecoCredentialLicenciaViewModel(get()) }
    viewModel { BambasCredentialViewModel(get()) }
    viewModel { PoderosaCredentialViewModel(get()) }
    viewModel { CredentialPHCQrViewModel(get()) }
    viewModel { QuellavecoCredentialVirtualViewModel(get()) }
}