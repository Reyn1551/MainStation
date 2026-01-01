# Panduan Setup Project Android Web-To-App

Halo! Jika kamu melihat error atau bingung cara menjalankan project ini, itu kemungkinan besar karena ada beberapa file konfigurasi rahasia yang tidak ikut ter-upload ke GitHub demi keamanan.

Ikuti langkah-langkah di bawah ini agar project bisa berjalan lancar di komputer kamu.

## 1. Prasyarat (Requirements)

Pastikan kamu sudah menginstal tools berikut:
*   **Android Studio** (Koala atau versi terbaru lebih disarankan)
*   **JDK 17** atau **JDK 1.8** (Biasanya sudah termasuk di Android Studio Gradle)

## 2. Setup Firebase (PENTING!)

Project ini menggunakan Firebase untuk Database. File konfigurasi `google-services.json` **TIDAK** ada di GitHub karena alasan keamanan. Kamu harus menambahkannya sendiri.

1.  Buka [Firebase Console](https://console.firebase.google.com/).
2.  Buat project baru atau minta akses ke pemilik repository jika ini project tim.
3.  Jika membuat baru:
    *   Tambahkan aplikasi Android.
    *   Masukkan Package Name: `com.mainstation.app` (Cek `app/build.gradle.kts` jika ragu).
4.  **Download file `google-services.json`**.
5.  **Copy/Paste file tersebut ke dalam folder `app/`** di project ini.
    *   Lokasi: `Web-To-App/android/app/google-services.json`

## 3. Setup Local Properties

File `local.properties` berisi lokasi Android SDK di komputer masing-masing, jadi ini juga tidak ada di GitHub.

1.  Buka project di Android Studio.
2.  Biasanya Android Studio akan otomatis membuatkan file ini saat kamu membuka project pertama kali atau saat proses Gradle Sync.
3.  Jika error "SDK location not found", buat file bernama `local.properties` di folder root (`Web-To-App/android/local.properties`) dan isi baris ini (sesuaikan path user kamu):

    **Contoh (Windows):**
    ```properties
    sdk.dir=C\:\\Users\\NamaUser\\AppData\\Local\\Android\\Sdk
    ```

    **Contoh (Mac/Linux):**
    ```properties
    sdk.dir=/Users/namauser/Library/Android/sdk
    ```

## 4. Cara Build & Run

1.  Buka Android Studio.
2.  Tunggu proses **Gradle Sync** selesai (lihat bar di bawah kanan).
3.  Jika Sync sukses, hubungkan HP Android kamu atau nyalakan Emulator.
4.  Klik tombol **Run (Play)** hijau di toolbar atas.

## Masalah Umum (Troubleshooting)

*   **Error "File google-services.json is missing"**:
    *   Ulangi langkah nomor 2. Pastikan filenya ada di dalam folder `app`, bukan di root folder luar.
*   **Error Database / Crash saat buka app**:
    *   Pastikan fitur **Firestore Database** dan **Authentication** sudah diaktifkan di Firebase Console kamu.
*   **Error API / Retrofit**:
    *   Jika aplikasi butuh koneksi ke server selain Firebase, pastikan URL-nya sudah benar di kodingan (biasanya di folder `di/` atau `data/remote`).

Selamat coding!
