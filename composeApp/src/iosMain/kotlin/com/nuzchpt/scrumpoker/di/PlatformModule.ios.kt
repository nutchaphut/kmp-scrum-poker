import com.nuzchpt.scrumpoker.IOSPlatform
import com.nuzchpt.scrumpoker.createDataStoreIOS
import com.nuzchpt.scrumpoker.data.local.Platform
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<Platform> { IOSPlatform() }
    single { createDataStoreIOS() }
}
