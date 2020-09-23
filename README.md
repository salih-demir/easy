# Easy Android Application

[Uygulama linki](https://github.com/salih-demir/easy/blob/master/easy.apk?raw=true)

## Uygulamanın Fonksiyonları:

- Veri yenileme mekanizması (swipe to refresh)
- Liste görünümü değiştirme
- Yorum sıralama
- Yorum arama
- Yorum sırası değiştirme (uzun basılı tutunca)
- Yorum okuma (Yoruma basıldığında yorum sesli olarak kendi dilinde okunuyor.)
- Ayarlar (Gece modu ve dil değiştirme)
- Hızlı aksiyonlar (Uygulama ikonuna uzun basınca çıkıyor.)
- Hareketli karşılama ekranı

## Uygulamanın Teknik Altyapısı
- Jacoco test kapsam raporu (her konfigurasyon için ayrı olacak şekilde) otomatik olarak oluşturuluyor.
- Mock server geliştirmesi (veriler internet isteği atılıyormuş gibi çekiliyor.)
- Server dil desteği
- Uygulama hatalarını takip için hata servisi
- Servis yanıtlarını loglama
- Responsive tasarım, tablet ile kullanabilirsiniz.
- Değiştirilebilir font desteği.

## Uygulamanın Mimarisi
- 100% Kotlin ile yazıldı.
- Tema değişimi ile uyumlu, renklerin ve temaların hepsi referans olarak girildi.
- Android context ile uyumlu çalışan [__AppModule__](https://github.com/salih-demir/easy/blob/master/code/app/src/main/java/com/cascade/easy/app/AppModule.kt) yapısı
- Kullanılan kütüphanelerin ProGuard konfigürasyonu yapılmıştır.
- Verilerin korunumu LiveData kullanılarak sağlanmıştır.
- Android Lint 0 hata oranı (false positive durumlar hariç)
