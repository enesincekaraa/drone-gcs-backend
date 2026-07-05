## 5 Temmuz 2026 — Faz 0 & Faz 1: SITL Kurulumu ve İlk MAVLink Bağlantısı

### Ne yapıldı
- ArduPilot SITL, macOS ARM64 üzerinde sıfırdan klonlanıp derlendi (1400+ dosya,
  yaklaşık 50 saniyelik build süresi)
- MAVProxy console ve map arayüzleri çalışır hale getirildi (gerçek uydu
  görüntülü harita, canlı telemetri konsolu)
- `drone-gcs-backend` adında yeni bir Java + Maven projesi kuruldu
- `dronefleet/mavlink` kütüphanesi bağımlılık olarak eklendi
- `UdpInputStream` sınıfı yazıldı: UDP'nin paket tabanlı yapısını,
  MAVLink kütüphanesinin beklediği sürekli byte akışına (InputStream) çeviren
  köprü sınıfı
- `Main` sınıfı yazıldı: `MavlinkConnection` üzerinden SITL'e bağlanıp,
  sonsuz döngüde mesajları okuyup, `instanceof` pattern matching ile
  Heartbeat mesajlarını filtreleyip ekrana basıyor
- İlk kez Java'dan gerçek zamanlı MAVLink Heartbeat mesajı yakalandı
- Proje git ile versiyonlandı, GitHub'a push edildi:
  https://github.com/enesincekaraa/drone-gcs-backend

### Karşılaşılan zorluklar
- macOS'ta SITL kurulumu sırasında sırayla eksik çıkan Python bağımlılıkları
  teker teker teşhis edilip çözüldü: pymavlink, empy (import ismi farklı: em),
  pexpect, MAVProxy, gnureadline, wxPython, matplotlib, pygame, opencv-python
- `.gitignore` dosyasındaki `.idea/` kuralı ilk seferde çalışmadı, dosyanın
  sonuna tekrar eklenerek düzeltildi

### Öğrenilenler
- UDP vs TCP: bağlantısız, paket tabanlı iletişim mantığı
- `DatagramSocket` (dinleme) ve `DatagramPacket` (gelen veriyi tutma) ayrımı
- `InputStream`'in tek zorunlu metodu (`read()`) ve override mantığı
- `final` alanların ne zaman kullanılması gerektiği (değişmeyecek referanslar
  için, değişecek sayaçlar için değil)
- `this` anahtar kelimesinin constructor'da parametre/field çakışmasını
  çözme amacı
- MAVLink protokolünün paket formatı (header, mesaj tipi, payload, checksum)
- Java'da `instanceof` pattern matching (Java 16+)
- Git temel iş akışı: `init` → `add` → `commit` → `remote add` → `push`

### Sıradaki adım
Faz 2: Konum (GlobalPositionInt) ve pil (BatteryStatus) telemetrisini de
yakalayıp, bunları bir Java domain modeline (record/entity) aktarmak