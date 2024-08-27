import com.nuzchpt.scrumpoker.AndroidPlatform
import com.nuzchpt.scrumpoker.createDataStoreAndroid
import com.nuzchpt.scrumpoker.data.local.Platform
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<Platform> { AndroidPlatform(androidContext()) }
    single { createDataStoreAndroid(androidContext()) }
}