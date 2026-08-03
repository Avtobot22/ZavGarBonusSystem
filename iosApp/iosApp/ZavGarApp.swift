import SwiftUI
import shared
import FirebaseCore

@main
struct ZavGarApp: App {

    init() {
        FirebaseApp.configure()
        // useMockServer: true -> приложение работает на заготовленных ответах без реального
        // бэкенда (offline-режим, симметрично Android-флагу USE_MOCK_SERVER).
        KoinHelper_iosKt.doInitKoinIos(useMockServer: false)
    }

    var body: some Scene {
        WindowGroup {
            ContentView().edgesIgnoringSafeArea(.all)
        }
    }
}
