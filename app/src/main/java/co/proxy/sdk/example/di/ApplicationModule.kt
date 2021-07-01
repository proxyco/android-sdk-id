package co.proxy.sdk.example.di

import android.content.Context
import co.proxy.sdk.example.data.configuration.ConfigurationRepository
import co.proxy.sdk.example.data.configuration.ConfigurationRepositoryImp
import co.proxy.sdk.example.data.events.EventsRepository
import co.proxy.sdk.example.data.events.EventsRepositoryImp
import co.proxy.sdk.example.data.reader.ReaderRepository
import co.proxy.sdk.example.data.reader.ReaderRepositoryImp
import co.proxy.sdk.example.data.user.UserRepository
import co.proxy.sdk.example.data.user.UserRepositoryImp
import co.proxy.sdk.example.datasource.sdk.ProxyBleServiceBinder
import co.proxy.sdk.example.datasource.sdk.ProxySDKDatasource
import co.proxy.sdk.example.datasource.sdk.ProxySDKDatasourceImp
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [ApplicationModule.BindsModule::class])
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Module
    @InstallIn(SingletonComponent::class)
    internal interface BindsModule {
        @Binds
        fun bindUserRepository(userRepository: UserRepositoryImp): UserRepository

        @Binds
        fun bindReaderRepository(readerRepository: ReaderRepositoryImp): ReaderRepository

        @Binds
        fun bindConfigurationRepository(configurationRepository: ConfigurationRepositoryImp): ConfigurationRepository

        @Binds
        fun bindEventsRepository(eventsRepository: EventsRepositoryImp): EventsRepository
    }

    @Provides
    @Singleton
    internal fun provideProxySDKDatasourceImp(@ApplicationContext context: Context): ProxySDKDatasourceImp {
        return ProxySDKDatasourceImp(context)
    }

    @Provides
    @Singleton
    internal fun provideProxySDKWrapper(proxySDKDatasource: ProxySDKDatasourceImp): ProxySDKDatasource {
        return proxySDKDatasource
    }

    @Provides
    @Singleton
    internal fun provideBleServiceBinder(proxySDKDatasource: ProxySDKDatasourceImp): ProxyBleServiceBinder {
        return proxySDKDatasource
    }

}