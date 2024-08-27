import SwiftUI
import Firebase
import ComposeApp

@main
struct iOSApp: App {
    init() {
        CommonModuleKt.initializeKoin()
        FirebaseApp.configure()
      }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
