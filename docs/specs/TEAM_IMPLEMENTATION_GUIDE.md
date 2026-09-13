# 📘 Cẩm Nang Hướng Dẫn Kỹ Thuật Dành Cho Đội Ngũ Phát Triển (Team Implementation Guide)

Chào **Ánh Sáng** (`@dinhthianhsang`) và **Thùy Dung** (`@thuyydung`)!  
Tài liệu này được biên soạn bởi Team Lead (**Ngọc Hà**) nhằm hướng dẫn chi tiết từng bước (Step-by-step) cách thức triển khai các Ticket đã được phân công trên [Roadmap Board](https://github.com/users/Tranthingocha-teo/projects/1).

Mọi thứ đã được thiết kế sẵn theo chuẩn kiến trúc của dự án ([ADR 0005](docs/adr/0005-modular-math-calculation-engines.md) và [ADR 0007](docs/adr/0007-offline-first-unit-currency-converter.md)), các bạn chỉ cần làm theo các bước dưới đây bằng Android Studio thông thường mà không cần bất kỳ công cụ phức tạp nào.

---

## 🧭 Quy Trình Chung Cho Mỗi Ticket

Mỗi khi bắt đầu làm một ticket:
1. Mở Terminal trong Android Studio:
   ```bash
   git checkout main
   git pull origin main
   git checkout -b feature/<mã_ticket>-<tên_ngắn>
   ```
2. Mở file code tương ứng và hoàn thiện logic.
3. Chạy kiểm thử tự động trên máy:
   ```bash
   ./gradlew testDebugUnitTest
   ```
   *(Đảm bảo terminal báo `BUILD SUCCESSFUL`)*
4. Commit và đẩy lên GitHub:
   ```bash
   git add .
   git commit -m "feat(<scope>): <mô tả> (closes #<mã_ticket>)"
   git push -u origin feature/<mã_ticket>-<tên_ngắn>
   ```
5. Mở Pull Request trên GitHub: Bot sẽ **tự động gán 2 thành viên còn lại vào Review** và kiểm tra kết nối issue giúp bạn!

---

## 👩‍💻 PHẦN 1: DÀNH CHO ĐINH THỊ ÁNH SÁNG (@dinhthianhsang)

### 🎫 Nhiệm Vụ 1: Ticket #30 — Non-Linear Affine Conversion Engine (Temperature)
- **Mục tiêu**: Hỗ trợ chuyển đổi qua lại giữa 3 đơn vị nhiệt độ: **Độ C (Celsius)**, **Độ F (Fahrenheit)**, và **Độ K (Kelvin)**.
- **Tạo nhánh**:
  ```bash
  git checkout -b feature/30-temperature-conversion
  ```
- **File cần chỉnh sửa**:
  1. `app/src/main/java/dhn/intern/smart_ai_caculator_app/enum/UnitCategory.kt`:
     - Thêm `TEMPERATURE` vào enum.
  2. `app/src/main/java/dhn/intern/smart_ai_caculator_app/util/calculator/UnitConverterUtil.kt`:
     - Thêm hàm chuyển đổi nhiệt độ:
       ```kotlin
       fun convertTemperature(value: Double, fromUnit: String, toUnit: String): Double {
           if (fromUnit == toUnit) return value
           
           // Bước 1: Đổi từ fromUnit về Celsius làm chuẩn trung gian
           val celsius = when (fromUnit) {
               "°C" -> value
               "°F" -> (value - 32.0) * 5.0 / 9.0
               "K"  -> value - 273.15
               else -> value
           }
           
           // Bước 2: Đổi từ Celsius sang toUnit
           return when (toUnit) {
               "°C" -> celsius
               "°F" -> (celsius * 9.0 / 5.0) + 32.0
               "K"  -> celsius + 273.15
               else -> celsius
           }
       }
       ```
  3. `app/src/main/java/dhn/intern/smart_ai_caculator_app/data/source/unit_calculator/unitsByCategory.kt`:
     - Bổ sung danh sách đơn vị cho `UnitCategory.TEMPERATURE`:
       ```kotlin
       UnitCategory.TEMPERATURE -> listOf(
           UnitItemUI("°C", "Celsius"),
           UnitItemUI("°F", "Fahrenheit"),
           UnitItemUI("K", "Kelvin")
       )
       ```
- **Viết Unit Test kiểm tra**:
  Mở `app/src/test/java/dhn/intern/smart_ai_caculator_app/calculator/UnitConverterUtilTest.kt` và thêm các case:
  - 0°C $\to$ 32°F (nhiệt độ đóng băng)
  - 100°C $\to$ 212°F (nhiệt độ sôi)
  - -40°C $\to$ -40°F (điểm hội tụ đặc biệt)
  - 0K $\to$ -273.15°C (độ không tuyệt đối)
- **Kiểm tra & Commit**:
  ```bash
  ./gradlew testDebugUnitTest
  git commit -m "feat(calculator): implement temperature affine conversion formulas (closes #30)"
  git push -u origin feature/30-temperature-conversion
  ```

---

### 🎫 Nhiệm Vụ 2: Ticket #32 — Searchable UnitPickerBottomSheet with Country Flags
*(Thực hiện sau khi Ticket #30 và #31 hoàn thành)*
- **Mục tiêu**: Nâng cấp BottomSheet chọn đơn vị có ô tìm kiếm và hiển thị cờ quốc gia cho tiền tệ.
- **Tạo nhánh**:
  ```bash
  git checkout -b feature/32-searchable-unit-picker
  ```
- **File cần chỉnh sửa**:
  `app/src/main/java/dhn/intern/smart_ai_caculator_app/ui/components/BottomSheets.kt` (tìm hàm `UnitPickerBottomSheet`).
- **Nội dung thực hiện**:
  1. Thêm một `var searchQuery by remember { mutableStateOf("") }`.
  2. Đặt một `OutlinedTextField` hoặc ô Search ở đầu danh sách có icon kính lúp và nút xóa `✕`.
  3. Lọc danh sách theo từ khóa:
     ```kotlin
     val filteredUnits = units.filter {
         it.lable.contains(searchQuery, ignoreCase = true) ||
         it.des.contains(searchQuery, ignoreCase = true)
     }
     ```
  4. Hiển thị cờ emoji cho tiền tệ (🇺🇸 USD, 🇪🇺 EUR, 🇻🇳 VND, 🇯🇵 JPY, 🇬🇧 GBP, 🇰🇷 KRW, v.v.).

---

## 👩‍💻 PHẦN 2: DÀNH CHO THÙY DUNG (@thuyydung)

### 🎫 Nhiệm Vụ 1: Ticket #31 — Offline-First Currency Converter & Rate Caching
- **Mục tiêu**: Lấy tỷ giá từ API mở, lưu vào Room Database để dùng offline và fallback khi mất mạng.
- **Tạo nhánh**:
  ```bash
  git checkout -b feature/31-offline-first-currency
  ```
- **API Endpoint tỷ giá**:
  - URL: `https://open.er-api.com/v6/latest/USD` (Miễn phí 100%, không cần đăng ký API Key, trả về JSON tỷ giá theo đồng USD).
- **Cấu trúc dữ liệu Room Database**:
  1. **Tạo Entity** `app/src/main/java/dhn/intern/smart_ai_caculator_app/data/local/entity/CurrencyRateEntity.kt`:
     ```kotlin
     @Entity(tableName = "currency_rates")
     data class CurrencyRateEntity(
         @PrimaryKey val code: String,       // "VND", "EUR", "JPY"
         val rateToUsd: Double,              // Tỷ giá quy đổi so với 1 USD
         val lastUpdated: Long               // Timestamp cập nhật
     )
     ```
  2. **Tạo DAO** `app/src/main/java/dhn/intern/smart_ai_caculator_app/data/local/dao/CurrencyRateDao.kt`:
     ```kotlin
     @Dao
     interface CurrencyRateDao {
         @Insert(onConflict = OnConflictStrategy.REPLACE)
         suspend fun insertRates(rates: List<CurrencyRateEntity>)
         
         @Query("SELECT * FROM currency_rates")
         suspend fun getAllRates(): List<CurrencyRateEntity>
     }
     ```
  3. **Đăng ký vào `AppDatabase`**:
     Thêm `CurrencyRateEntity::class` vào mảng `entities` trong `AppDatabase.kt`.
  4. **Tạo Repository Offline-First** (`CurrencyRepository.kt`):
     - Hàm `suspend fun getRates(): Map<String, Double>`:
       - Thử gọi HTTP lấy dữ liệu từ `open.er-api.com` trên `Dispatchers.IO`.
       - Nếu thành công $\to$ Lưu vào Room DB và trả về kết quả.
       - Nếu thất bại hoặc thiết bị mất mạng $\to$ Đọc từ Room DB.
       - Nếu Room DB cũng trống (app cài mới chưa có mạng) $\to$ Trả về bộ dữ liệu tĩnh đóng gói sẵn (`DefaultCurrencyRates.kt`: USD=1.0, VND=25450.0, EUR=0.92, JPY=155.0...).
- **Quy tắc làm tròn tiền tệ trong `SmartFormatter`**:
  - Với VND, JPY: làm tròn số nguyên không có số thập phân (`25,450 đ`).
  - Với USD, EUR: làm tròn 2 đến 4 số thập phân (`1.0850 $`).
- **Kiểm tra & Commit**:
  ```bash
  ./gradlew testDebugUnitTest
  git commit -m "feat(currency): implement offline-first currency repository and cache (closes #31)"
  git push -u origin feature/31-offline-first-currency
  ```

---

### 🎫 Nhiệm Vụ 2: Ticket #33 — Debounced Conversion History in Room Database
*(Thực hiện sau khi Ticket #31 hoàn thành)*
- **Mục tiêu**: Tự động lưu phép quy đổi hoàn chỉnh vào Lịch sử tính toán sau 1.5 – 2 giây người dùng ngừng gõ (chống rác phím bấm).
- **Tạo nhánh**:
  ```bash
  git checkout -b feature/33-debounced-conversion-history
  ```
- **File cần chỉnh sửa**:
  1. `app/src/main/java/dhn/intern/smart_ai_caculator_app/enum/HistorySource.kt`:
     - Thêm enum `UNIT_CONVERTER` vào danh sách.
  2. `UnitCalculatorViewModel.kt`:
     - Tạo một coroutine `debounceJob: Job? = null`.
     - Mỗi khi người dùng gõ số, hủy job cũ và hẹn giờ 1.5 giây:
       ```kotlin
       debounceJob?.cancel()
       debounceJob = viewModelScope.launch {
           delay(1500)
           if (currentInput.toDoubleOrNull() != null && currentInput.toDouble() > 0) {
               historyRepository.insertHistory(
                   CalculatorHistoryEntity(
                       expression = "$fromValue $fromUnitName",
                       result = "$toValue $toUnitName",
                       source = HistorySource.UNIT_CONVERTER,
                       timestamp = System.currentTimeMillis()
                   )
               )
           }
       }
       ```
  3. `app/src/main/java/dhn/intern/smart_ai_caculator_app/ui/components/history/CardItemsHistory.kt`:
     - Render thẻ lịch sử cho mục `UNIT_CONVERTER` đẹp mắt, có icon chuyển đổi hai chiều.

---

## 🤝 Hỗ Trợ Kỹ Thuật

Nếu các bạn gặp khó khăn trong quá trình làm, hãy:
- Tag `@Tranthingocha-teo` trực tiếp trên Issue hoặc Pull Request trên GitHub.
- Hoặc mở mục Discussion / Chat nhóm để cùng thảo luận.

Chúc nhóm hoàn thành xuất sắc các mục tiêu đề ra! 🚀
