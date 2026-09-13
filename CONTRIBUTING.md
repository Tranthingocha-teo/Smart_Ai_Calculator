# Quy định Đóng góp & Quản lý Dự án (Contributing Guidelines)

Chào mừng bạn đến với dự án **Smart AI Calculator**! Tài liệu này quy định tiêu chuẩn kỹ thuật và quy trình quản trị dự án theo chuẩn Công nghệ Phần mềm chuyên nghiệp.

---

## 🌿 1. Quy trình Phân nhánh (Git Branching Model - GitHub Flow)

- Nhánh `main` là nhánh sản xuất duy nhất, luôn trong trạng thái **Build Thành công** và **Pass 100% Tests**.
- **Không bao giờ commit trực tiếp lên `main`**. Mọi tính năng mới hoặc bản sửa lỗi phải được thực hiện trên nhánh riêng:
  - `feature/<tên-tính-năng>` (ví dụ: `feature/ai-scan-gemini`, `feature/room-history`)
  - `fix/<mô-tả-lỗi>` (ví dụ: `fix/datepicker-timezone-shift`)
  - `test/<phạm-vi-test>` (ví dụ: `test/loan-calculator-unit-test`)
  - `docs/<chủ-đề>` (ví dụ: `docs/adr-architecture-records`)

---

## 📝 2. Quy chuẩn Viết Commit (Conventional Commits)

Mọi commit phải tuân thủ nghiêm ngặt định dạng:

```text
<type>(<scope>): <mô tả ngắn gọn bằng thể mệnh lệnh>

[Mô tả chi tiết tùy chọn]
```

### Các Type hợp lệ:
- `feat`: Thêm tính năng mới cho người dùng.
- `fix`: Sửa lỗi phát sinh trong mã nguồn.
- `test`: Thêm hoặc chỉnh sửa các bộ Unit Test.
- `refactor`: Tái cấu trúc mã nguồn không thay đổi chức năng.
- `docs`: Chỉnh sửa tài liệu (README, ADR, KDoc).
- `ci`: Cấu hình GitHub Actions, workflow, dependabot.
- `chore`: Cập nhật cấu hình build, dependencies Gradle.

---

## 🔀 3. Quy trình Mở Pull Request (PR)

1. Đẩy nhánh làm việc lên GitHub: `git push -u origin feature/<tên-nhánh>`
2. Tạo Pull Request vào nhánh `main` sử dụng mẫu `.github/pull_request_template.md`.
3. Điền đầy đủ:
   - Mô tả thay đổi.
   - Liên kết Issue tương ứng (`Closes #<issue_number>`).
   - Đánh dấu checklist kiểm thử.
4. Đảm bảo toàn bộ kiểm tra của **GitHub Actions CI** chạy thành công (**All checks have passed**).
5. Sau khi được review hoặc đạt tiêu chuẩn chất lượng, thực hiện **Squash and merge** vào `main`.

---

## 🧪 4. Quy chuẩn Kiểm thử & Biên dịch

Trước khi commit bất kỳ dòng code nào, lập trình viên bắt buộc phải chạy lệnh:

```bash
# Chạy toàn bộ Unit Tests
./gradlew testDebugUnitTest

# Biên dịch kiểm tra lỗi
./gradlew assembleDebug
```
Nếu có bất kỳ test nào thất bại (`FAILURE`), lập trình viên phải sửa triệt để trước khi đẩy lên remote.
