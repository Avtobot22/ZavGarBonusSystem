import SwiftUI
import shared
import FirebaseCore

@main
struct ZavGarApp: App {

    init() {
        FirebaseApp.configure()
        KoinHelperKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView().edgesIgnoringSafeArea(.all)
        }
    }
}
