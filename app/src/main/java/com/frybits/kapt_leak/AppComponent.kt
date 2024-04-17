package com.frybits.kapt_leak

import dagger.Component

@Component(
    modules = [
        ActivitiesModule::class
    ]
)
interface AppComponent {
    fun inject(myApplication: MyApplication)
}