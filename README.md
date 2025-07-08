# RatProject: Eğitim Amaçlı Android Yönetim Aracı

![Kontrol Paneli](https://i.imgur.com/your-screenshot-url.png) <!-- Projenizin bir ekran görüntüsünü buraya ekleyebilirsiniz -->

Bu proje, Python (Flask) tabanlı bir sunucu ve yerel bir Android istemcisinden oluşan, **tamamen eğitim amaçlı** bir Uzaktan Yönetim Aracıdır (RAT). Proje, ağ programlama, Android servisleri, web soketleri ve istemci-sunucu mimarisi gibi konuları öğrenmek için pratik bir ortam sunar.

**Türkçe** geliştirilmiş modern ve kullanıcı dostu bir web arayüzü üzerinden bağlı cihazları yönetebilir ve komutlar gönderebilirsiniz.

---

## ⚠️ Önemli Uyarı ve Yasal Sorumluluk Reddi

Bu yazılım **sadece eğitim ve araştırma amaçlıdır**.

- **Kesinlikle** yasa dışı, kötü niyetli veya izniniz olmayan sistemler üzerinde kullanmayınız.
- Bu aracın kullanımıyla ilgili tüm yerel, eyalet ve federal yasalara uymak kullanıcının sorumluluğundadır.
- Geliştirici, bu projenin yanlış veya yasa dışı kullanımından kaynaklanan hiçbir hasar veya sonuçtan sorumlu tutulamaz.
- **Sadece kendi kontrolünüzdeki cihazlarda ve ağlarda test yapın.**

---

## ✨ Temel Özellikler

- **Modern Web Kontrol Paneli**: Bağlı cihazları yönetmek için duyarlı ve gerçek zamanlı web arayüzü.
- **Gerçek Zamanlı İletişim**: Sunucu, web paneli ve Android istemcileri arasında düşük gecikmeli iletişim için **Flask-SocketIO**.
- **Cihaz Yönetimi**: Bağlı tüm istemcileri listeleme ve hedef cihaz seçme.
- **Dosya Sistemi Erişimi**: Cihazdaki dosya ve klasörleri listeleme ve önemli dosyaları sunucuya indirme.
- **Ekran Görüntüsü**: Cihazın ekranının anlık görüntüsünü alıp panelde görüntüleme.
- **GPS Konum Takibi**: Cihazın anlık konumunu (enlem, boylam) alıp interaktif bir harita üzerinde gösterme.
- **Uzak Kamera**: Cihazın kamerasını kullanarak fotoğraf çekme ve panelde görüntüleme.
- **Galeri Erişimi**: Cihazın galerisindeki son fotoğrafları çekip panelde gösterme.
- **Arka Plan Çalışması**: Android istemcisi, uygulama kapalıyken bile komutları alabilmek için bir **Foreground Service** olarak çalışır.

---

## 🛠️ Teknoloji Mimarisi

- **Sunucu (Backend)**:
   - **Framework**: Flask
   - **Gerçek Zamanlı İletişim**: Flask-SocketIO, Eventlet
   - **Dil**: Python 3
- **Kontrol Paneli (Frontend)**:
   - **Yapı**: HTML, CSS, JavaScript
   - **Gerçek Zamanlı İletişim**: Socket.IO Client
   - **API İletişimi**: Fetch API
- **İstemci (Client)**:
   - **Platform**: Android (Min SDK 24 - Android 7.0)
   - **Dil**: Java/Kotlin
   - **Gerçek Zamanlı İletişim**: Socket.IO Client for Java
   - **Arka Plan İşlemleri**: Android Services
   - **Konum**: Google Play Services

---

## 🚀 Kurulum ve Başlatma

### Ön Gereksinimler
- [Python 3.8+](https://www.python.org/downloads/)
- [Android Studio](https://developer.android.com/studio) (En son sürüm önerilir)
- Fiziksel bir Android cihaz veya Android Emulator

### 1. Sunucuyu Ayarlama

Sunucu, bağlı istemcileri dinler ve web kontrol panelini sunar.

```bash
# 1. Proje deposunu klonlayın
git clone https://github.com/your-username/RatProject.git
cd RatProject/server

# 2. Kurulum betiğini çalıştırın (Önerilen)
# Bu betik, sanal ortamı kurar, bağımlılıkları yükler ve sunucuyu başlatır.
chmod +x start_server.sh
./start_server.sh

# --- VEYA Manuel Kurulum ---
# Sanal bir ortam oluşturun
python3 -m venv rat_env

# Ortamı etkinleştirin
# Linux/macOS:
source rat_env/bin/activate
# Windows:
# .\rat_env\Scripts\activate

# Gerekli Python paketlerini yükleyin
pip install -r requirements.txt

# Sunucuyu başlatın
python app.py
```
Sunucu başarıyla başladığında, `http://0.0.0.0:8080` adresinde çalışacaktır.

### 2. Android İstemcisini Ayarlama

Android uygulaması, sunucuya bağlanacak ve komutları yerine getirecektir.

1.  **Projeyi Android Studio'da Açın**:
    `RatProject` klasörünü Android Studio ile açın. Gradle senkronizasyonunun tamamlanmasını bekleyin.

2.  **Sunucu IP Adresini Yapılandırın**:
    Uygulamanın sunucunuza bağlanabilmesi için sunucunun yerel IP adresini bilmeniz gerekir.
   - **Linux/macOS**: Terminale `ifconfig` veya `ip a` yazın.
   - **Windows**: Komut istemine `ipconfig` yazın.

    IP adresinizi öğrendikten sonra, Android projesinde `app/src/main/java/com/example/ratproject/services/RatService.java` (veya benzer bir dosya) içindeki `SERVER_URL` değişkenini güncelleyin.

    ```java
    // Örnek: app/src/main/java/com/example/ratproject/services/RatService.java

    // private static final String SERVER_URL = "http://192.168.1.10:8080"; // Bu satırı bulun
    private static final String SERVER_URL = "http://BURAYA_SUNUCU_IP_ADRESINIZI_YAZIN:8080";
    ```

3.  **Uygulamayı Derleyin ve Yükleyin**:
   - Android cihazınızı USB ile bilgisayarınıza bağlayın ve **USB Hata Ayıklama (USB Debugging)** modunu etkinleştirin.
   - Android Studio'da `Run > Run 'app'` menüsünü kullanarak uygulamayı cihazınıza yükleyin.

---

## 💻 Kullanım

1.  **Sunucunun çalıştığından emin olun.**
2.  **Android uygulamayı hedef cihaza yükleyip çalıştırın.** Uygulama başlatıldığında arka plan servisi de başlayacak ve sunucuya bağlanacaktır.
3.  Bilgisayarınızdaki bir web tarayıcıdan şu adresi açın:
    ```
    http://<SUNUCUNUZUN_IP_ADRESI>:8080
    ```
    veya sunucu aynı makinede çalışıyorsa:
    ```
    http://localhost:8080
    ```
4.  **Kontrol Panelini Kullanma**:
   - **Cihaz Seçimi**: "Hedef Cihaz" menüsünden komut göndermek istediğiniz cihazı seçin.
   - **Komut Gönderme**: İlgili butona (Ekran Görüntüsü, Konum vb.) tıklayarak komutu gönderin.
   - **Sonuçları Görüntüleme**: Komut sonuçları "Komut Sonuçları & Detaylar" bölümünde gerçek zamanlı olarak görünecektir. Sonuçların üzerine tıklayarak detayları (resimler, haritalar, dosya listeleri) görebilirsiniz.

---

## Android İzinleri ve Nedenleri

Uygulama, işlevlerini yerine getirebilmek için tehlikeli olarak kabul edilen bazı izinler ister.

- `INTERNET`: Sunucu ile iletişim kurmak için.
- `ACCESS_FINE_LOCATION`, `ACCESS_BACKGROUND_LOCATION`: Cihazın konumunu (arka planda bile) almak için.
- `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `MANAGE_EXTERNAL_STORAGE`: Dosya sistemini listelemek ve dosya indirmek için.
- `CAMERA`: Uzaktan fotoğraf çekmek için.
- `FOREGROUND_SERVICE`: Uygulama kapalıyken bile sunucu bağlantısını aktif tutmak için.

---

## 📜 Lisans

Bu proje MIT Lisansı altında lisanslanmıştır. Detaylar için `LICENSE` dosyasına bakınız.
