# RAT Project - Educational Purpose

Bu proje eğitim amaçlı bir RAT (Remote Access Tool) uygulamasıdır. Python sunucu ve Android client'dan oluşmaktadır.

## Özellikler

- **Dosya İşlemleri**: Dosya listeleme ve indirme
- **Ekran Görüntüsü**: Uzaktan ekran görüntüsü alma
- **Konum Takibi**: GPS konum bilgisi alma
- **WebSocket Bağlantısı**: Gerçek zamanlı iletişim

## Kurulum

### Python Sunucu

1. `server` dizinine gidin:
   ```bash
   cd server
   ```

2. Bağımlılıkları yükleyin:
   ```bash
   pip install -r requirements.txt
   ```

3. Sunucuyu başlatın:
   ```bash
   python app.py
   ```
   veya
   ```bash
   ./start_server.sh
   ```

### Android Uygulama

1. Android Studio'da projeyi açın
2. `MainActivity.java` dosyasında `SERVER_URL` değişkenini kendi IP adresinize göre ayarlayın
3. Uygulamayı derleyin ve cihaza yükleyin

## Kullanım

### Sunucu API Endpoints

- `GET /`: Sunucu durumu
- `GET /clients`: Bağlı client'lar
- `POST /send_command`: Client'a komut gönderme

### Komut Örnekleri

#### Dosya Listeleme
```bash
curl -X POST http://localhost:8080/send_command \
  -H "Content-Type: application/json" \
  -d '{"client_id": "CLIENT_ID", "command": "list_files", "path": "/storage/emulated/0/"}'
```

#### Dosya İndirme
```bash
curl -X POST http://localhost:8080/send_command \
  -H "Content-Type: application/json" \
  -d '{"client_id": "CLIENT_ID", "command": "download_file", "filepath": "/storage/emulated/0/test.txt"}'
```

#### Ekran Görüntüsü
```bash
curl -X POST http://localhost:8080/send_command \
  -H "Content-Type: application/json" \
  -d '{"client_id": "CLIENT_ID", "command": "screenshot"}'
```

#### Konum Bilgisi
```bash
curl -X POST http://localhost:8080/send_command \
  -H "Content-Type: application/json" \
  -d '{"client_id": "CLIENT_ID", "command": "location"}'
```

## Güvenlik Uyarısı

⚠️ **Bu proje sadece eğitim amaçlıdır!**

- Sadece kendi cihazlarınızda test edin
- Kötü niyetli kullanım yasaktır
- Üçüncü şahıslara zarar vermek için kullanmayın
- Yerel ağda test edin, internet üzerinde çalıştırmayın

## Teknik Detaylar

### Sunucu
- **Framework**: Flask + Flask-SocketIO
- **Port**: 8080
- **WebSocket**: Socket.IO protokolü

### Android
- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 34
- **Socket**: Socket.IO client
- **Servis**: Foreground service

### İzinler
- `INTERNET`: Ağ bağlantısı
- `ACCESS_FINE_LOCATION`: GPS konum
- `READ_EXTERNAL_STORAGE`: Dosya okuma
- `WRITE_EXTERNAL_STORAGE`: Dosya yazma
- `FOREGROUND_SERVICE`: Arka plan servisi

## Sorun Giderme

1. **Bağlantı Sorunu**: IP adresini kontrol edin
2. **İzin Sorunu**: Uygulama ayarlarından izinleri açın
3. **Sunucu Hatası**: Port 8080'in boş olduğunu kontrol edin

## Lisans

Bu proje MIT lisansı altında yayınlanmıştır. Sadece eğitim amaçlı kullanın.