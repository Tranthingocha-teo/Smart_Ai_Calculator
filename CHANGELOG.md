# Changelog

Mọi thay đổi đáng chú ý của dự án **Smart AI Calculator** được ghi lại chi tiết theo chuẩn [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) và tuân thủ [Semantic Versioning](https://semver.org/).

---

## [1.1.0] - 2026-09-13

### 🚀 Tính năng Mới (Added)
- **Tip Calculator Screen (`TipCalculatorScreen.kt`)**: Tính toán tiền tip theo hóa đơn, thuế %, số người chia tiền và phương thức chia trước/sau thuế.
- **Date Calculator Screen (`DateCalculatorScreen.kt`)**: 2 chế độ: Đo khoảng thời gian giữa 2 mốc ngày (ngày, tuần, năm) và cộng/trừ ngày tháng tùy biến.
- **Loan Calculator Screen (`LoanCalculatorScreen.kt`)**: Tính toán lịch trả nợ vay theo 2 phương thức: Dư nợ giảm dần (Equal Principal) và Trả góp đều hàng tháng (EMI).
- **GPA Calculator Screen (`GpaCalculatorScreen.kt`)**: Bảng tính điểm trung bình tích lũy theo tín chỉ, hỗ trợ thêm/sửa/xóa môn học, sắp xếp theo điểm và bảng chữ cái.
- **Pure Math Engines (`util/calculator/*`)**: Tách biệt logic toán học và tài chính thành các module độc lập không phụ thuộc UI.
- **Toàn bộ Unit Test (`test/calculator/*`)**: Kiểm thử tự động 100% các trường hợp biên và thuật toán cho Tip, Date, Loan, GPA.

### 🛠️ Quản trị Dự án (Project Management)
- Thiết lập hệ thống GitHub Actions CI/CD tự động kiểm thử và build APK.
- Cấu hình Dependabot tự động quét lỗ hổng bảo mật.
- Thiết lập bộ Issue Templates, Pull Request Template và hướng dẫn `CONTRIBUTING.md`.
- Bổ sung bộ quyết định kiến trúc ADRs 0001 - 0005.

---

## [1.0.0] - 2026-09-12

### 🚀 Tính năng Cơ bản (Initial MVP)
- Khởi tạo kiến trúc ứng dụng Android Jetpack Compose với Koin DI.
- **Basic Calculator**: Bộ máy tính số học cơ bản với thuật toán Shunting-Yard.
- **Converter Screen**: Chuyển đổi đơn vị (chiều dài, diện tích, thể tích, khối lượng).
- **BMI Calculator**: Tính chỉ số khối cơ thể và phân loại tình trạng sức khỏe.
- **Discount Calculator**: Tính toán giá sau giảm giá và số tiền tiết kiệm.
- Hỗ trợ đổi theme Sáng/Tối và Đa ngôn ngữ cơ bản.
