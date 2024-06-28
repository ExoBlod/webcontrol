package com.webcontrol.android.di.angloamerican

import android.content.Context
import androidx.room.Room
import com.webcontrol.angloamerican.data.db.AppDatabase
import com.webcontrol.angloamerican.data.db.AppDatabase.Companion.DB_NAME
import com.webcontrol.angloamerican.data.db.dao.CheckListDao
import com.webcontrol.angloamerican.data.db.dao.ChecklistGroupsDao
import com.webcontrol.angloamerican.data.db.dao.PreaccesoDetalleMinaDao
import com.webcontrol.angloamerican.data.db.dao.PreaccesoMinaDao
import com.webcontrol.angloamerican.data.network.AuthInterceptor
import com.webcontrol.angloamerican.data.network.service.AngloamericanApiService
import com.webcontrol.angloamerican.data.repositories.*
import com.webcontrol.angloamerican.data.repositories.approvemovements.approveruser.AllMovementsRepository
import com.webcontrol.angloamerican.data.repositories.approvemovements.movementdetail.MovementDetailRepository
import com.webcontrol.angloamerican.domain.repository.approvemovements.approveruser.IAllMovementsRepository
import com.webcontrol.angloamerican.domain.repository.approvemovements.movementdetail.IMovementDetail
import com.webcontrol.angloamerican.domain.usecases.*
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.CheckListPreUsoApi
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.AnswersRepository
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.HistoryRepository
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.IAnswersRepository
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.IHistoryRepository
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.IInspectionRepository
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.IQuestionsRepository
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.ISignatureRepository
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.IVehicleRepository
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.InspectionRepository
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.QuestionsRepository
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.SignatureRepository
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.VehicleRepository
import com.webcontrol.core.utils.LocalStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AngloamericanModule {
    @Provides
    fun provideAuthInterceptor(localStorage: LocalStorage): AuthInterceptor {
        return AuthInterceptor(localStorage)
    }

    @Singleton
    @Provides
    fun provideAngloamericanApiService(@Named("angloamerican") retrofit: Retrofit): AngloamericanApiService {
        return retrofit.create(AngloamericanApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAngloAmericanApiPreUsoService(@Named("angloamerican") retrofit: Retrofit): CheckListPreUsoApi {
        return retrofit.create(CheckListPreUsoApi::class.java)
    }

    @Singleton
    @Provides
    fun provideHistoryRepository(apiService: CheckListPreUsoApi): IHistoryRepository {
        return HistoryRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideQuestionsRepository(apiService: CheckListPreUsoApi): IQuestionsRepository {
        return QuestionsRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideVehicleRepository(apiService: CheckListPreUsoApi): IVehicleRepository {
        return VehicleRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideAnswersRepository(apiService: CheckListPreUsoApi): IAnswersRepository {
        return AnswersRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideInspectionRepository(apiService: CheckListPreUsoApi): IInspectionRepository {
        return InspectionRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideSignatureRepository(apiService: CheckListPreUsoApi): ISignatureRepository {
        return SignatureRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideSecurityRepository(apiService: AngloamericanApiService): ISecurityRepository {
        return SecurityRepository(apiService)
    }

    @Provides
    fun provideGetTokenUseCase(repository: ISecurityRepository): GetTokenUseCase {
        return GetTokenUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideCredentialRepository(apiService: AngloamericanApiService): ICredentialRepository {
        return CredentialRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideCoursesRepository(apiService: AngloamericanApiService): ICoursesRepository {
        return CoursesRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideAllMovementsRepository(apiService: AngloamericanApiService): IAllMovementsRepository {
        return AllMovementsRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideMovementDetailRepository(apiService: AngloamericanApiService): IMovementDetail {
        return MovementDetailRepository(apiService)
    }

    @Provides
    fun provideGetCredentialUseCase(repository: ICredentialRepository): GetCredentialUseCase {
        return GetCredentialUseCase(repository)
    }

    @Provides
    fun provideGetCoursesUseCase(repository: ICoursesRepository): GetCoursesUseCase {
        return GetCoursesUseCase(repository)
    }

    @Provides
    fun provideSendInputCourseUseCase(repository: ICoursesRepository): SendInputCourseUseCase {
        return SendInputCourseUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideWorkerRepository(apiService: AngloamericanApiService): IWorkerRepository {
        return WorkerRepository(apiService)
    }

    @Provides
    fun provideGetWorkerUseCase(repository: IWorkerRepository): GetWorkerUseCase {
        return GetWorkerUseCase(repository)
    }

    @Singleton
    @Provides
    fun providePreaccessMineRepository(apiService: AngloamericanApiService, checkListDao: CheckListDao, preaccesoMinaDao: PreaccesoMinaDao, preaccesoDetalleMinaDao: PreaccesoDetalleMinaDao): IPreaccessMineRepository {
        return PreaccessMineRepository(apiService, checkListDao ,preaccesoMinaDao,preaccesoDetalleMinaDao)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            DB_NAME
        ).build()
    }

    @Provides
    fun providePreAccessMineDao(appDatabase: AppDatabase): PreaccesoMinaDao {
        return appDatabase.preAccesoMina()
    }

    @Provides
    fun providePreAccessDetailMineDao(appDatabase: AppDatabase): PreaccesoDetalleMinaDao {
        return appDatabase.preAccesodetalleMina()
    }

    @Provides
    fun provideCheckListDao(appDatabase: AppDatabase): CheckListDao {
        return appDatabase.checkListDao()
    }

    @Provides
    fun provideCheckListGroupsDao(appDatabase: AppDatabase): ChecklistGroupsDao {
        return appDatabase.checkListGroupsDao()
    }
}