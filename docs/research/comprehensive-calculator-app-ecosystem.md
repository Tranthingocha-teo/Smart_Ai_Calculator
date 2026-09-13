# Báo Cáo Nghiên Cứu: Kiến Trúc & Tính Năng Của Một Ứng Dụng Máy Tính Toàn Diện (Comprehensive Calculator App Ecosystem)

> **Mục tiêu**: Phân tích toàn diện các tiêu chuẩn công nghiệp, đối chuẩn (benchmark) với các ứng dụng máy tính hàng đầu thế giới (Casio fx-580/880, Desmos, Photomath, HiPER Calc, Wolfram|Alpha, Apple Math Notes) và thiết lập lộ trình hoàn thiện cho dự án **Smart AI Calculator**.

---

## 1. Bức Tranh Toàn Cảnh & Đối Chuẩn Ngành (Market Benchmark)

Để được coi là một ứng dụng máy tính **đầy đủ và toàn diện (all-in-one comprehensive calculator)** trên thị trường hiện nay, sản phẩm thường kết hợp thế mạnh từ 5 phân khúc chính:

| Phân khúc | Đại diện tiêu biểu | Điểm mạnh cốt lõi |
| :--- | :--- | :--- |
| **Máy tính học sinh - sinh viên (Academic)** | Casio fx-580VN X, fx-880BTG, Texas Instruments TI-84 Plus CE | Hiển thị tự nhiên (Natural Display), giải phương trình/bất phương trình, ma trận, vector, thống kê, bảng giá trị (Table). |
| **Máy tính đồ thị tương tác (Graphing)** | Desmos, GeoGebra | Vẽ đồ thị 2D/3D mượt mà, zoom/pan tương tác, tìm điểm đặc biệt (cực trị, giao điểm, nghiệm), thanh trượt tham số (sliders). |
| **AI Scanner & Giải toán từng bước (EdTech)** | Photomath, Mathway, Symbolab, Apple Math Notes | Nhận diện chữ viết tay/chữ in qua camera (OCR), phân tích cấu trúc bài toán, sinh lời giải sư phạm từng bước kèm công thức. |
| **Tính toán kỹ thuật & Khoa học chuyên sâu (CAS)** | Wolfram\|Alpha, HiPER Scientific Calculator | Máy tính đại số ký hiệu (Computer Algebra System - CAS), số phức, tích phân/đạo hàm giải tích, Base-N lập trình viên. |
| **Tiện ích đời sống & Tài chính (Daily & Finance)** | Samsung Calculator, Xiaomi Mi Calculator | Chuyển đổi đơn vị, tỷ giá ngoại tệ trực tiếp, tính lãi suất khoản vay/tiết kiệm, chiết khấu, ngày tháng, sức khỏe. |

---

## 2. Bản Đồ 7 Trụ Cột Tính Năng Của Một Ứng Dụng Toàn Diện

Một ứng dụng máy tính toàn diện hiện đại bao gồm 7 trụ cột tính năng chính:

```
                      ┌────────────────────────────────────────┐
                      │   ALL-IN-ONE SMART CALCULATOR SUITE    │
                      └───────────────────┬────────────────────┘
          ┌───────────────────────┬───────┴───────┬───────────────────────┐
          ▼                       ▼               ▼                       ▼
   [1. Standard &          [2. Advanced      [3. Algebra &          [4. 2D Interactive
    Natural Display]        Scientific]       Equation Solver]       Graphing Engine]
   - Phân số, căn thức     - Số phức C       - Bậc 1, 2, 3, 4       - y = f(x), x = g(y)
   - Thao tác con trỏ      - Vi tích phân    - Hệ PT 2, 3, 4 ẩn     - Bảng giá trị Table
   - Hằng số lý/hóa        - Ma trận/Vector  - Bất phương trình     - Cực trị, giao điểm
   - Chuyển S <=> D        - Base-N Lập trình- Bậc hai 2 biến       - Trượt tham số a, b
          │                       │               │                       │
          └───────────────────────┼───────────────┼───────────────────────┘
                                  ▼               ▼
                           [5. Daily Life & [6. Smart AI & Vision
                            Financial Tools] Pedagogical Tutor]
                           - Tỷ giá online  - Camera OCR quét ảnh
                           - Đổi 15+ đơn vị - Gemini 1.5 Flash
                           - Lãi vay, Lãi kép- Giải bước sư phạm
                           - BMI, Giảm giá  - Chatbot hỏi đáp toán
                                  │               │
                                  └───────┬───────┘
                                          ▼
                                   [7. Core System & UX]
                                   - Lịch sử Room DB phân loại
                                   - LaTeX Render & Xuất PDF
                                   - Theme Material You, Haptic
```

---

### Trụ Cột 1: Hiển Thị Tự Nhiên & Biểu Thức Cơ Bản (Natural Display & Expression Engine)
1. **Natural V.P.A.M / Textbook Display**: Hiển thị phân số nhiều tầng $\frac{a}{b}$, căn bậc n $\sqrt[n]{x}$, tích phân $\int$, tổng xích-ma $\sum$ đúng như trong sách giáo khoa (thay vì chuỗi phẳng `sqrt(4)/(2+3)`).
2. **Con trỏ điều hướng thông minh (Smart Cursor Navigation)**: Cho phép chạm vào bất kỳ vị trí nào trong biểu thức để chèn, sửa hoặc xóa toán tử mà không phải nhập lại từ đầu.
3. **Chuyển đổi Định dạng Kết quả (S <=> D Key)**: Chuyển đổi qua lại giữa số phân số tối giản $\frac{a}{b}$, hỗn số $c\frac{a}{b}$, số thập phân vô hạn tuần hoàn $0.(3)$ và số thập phân thông thường.
4. **Bộ hằng số khoa học**: Bảng tra cứu tích hợp các hằng số vật lý, hóa học, toán học tiêu chuẩn ($c, G, h, \pi, e, k_B, N_A$).

---

### Trụ Cột 2: Tính Toán Khoa Học & Kỹ Thuật Chuyên Sâu (Advanced Scientific & Technical)
1. **Số phức (Complex Numbers $\mathbb{C}$)**:
   - Dạng đại số: $a + bi$.
   - Dạng lượng giác / cực: $r\angle\theta$.
   - Các hàm số phức: $\text{arg}(z)$, $|z|$, $\bar{z}$, liên hợp, lũy thừa số phức.
2. **Giải tích số (Numerical Calculus)**:
   - Đạo hàm tại một điểm: $\left.\frac{d}{dx}f(x)\right|_{x=x_0}$.
   - Tích phân xác định: $\int_{a}^{b} f(x)dx$ (Simpson / Gauss-Kronrod quadrature).
   - Tổng chuỗi $\sum_{x=a}^{b} f(x)$ và tích chuỗi $\prod_{x=a}^{b} f(x)$.
3. **Xác suất & Thống kê (Statistics & Probability)**:
   - Thống kê 1 biến: Trung bình $\bar{x}$, Trung vị (Median), Tứ phân vị ($Q_1, Q_3$), Phương sai ($s^2, \sigma^2$), Độ lệch chuẩn.
   - Hồi quy 2 biến: Tuyến tính $y = ax + b$, Parabol $y = ax^2 + bx + c$, Hàm mũ, Logarit; hệ số tương quan $r$.
   - Hoán vị $nPr$, Tổ hợp $nCr$, Giai thừa $n!$.
4. **Đại số tuyến tính & Ma trận (Linear Algebra & Matrices)**:
   - Hỗ trợ ma trận kích thước lên đến $4 \times 4$.
   - Phép toán: Cộng, trừ, nhân ma trận, Ma trận chuyển vị $A^T$, Định thức $\det(A)$, Ma trận nghịch đảo $A^{-1}$, Hạng của ma trận $\text{rank}(A)$.
5. **Máy tính Lập trình viên (Programmer / Base-N Calculator)**:
   - Hệ cơ số: HEX (16), DEC (10), OCT (8), BIN (2).
   - Phép toán Bitwise: AND, OR, XOR, NOT, NAND, NOR, XNOR.
   - Dịch bit: Shift Left/Right (Arithmetic, Logical, Circular).
   - Kích thước từ: QWORD (64-bit), DWORD (32-bit), WORD (16-bit), BYTE (8-bit) với số bù 2 (2's complement).

---

### Trụ Cột 3: Giải Phương Trình & Bất Phương Trình (Algebra & Equation Solver)
1. **Phương trình đại số đơn biến**:
   - Bậc nhất: $ax + b = 0$.
   - Bậc hai: $ax^2 + bx + c = 0$ (tính biệt thức $\Delta$, nghiệm thực hoặc phức, tọa độ đỉnh Parabol $x_v, y_v$).
   - Bậc ba: $ax^3 + bx^2 + cx + d = 0$ (nghiệm Cardano, tìm điểm cực đại, cực tiểu).
   - Bậc bốn: $ax^4 + bx^3 + cx^2 + dx + e = 0$.
2. **Hệ phương trình tuyến tính (System of Linear Equations)**:
   - Hệ 2 ẩn: $\begin{cases} a_1 x + b_1 y = c_1 \\ a_2 x + b_2 y = c_2 \end{cases}$ (Quy tắc Cramer hoặc thế).
   - Hệ 3 ẩn & 4 ẩn (Khử Gauss-Jordan).
3. **Bất phương trình đa thức (Polynomial Inequalities)**:
   - Bất phương trình bậc 2 ($ax^2 + bx + c > 0, \ge 0, < 0, \le 0$), trả về khoảng nghiệm chuẩn $(-\infty; x_1) \cup (x_2; +\infty)$.
4. **Giải phương trình số tổng quát (General Root Finding - Newton-Raphson)**:
   - Hỗ trợ tìm nghiệm xấp xỉ cho các phương trình siêu việt bất kỳ dạng $f(x) = 0$.

---

### Trụ Cột 4: Đồ Thị Hàm Số Tương Tác 2D (Interactive 2D Graphing Engine)
1. **Hỗ trợ đa dạng loại hàm**:
   - Hàm hiện (Explicit): $y = f(x)$.
   - Đồ thị nhiều hàm đồng thời: So sánh $f_1(x)$ và $f_2(x)$.
   - Bảng giá trị (Table of Values): Liệt kê cặp $(x, y)$ theo bước nhảy cấu hình được (Start, End, Step).
2. **Thao tác tương tác trực quan (Interactive Viewport)**:
   - Kéo (Pan) và phóng to/thu nhỏ (Pinch to Zoom) trực tiếp trên màn hình cảm ứng Jetpack Compose Canvas.
   - Tự động căn chỉnh trục tọa độ (Auto-scale viewport).
3. **Phát hiện điểm đặc biệt (Key Points Detection)**:
   - Tự động đánh dấu giao điểm với trục hoành ($y=0$), trục tung ($x=0$).
   - Tự động tìm điểm cực trị địa phương (Local Extrema: Min/Max).
   - Tự động tìm giao điểm giữa hai đồ thị khác nhau.
4. **Thanh trượt tham số (Parameter Sliders)**:
   - Gán tham số $a, b, m$ (ví dụ: $y = ax^2 + bx + c$) và dùng thanh trượt để quan sát sự biến thiên của đồ thị trong thời gian thực.

---

### Trụ Cột 5: Đời Sống, Tài Chính & Sức Khỏe (Daily Life & Financial Tools)
1. **Chuyển đổi đơn vị chuyên sâu (15+ Danh mục)**:
   - Độ dài, Diện tích, Thể tích, Khối lượng, Nhiệt độ, Tốc độ, Thời gian, Áp suất, Năng lượng, Công suất, Góc, Dung lượng lưu trữ số, Lực, Mức tiêu thụ nhiên liệu.
2. **Tỷ giá hối đoái thời gian thực (Live Currency Converter)**:
   - Cập nhật tỷ giá hơn 160 đồng tiền thế giới từ API mở (Frankfurter / Open Exchange Rates).
   - Chế độ offline cache cho phép chuyển đổi ngay cả khi mất mạng dựa trên tỷ giá cập nhật gần nhất.
3. **Tính toán tài chính nâng cao**:
   - Khoản vay (Loan/EMI): Dư nợ giảm dần, Gốc đều hàng tháng, Bảng chi tiết dòng tiền (Amortization schedule).
   - Lãi kép & Tiết kiệm: Lãi gộp định kỳ (hàng tháng, quý, năm) kèm biểu đồ tăng trưởng vốn.
   - Tính lương Net/Gross, Thuế thu nhập cá nhân.
4. **Sức khỏe & Đời sống**:
   - BMI & BMR (Tỷ lệ trao đổi chất cơ bản) + TDEE (Tổng năng lượng tiêu thụ hàng ngày).
   - Máy tính tuổi chính xác đến từng ngày, đếm ngược sự kiện, ngày làm việc trừ cuối tuần/lễ.
   - Tính tiền Tip & Chia hóa đơn (Bill Splitter) kèm thuế VAT.

---

### Trụ Cột 6: Trí Tuệ Nhân Tạo & Quét Đa Phương Thức (Smart AI & Multimodal Scanner)
1. **Camera Math OCR Scanner**:
   - Quét ảnh đề bài in trong sách bài tập hoặc chữ viết tay trên giấy.
   - Khung Crop Box tương tác để cô lập một bài toán duy nhất trên trang sách nhiều câu.
2. **Gemini Multimodal Math Solver**:
   - Nhận diện phương trình, hình học hoặc bài toán có lời văn (word problems).
   - Sinh cấu trúc lời giải sư phạm 3 phần:
     1. Kết quả cuối cùng (Final Answer - nổi bật).
     2. Diễn giải từng bước chi tiết (Step-by-step reasoning with formulas).
     3. Các công thức & định lý trọng tâm được áp dụng (Key Concepts).
3. **AI Math Tutor Chatbot**:
   - Cuộc trò chuyện theo ngữ cảnh (Contextual multi-turn conversation) giúp học sinh hỏi tiếp: "Tại sao từ bước 2 sang bước 3 lại đổi dấu?", "Có cách giải nào khác không?".

---

### Trụ Cột 7: Hệ Thống Cốt Lõi, UX & Trải Nghiệm Người Dùng (Core System & UX Layer)
1. **Lịch sử tính toán thông minh (Smart Persistent History)**:
   - Lưu trữ Room Database với khả năng lọc theo loại máy tính (Basic, Loan, BMI, AI Scan...).
   - Đánh dấu yêu thích (Pin/Star), tìm kiếm theo nội dung biểu thức, xóa từng mục hoặc toàn bộ.
2. **Xuất bản & Chia sẻ (Export & Share)**:
   - Xuất lời giải hoặc bảng tính ra PDF, hình ảnh chất lượng cao hoặc chuỗi mã LaTeX.
3. **Giao diện & Tương tác (Design & Accessibility)**:
   - Hỗ trợ Dynamic Color Material You, Dark/Light Mode.
   - Phản hồi rung xúc giác (Haptic Feedback) và âm thanh gõ phím nhẹ nhàng.
   - Hỗ trợ Widget ngoài màn hình chính (Quick Calculator / Currency Widget).

---

## 3. Ma Trận Đánh Giá: Hiện Trạng Smart AI Calculator vs. Tiêu Chuẩn Toàn Diện

| Trụ Cột / Hạng Mục | Tiêu Chuẩn Toàn Diện | Hiện Trạng Dự Án Hiện Tại | Mức Độ Hoàn Thiện |
| :--- | :--- | :--- | :---: |
| **1. Basic Calculator** | Shunting-Yard, ngoặc, lũy thừa, sin/cos/tan/log, Natural Display | Đã có Shunting-Yard, lũy thừa, sin/cos/tan/log; chưa có Natural LaTeX display | **70%** |
| **2. Scientific & Tech** | Số phức, Vi tích phân số, Thống kê, Ma trận, Base-N Lập trình | Đang có bàn phím Scientific cơ bản; chưa có Base-N, Ma trận, Vi tích phân | **30%** |
| **3. Equation Solver** | Bậc 1, 2, 3, 4, Hệ 2-3-4 ẩn, Bất phương trình | Đang thiếu hoàn toàn module giải phương trình đại số | **0%** (Cần xây dựng) |
| **4. Graphing 2D** | Vẽ hàm $y=f(x)$, Zoom/Pan, Bảng Table, Cực trị/Giao điểm | Đang thiếu hoàn toàn module đồ thị tương tác | **0%** (Cần xây dựng) |
| **5. Daily & Finance** | Đổi đơn vị, Tỷ giá realtime, Vay EMI, BMI, Tip, Date, GPA, Giảm giá | Đã có 7 màn hình tiện ích (Loan, BMI, Tip, Date, GPA, Discount, Unit) | **85%** |
| **6. AI & Scanner** | CameraX crop box, Gemini Vision solver, AI Chat tutor | Đã có UI Scanner, CameraPreview, ChatScreen; đang hoàn thiện SDK Gemini (#9, #10, #11) | **60%** |
| **7. History & System** | Room DB phân loại, Star, Search, Material You, Widget | Đã có Room DB, Lịch sử phân loại HistorySource; chưa có Widget, LaTeX export | **70%** |

---

## 4. Lộ Trình Đề Xuất Phát Triển Thành Siêu Ứng Dụng (Phased Roadmap)

Để đưa **Smart AI Calculator** từ phiên bản hiện tại trở thành một **ứng dụng máy tính toàn diện hoàn chỉnh**, lộ trình thực thi được phân bổ thành các giai đoạn:

### Giai Đoạn 1: Giải Phương Trình Đại Số (Equation & Systems Solver)
- Xây dựng Engine giải:
  - Phương trình bậc nhất ($ax + b = 0$) & bậc hai ($ax^2 + bx + c = 0$, tính $\Delta$, nghiệm thực/nghiệm phức).
  - Phương trình bậc ba ($ax^3 + bx^2 + cx + d = 0$).
  - Hệ 2 phương trình & 3 phương trình bậc nhất 2-3 ẩn số.
- Giao diện nhập liệu ma trận hệ số trực quan với bàn phím tùy biến `UnitKeypad`.
- Hiển thị các bước giải trung gian rõ ràng.

### Giai Đoạn 2: Vẽ Đồ Thị Hàm Số 2D Tương Tác (Interactive 2D Graphing Engine)
- Thành phần Jetpack Compose `Canvas` vẽ hệ trục tọa độ Descartes $Oxy$.
- Engine lấy mẫu điểm (Sampling engine) từ `CalculatorEngine` với biến $x$.
- Hỗ trợ thao tác vuốt di chuyển (Pan) và phóng to/thu nhỏ (Pinch-to-zoom).
- Tạo bảng giá trị (Table Screen) liệt kê $x \rightarrow y$.

### Giai Đoạn 3: Hoàn Thiện AI Multimodal Math Solver & Chatbot (Issues #9, #10, #11)
- Tích hợp chính thức Google Gemini 1.5 Flash SDK (`google.ai.client.generativeai`).
- Hoàn thiện luồng chụp ảnh CameraX với Crop Box tương tác.
- Render lời giải định dạng sư phạm (Pedagogical Math Response).
- Bộ nhớ ngữ cảnh phiên chat hỏi đáp bài tập với trợ lý AI.

### Giai Đoạn 4: Máy Tính Lập Trình Viên (Base-N) & Số Phức / Ma Trận
- Bổ sung chế độ máy tính lập trình viên: HEX, DEC, OCT, BIN, phép toán logic bitwise.
- Tính toán ma trận kích thước $2 \times 2$ và $3 \times 3$ (Cộng, nhân, định thức, nghịch đảo).
- Hỗ trợ số phức căn bản $a + bi$.

### Giai Đoạn 5: Đánh Bóng UX, Tỷ Giá Trực Tuyến & Tiện Ích
- Kết nối API tỷ giá tiền tệ trực tuyến có bộ nhớ đệm Offline.
- Hỗ trợ Widget màn hình chính (Quick Calculator Widget).
- Xuất lịch sử ra ảnh / LaTeX / PDF.
