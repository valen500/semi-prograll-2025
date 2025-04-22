plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // 🔥 Necesario para Firebase
}

android {
    namespace = "com.example.miprimeraaplicacion"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.miprimeraaplicacion"
        minSdk = 22
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Librerías base
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase BoM para manejar versiones automáticamente
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))

    // Firebase: solo agregas lo que usas
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")

    // Para compatibilidad con versiones más antiguas

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
