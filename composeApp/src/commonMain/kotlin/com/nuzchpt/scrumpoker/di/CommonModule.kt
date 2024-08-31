package di

import FireStoreService
import FireStoreServiceImpl
import RoomRepository
import RoomRepositoryImpl
import com.nuzchpt.scrumpoker.data.local.LocalStorageDatasource
import com.nuzchpt.scrumpoker.data.local.LocalStorageDatasourceImpl
import com.nuzchpt.scrumpoker.domain.repository.UserRepository
import com.nuzchpt.scrumpoker.domain.repository.UserRepositoryImpl
import com.nuzchpt.scrumpoker.domain.usecase.ClearRoomParticipatesPointUseCase
import com.nuzchpt.scrumpoker.domain.usecase.CreateRoomUseCase
import com.nuzchpt.scrumpoker.domain.usecase.GetParticipantsUseCase
import com.nuzchpt.scrumpoker.domain.usecase.GetRoomDetailUseCase
import com.nuzchpt.scrumpoker.domain.usecase.GetUserInfoUseCase
import com.nuzchpt.scrumpoker.domain.usecase.JoinRoomUseCase
import com.nuzchpt.scrumpoker.domain.usecase.LeaveRoomUseCase
import com.nuzchpt.scrumpoker.domain.usecase.SaveUserInfoUseCase
import com.nuzchpt.scrumpoker.domain.usecase.SetPointVotingUseCase
import com.nuzchpt.scrumpoker.domain.usecase.SetVotingStateUseCase
import com.nuzchpt.scrumpoker.ui.main.viewmodel.MainViewModel
import com.nuzchpt.scrumpoker.ui.main.viewmodel.MainViewModelImpl
import com.nuzchpt.scrumpoker.ui.room.viewmodel.RoomViewModel
import com.nuzchpt.scrumpoker.ui.room.viewmodel.RoomViewModelImpl
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import platformModule

val commonModule = module {
    single { Firebase.firestore }
    single<LocalStorageDatasource> { LocalStorageDatasourceImpl(get(), get()) }
    single<FireStoreService> { FireStoreServiceImpl(get()) }
    single<RoomRepository> { RoomRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    factory { GetRoomDetailUseCase(get()) }
    factory { JoinRoomUseCase(get()) }
    factory { GetParticipantsUseCase(get()) }
    factory { SaveUserInfoUseCase(get()) }
    factory { GetUserInfoUseCase(get()) }
    factory { SetVotingStateUseCase(get()) }
    factory { ClearRoomParticipatesPointUseCase(get()) }
    factory { SetPointVotingUseCase(get()) }
    factory { LeaveRoomUseCase(get()) }
    factory { CreateRoomUseCase(get()) }
    viewModel<MainViewModel> { MainViewModelImpl(get(), get(), get(), get(), get()) }
    viewModel<RoomViewModel> { RoomViewModelImpl(get(), get(), get(), get(), get(), get(), get()) }
}

fun initializeKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(platformModule, commonModule)
    }
}