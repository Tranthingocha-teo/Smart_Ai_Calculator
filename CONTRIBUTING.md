# Quy Chuẩn Đóng Góp & Làm Việc Nhóm (Team Collaboration Guide)

Chào mừng bạn đến với đội ngũ phát triển **Smart AI Calculator**! Tài liệu này quy định toàn bộ quy trình làm việc chung (Git Workflow, Task Management, Testing, và Code Review) nhằm đảm bảo cả nhóm phối hợp nhịp nhàng, code luôn sạch và không bao giờ gặp xung đột làm vỡ nhánh chính (`main`).

---

## 📌 1. Quản Lý Tiến Độ & Nhận Việc (Task Management)

Toàn bộ công việc của dự án được theo dõi trực quan tại:
👉 [**GitHub Project: Smart AI Calculator Roadmap**](https://github.com/users/Tranthingocha-teo/projects/1)

### Vòng đời của một Ticket (Status Lifecycle)
```
[ Backlog ] ──► [ Todo ] ──► [ In Progress ] ──► [ In Review ] ──► [ Done ]
```

1. **Backlog / Todo**: Các ticket chưa làm.
2. **Nhận Ticket**:
   - Khi bạn chuẩn bị làm một ticket (ví dụ Issue `#29`), hãy gán chính mình vào mục **Assignee** trên GitHub Issue.
   - Kéo thẻ ticket từ **Todo** sang **In Progress**.
   - *Lưu ý*: Mỗi thành viên tại một thời điểm chỉ nên tập trung vào **1 ticket đang In Progress**.

---

## 🌿 2. Quy Chuẩn Rẽ Nhánh (Branching Strategy)

**Tuyệt đối không làm việc trực tiếp trên nhánh `main`.**

Mỗi ticket phải được thực hiện trên một nhánh độc lập (Feature Branch) tạo từ `main` mới nhất:

```bash
# 1. Cập nhật nhánh main mới nhất từ remote
git checkout main
git pull origin main

# 2. Tạo nhánh mới theo mã số Issue và tên ngắn gọn
git checkout -b feature/<issue_number>-<slug>

# Ví dụ cho Ticket #29:
git checkout -b feature/29-linear-unit-engine
```

### Quy ước đặt tên nhánh:
- **Tính năng mới**: `feature/<issue_number>-<slug>` (Ví dụ: `feature/29-linear-unit-engine`, `feature/30-temperature-conversion`).
- **Sửa lỗi**: `fix/<issue_number>-<slug>` (Ví dụ: `fix/35-fix-division-by-zero`).
- **Tái cấu trúc**: `refactor/<issue_number>-<slug>` (Ví dụ: `refactor/40-koin-modules`).

---

## 🧪 3. Quy Trình Phát Triển & Kiểm Thử (TDD Workflow)

Dự án áp dụng phương pháp **Test-Driven Development (TDD)** và kiến trúc phân lớp thuần túy ([ADR 0005](docs/adr/0005-modular-math-calculation-engines.md)):

1. **Viết Unit Test trước (Red)**: Viết test cho logic cần triển khai tại thư mục `app/src/test/java/...`. Chạy test thấy báo đỏ (fail vì chưa có code xử lý).
2. **Viết Code chức năng (Green)**: Viết lượng code tối thiểu để test chuyển sang màu xanh (pass).
3. **Tối ưu & Tái cấu trúc (Refactor)**: Làm sạch code, đảm bảo tuân thủ Clean Architecture và định dạng Kotlin.
4. **Xác minh trước khi commit**:
   ```bash
   # Chạy toàn bộ Unit Test cục bộ
   ./gradlew testDebugUnitTest
   
   # Kiểm tra build APK debug
   ./gradlew assembleDebug
   ```
   *Chỉ khi lệnh test kết thúc với `BUILD SUCCESSFUL` thì mới được commit code!*

---

## 💬 4. Quy Chuẩn Commit (Conventional Commits)

Nội dung commit cần ngắn gọn, rõ nghĩa và luôn tham chiếu đến mã số Issue:

```
<type>(<scope>): <mô tả ngắn bằng tiếng Anh hoặc tiếng Việt> (closes #<issue_number>)
```

### Các tiền tố `<type>` chuẩn:
- `feat`: Tính năng mới (ví dụ: `feat(calculator): add linear unit conversion engine (closes #29)`)
- `fix`: Sửa lỗi (ví dụ: `fix(currency): handle network timeout in currency repo (closes #31)`)
- `test`: Bổ sung hoặc chỉnh sửa unit test (ví dụ: `test(unit): add boundary tests for temperature (closes #30)`)
- `refactor`: Tái cấu trúc mã nguồn không thay đổi chức năng
- `docs`: Chỉnh sửa tài liệu, README, ADR

---

## 🚀 5. Mở Pull Request & Quy Trình Review Code

Sau khi hoàn thành và test xanh, đẩy nhánh lên GitHub:

```bash
git push -u origin feature/<issue_number>-<slug>
```

### Mở Pull Request trên GitHub:
1. Tạo PR từ nhánh `feature/...` của bạn vào nhánh `main`.
2. Điền đầy đủ nội dung theo mẫu [pull_request_template.md](.github/pull_request_template.md):
   - Đảm bảo có dòng **`Closes #<issue_number>`** để GitHub tự động liên kết và đóng Issue khi merge.
   - Liệt kê tóm tắt các thay đổi và checklist đã kiểm thử.
3. Kéo thẻ ticket trên Project Board sang cột **In Review**.
4. Chọn ít nhất **1 đồng đội trong nhóm làm Reviewer**.

### Tiêu chuẩn để một PR được phép Merge vào `main`:
- [x] **CI Xanh 100%**: GitHub Actions workflow `Android CI / CD` chạy hoàn tất thành công.
- [x] **Có Review & Phê duyệt**: Nhận được ít nhất **1 lượt Approve** từ thành viên trong nhóm.
- [x] **Không có xung đột (No merge conflicts)** với nhánh `main`.

---

## 🔀 6. Chiến Lược Merge (Squash and Merge)

Khi PR đã sẵn sàng và được duyệt:
1. Nhấn nút **"Squash and merge"** trên GitHub.
2. Tiêu đề commit gộp sẽ có dạng: `feat(calculator): <tên tính năng> (#<PR_number>)`.
3. Xóa nhánh tạm sau khi merge bằng nút **"Delete branch"** trên giao diện GitHub.
4. GitHub sẽ tự động:
   - Đóng Issue liên quan.
   - Dời thẻ ticket trên Project Board sang cột **Done**.

---

## ⚠️ 7. Xử Lý Khi Nhánh Của Bạn Bị Xung Đột (Merge Conflicts)

Nếu trong lúc bạn đang code, đồng đội đã merge code mới vào `main`, hãy cập nhật nhánh của bạn:

```bash
# Chuyển về main và kéo code mới nhất về
git checkout main
git pull origin main

# Chuyển lại về nhánh tính năng và Rebase lên main mới nhất
git checkout feature/<issue_number>-<slug>
git rebase main

# Nếu có xung đột, mở các file conflict lên giải quyết, sau đó:
git add <các file đã sửa>
git rebase --continue

# Đẩy lại lên remote (dùng force-with-lease an toàn)
git push --force-with-lease origin feature/<issue_number>-<slug>
```

---

*Chúc cả nhóm phối hợp hiệu quả và xây dựng ứng dụng Smart AI Calculator xuất sắc! 🎉*
