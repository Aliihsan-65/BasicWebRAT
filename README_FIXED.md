# RAT Project - Educational Skeleton

## Düzeltilen Sorunlar

### 1. Logcat Service Sorunu ✅
- `Log.d()` yerine `Log.i()` kullanıldı
- Service başlatma ve bağlantı logları eklendi
- Logcat'te "RatService" tag'i ile görünecek

### 2. Python-Android Uyumsuzluk ✅
- `python-engineio` dependency eklendi
- Socket.IO logger'ları aktif edildi
- Versiyon uyumluluğu düzeltildi

### 3. Güvenlik İyileştirmeleri ✅
- Screenshot özelliği devre dışı bırakıldı
- Sadece temel komutlar çalışır (dosya listeleme, konum)

## Çalışma Talimatları

### 1. Server Kurulumu
```bash
cd server
python3 -m venv rat_env
source rat_env/bin/activate  # Linux/Mac
# rat_env\Scripts\activate   # Windows
pip install -r requirements.txt
python app.py
```

### 2. Android Kurulumu
- Android Studio'da projeyi aç
- Emülatör başlat
- IP adresini MainActivity.java'da güncelle (satır 26)
- Uygulamayı çalıştır
- "Connect" butonuna bas

### 3. Test Komutları
```bash
# Server çalışırken
python test_commands.py
```

## Çalışan Komutlar

1. **list_files** - Dosya listeleme
2. **location** - Konum alma
3. **client_info** - Cihaz bilgileri

## Logcat Kontrolü

```bash
adb logcat | grep RatService
```

## Güvenlik Notu

Bu uygulama sadece eğitim amaçlıdır. Gerçek cihazlarda kullanmayın.