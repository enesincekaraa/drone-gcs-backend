# Proje Günlüğü — drone-gcs-backend

## 5 Temmuz 2026 — Faz 0 & Faz 1: Kurulum ve İlk Bağlantı

### Ne yapıldı
- ArduPilot SITL macOS ARM64 üzerinde sıfırdan kuruldu ve derlendi
- MAVProxy console + map arayüzleri çalışır hale getirildi
- Java + Maven projesi (`drone-gcs-backend`) kuruldu, `dronefleet/mavlink` bağımlılığı eklendi
- UDP → InputStream köprü sınıfı (`UdpInputStream`) yazıldı
- İlk MAVLink Heartbeat mesajı Java'dan başarıyla yakalandı

### Karşılaşılan zorluklar
- macOS'ta sırayla eksik çıkan Python bağımlılıkları (pymavlink, empy, pexpect,
  MAVProxy, gnureadline, wxPython, matplotlib, pygame, opencv-python) teker
  teker teşhis edilip çözüldü
- UDP'nin bağlantısız/paket tabanlı yapısı ile MAVLink kütüphanesinin beklediği
  sürekli byte akışı (InputStream) arasındaki uyumsuzluk, özel bir köprü
  sınıfı yazılarak çözüldü

### Öğrenilenler
- UDP vs TCP farkı, DatagramSocket/DatagramPacket kullanımı
- InputStream'in tek zorunlu metodu (read()) ve override mantığı
- MAVLink protokolünün paket formatı (header, mesaj tipi, payload, checksum)
- Java'da instanceof pattern matching (Java 16+)

### Sıradaki adım
Faz 2: Konum ve pil telemetrisini de yakalayıp bir veri modeline aktarmak