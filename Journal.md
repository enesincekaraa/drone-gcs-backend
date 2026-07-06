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

## 6 Temmuz 2026 (devam) — Merkezi Log Yönetimi: Loki + Promtail + Grafana

### Ne yapıldı
- Projeye SLF4J + Logback bağımlılığı eklendi (`logback-classic`)
- `logback.xml` yapılandırması yazıldı: hem konsola hem `logs/app.log`
  dosyasına, günlük rotation (7 gün saklama) ile log yazımı sağlandı
- `Main.java`, `System.out.println` yerine gerçek bir SLF4J logger
  kullanacak şekilde güncellendi (placeholder `{}` sözdizimi kullanıldı)
- `docker-compose.yml` ile üç servisli bir log altyapısı kuruldu:
  Loki (log depolama), Promtail (log toplama ajanı), Grafana (görselleştirme)
- `promtail-config.yml` yazıldı: Promtail'in `logs/*.log` dosyalarını izleyip
  Loki'ye göndermesi sağlandı
- Grafana'da Loki veri kaynağı (data source) tanımlandı
- Grafana'nın Explore ekranında, Java uygulamasından akan gerçek zamanlı
  Heartbeat logları başarıyla görüntülendi

### Karşılaşılan zorluklar
- Loki container'ı başlamıyordu: `docker-compose.yml`'de `command:` alanı
  YAML listesi (`- config.file=...`) olarak yazılmıştı, bu da başındaki
  tireyi (`-`) YAML liste işareti olarak yorumlanmasına, dolayısıyla Loki'nin
  beklediği `-config.file=` bayrağının (flag) kaybolmasına sebep oldu.
  Düz string formatına (`command: -config.file=...`) çevrilerek çözüldü
- Grafana'nın Loki'ye `localhost` yerine Docker servis ismiyle (`loki:3100`)
  bağlanması gerektiği anlaşıldı — container'lar birbirine servis ismiyle
  ulaşır, host'un localhost'u ile karışmamalı

### Öğrenilenler
- SLF4J (ortak log arayüzü) ile Logback (gerçek işi yapan motor) arasındaki
  fark ve neden ayrı tutuldukları
- Log seviyeleri (ERROR, WARN, INFO, DEBUG) ve root logger kavramı
- Log rotation: `TimeBasedRollingPolicy` ile günlük arşivleme ve `maxHistory`
  ile eski dosyaların otomatik silinmesi
- Docker Compose'da `depends_on` ile başlatma sırası kontrolü
- Volume mount mantığı: host ve container arasında dosya sistemi paylaşımı
- Docker'ın iç ağında servislerin birbirine `localhost` değil, servis ismiyle
  (`loki`, `promtail` gibi) ulaştığı
- YAML'da liste (`- item`) ile düz string arasındaki sözdizimi farkı ve bunun
  komut satırı argümanlarını nasıl bozabileceği

### Sıradaki adım
Faz 2'ye devam: GlobalPositionInt (konum) ve BatteryStatus (pil) mesajlarını
da yakalayıp işlemek