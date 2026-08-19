# 🛠️ KẾ HOẠCH KỸ THUẬT & HƯỚNG DẪN THỰC THI TỪNG BƯỚC (AI-READY ROADMAP)
## DỰ ÁN: "SCROLL & SCROLL - CUỘN GIẤY VỆ SINH VÔ TẬN"
*(Platform: Android Native | Ngôn ngữ: Kotlin | UI: Jetpack Compose | Target SDK: 36 | Min SDK: 24)*

---

## 📌 HƯỚNG DẪN DÀNH CHO CÁC AI AGENT KẾ TIẾP
> **Tài liệu này được thiết kế để bất kỳ AI nào (hoặc Lập trình viên) khi đọc vào đều có thể hiểu ngay kiến trúc và thực hiện tiếp công việc mà không bị mất ngữ cảnh.**
> 
> - **Mã nguồn dự án**: `e:\Android\ScrollnScroll`
> - **Tài liệu thiết kế & Use-case chi tiết**: Xem file [`ý tưởng và phân tích.md`](file:///e:/Android/ScrollnScroll/%C3%BD%20t%C6%B0%E1%BB%9Fng%20v%C3%A0%20ph%C3%A2n%20t%C3%ADch.md)
> - **Quy tắc thực thi**: Triển khai theo từng **Phase (Giai đoạn)** từ 1 đến 7. Mỗi Phase độc lập, có mã Prompt mẫu, danh sách file cụ thể và Checklist kiểm thử rõ ràng. Không nhảy cóc các bước.

---

## MỤC LỤC KẾ HOẠCH KỸ THUẬT
- [PHASE 0: KIẾN TRÚC TỔNG THỂ & CẤU TRÚC THƯ MỤC](#phase-0-kiến-trúc-tổng-thể--cấu-trúc-thư-mục)
- [PHASE 1: FOUNDATION & DESIGN SYSTEM (Theme, Components, Sound & Haptics)](#phase-1-foundation--design-system)
- [PHASE 2: DATA LAYER & STATE MANAGEMENT (DataStore, Models, Repository, ViewModel)](#phase-2-data-layer--state-management)
- [PHASE 3: CORE GAMEPLAY - CANVAS CUỘN GIẤY & VẬT LÝ CUỘN (Physics, SPS & Overdrive)](#phase-3-core-gameplay---canvas-cuộn-giấy--vật-lý-cuộn)
- [PHASE 4: HỆ THỐNG TIẾN HÓA SKIN & TỦ ĐỒ (Skins & Badges Cabinet)](#phase-4-hệ-thống-tiến-hóa-skin--tủ-đồ)
- [PHASE 5: BẰNG KHEN VIRAL & TRÌNH CHIA SẺ STORY (Certificate & Share Intent)](#phase-5-bằng-khen-viral--trình-chia-sẻ-story)
- [PHASE 6: BẢNG XẾP HẠNG TOÀN CẦU (Leaderboard Screen)](#phase-6-bảng-xếp-hạng-toàn-cầu)
- [PHASE 7: NAVIGATION, POLISHING & BUILD RELEASE (App Navigation & Final Polish)](#phase-7-navigation-polishing--build-release)

---

## PHASE 0: KIẾN TRÚC TỔNG THỂ & CẤU TRÚC THƯ MỤC

### 1. Tech Stack Chuẩn
*   **Language**: Kotlin 2.x (Coroutines, StateFlow, Flow)
*   **UI Framework**: Jetpack Compose (Material 3, Custom Canvas, Animatable, Compose Foundation Gestures)
*   **Architecture**: MVVM + Clean Architecture (Data Layer $\rightarrow$ Repository $\rightarrow$ ViewModel $\rightarrow$ UI Screen)
*   **Local Persistence**: AndroidX DataStore Preferences (Lưu tổng mét, danh hiệu, skin đã mở khóa)
*   **Low-latency Audio**: Android `SoundPool` (Độ trễ phản hồi âm thanh ASMR $< 10\text{ms}$)
*   **Haptic Engine**: Android `Vibrator` / `HapticFeedbackConstants`

### 2. Cây Thư Mục Dự Án Chuẩn (Directory Tree)
```text
app/src/main/java/com/trustmepro/scrollandscroll/
├── audio/                          # Quản lý âm thanh ASMR SoundPool
│   └── SoundManager.kt
├── data/
│   ├── model/                      # Các Data Model thuần túy
│   │   ├── SkinItem.kt             # Model thông tin Skin giấy
│   │   ├── BadgeItem.kt            # Model danh hiệu & bằng khen
│   │   ├── GameStats.kt            # Model thống kê mét, số lần vuốt, SPS
│   │   └── LeaderboardUser.kt      # Model người dùng trên bảng xếp hạng
│   ├── preference/
│   │   └── GamePreferences.kt      # Quản lý DataStore Preferences
│   └── repository/
│       ├── GameRepository.kt       # Repository quản lý điểm số & tiến độ
│       └── LeaderboardRepository.kt # Repository quản lý bảng xếp hạng
├── ui/
│   ├── cabinet/                    # Màn hình Tủ Đồ & Danh Hiệu
│   │   ├── CabinetScreen.kt
│   │   ├── components/
│   │   │   ├── SkinCard.kt
│   │   │   └── BadgeCard.kt
│   │   └── CabinetViewModel.kt
│   ├── components/                 # Các UI Components dùng chung (Design System)
│   │   ├── NeoButton.kt            # Nút bấm phong cách Neo-Brutalism
│   │   ├── NeoCard.kt              # Card viền đen đổ bóng cứng
│   │   ├── CircularIconButton.kt   # Nút tròn action góc màn hình
│   │   ├── OdometerText.kt         # Đồng hồ nhảy số kiểu cơ học
│   │   └── CertificateDialog.kt    # Modal Bằng Khen & Share Story
│   ├── game/                       # Màn hình Game chính
│   │   ├── GameScreen.kt
│   │   ├── GameViewModel.kt
│   │   ├── ToiletPaperCanvas.kt    # Trái tim game: Canvas cuộn giấy & Physics
│   │   └── components/
│   │       ├── SpsGauge.kt         # Thanh đo tốc độ vuốt SPS
│   │       └── OverdriveEffect.kt  # Hiệu ứng viền bốc lửa / RGB
│   ├── leaderboard/                # Màn hình Bảng Xếp Hạng
│   │   ├── LeaderboardScreen.kt
│   │   └── LeaderboardViewModel.kt
│   ├── navigation/                 # Quản lý điều hướng màn hình
│   │   ├── AppNavigation.kt
│   │   └── Screen.kt
│   ├── onboarding/                 # Dialog nhập Nickname ban đầu
│   │   └── NicknameDialog.kt
│   ├── settings/                   # Dialog Cài đặt (ASMR, Rung)
│   │   └── SettingsDialog.kt
│   └── theme/                      # Design Tokens (Color, Shape, Type, Theme)
│       ├── Color.kt
│       ├── Type.kt
│       └── Theme.kt
├── util/                           # Các helper & utils
│   ├── HapticManager.kt            # Quản lý rung xúc giác
│   └── ShareHelper.kt              # Chụp bitmap card bằng khen & gọi Share Intent
└── MainActivity.kt
```

### 3. Cấu Hình Dependencies Cần Thiết (Gradle & Libs)
Các thư viện cần khai báo thêm vào `gradle/libs.versions.toml` và `app/build.gradle.kts`:
- **AndroidX Navigation Compose**: Điều hướng màn hình
- **AndroidX DataStore Preferences**: Lưu trữ dữ liệu ngoại tuyến
- **Compose Material Icons Extended**: Bộ icon mở rộng cho Game UI
- **Lifecycle ViewModel Compose**: Quản lý StateFlow trong Compose

### 📋 Checklist Kiểm thử Phase 0 (Verification):
- [ ] File `libs.versions.toml` và `app/build.gradle.kts` đồng bộ không lỗi dependency.
- [ ] Chạy `./gradlew tasks` hoặc `./gradlew assembleDebug` thành công.
- [ ] Cấu trúc thư mục gói (package structure) được tạo đầy đủ.

---

### 🤖 PROMPT MẪU CHO AI THỰC HIỆN PHASE 0:
```markdown
Bạn là Senior Android Developer. Hãy đọc file `kế hoạch kỹ thuật.md` và thực hiện PHASE 0: CẤU HÌNH HẠ TẦNG & DEPENDENCIES cho dự án Scroll & Scroll:
1. Cập nhật `gradle/libs.versions.toml` để thêm các thư viện:
   - `androidx-navigation-compose`
   - `androidx-datastore-preferences`
   - `androidx-compose-material-icons-extended`
   - `androidx-lifecycle-viewmodel-compose`
2. Cập nhật `app/build.gradle.kts` để import các dependencies trên.
3. Tạo khung các thư mục package trống theo đúng cây thư mục Phase 0:
   - `audio`, `data/model`, `data/preference`, `data/repository`
   - `ui/cabinet/components`, `ui/components`, `ui/game/components`, `ui/leaderboard`, `ui/navigation`, `ui/onboarding`, `ui/settings`, `ui/theme`
   - `util`
4. Kiểm tra Gradle sync và đảm bảo dự án build thành công không lỗi syntax/dependency.
```

---

## PHASE 1: FOUNDATION & DESIGN SYSTEM

### 🎯 Mục tiêu:
Xây dựng toàn bộ hệ thống màu sắc (Color Tokens), Typography, các Component dùng chung phong cách **Neo-Brutalism** (viền đen dày $2.5\text{dp}$, đổ bóng cứng $+4\text{dp}$, hiệu ứng lún khi nhấn), cùng bộ quản lý Âm thanh ASMR và Rung Haptic.

### 📁 Danh sách File cần tạo/sửa:
1. `ui/theme/Color.kt` (Khai báo các mã màu `#FFFDF0`, `#1E1E1E`, `#FFDE59`, `#FF5722`, `#00E676`, `#D32F2F`)
2. `ui/theme/Type.kt` (Khai báo Typography to bản phong cách Comic/Arcade)
3. `ui/theme/Theme.kt` (Cấu hình MaterialTheme)
4. `ui/components/NeoButton.kt` (Nút bấm Neo-Brutalism có animation lún $3\text{dp}$ khi ấn)
5. `ui/components/NeoCard.kt` (Card có viền đen dày và bóng đổ cứng)
6. `ui/components/CircularIconButton.kt` (Nút tròn action viền đen)
7. `audio/SoundManager.kt` (Khởi tạo `SoundPool` phát âm thanh sột soạt, click, kèn ăn mừng)
8. `util/HapticManager.kt` (Xử lý rung Tick, Click, Heavy Pulse)

### 📋 Checklist Kiểm thử (Verification):
- [ ] Mở `@Preview` của `NeoButton`, `NeoCard`, `CircularIconButton` trên Android Studio: Hiển thị đúng phong cách Neo-Brutalism.
- [ ] Bấm thử vào `NeoButton`: Nút nảy chuyển động lún $3\text{dp}$ chân thực và phát rung nhẹ.

---

### 🤖 PROMPT MẪU CHO AI THỰC HIỆN PHASE 1:
```markdown
Bạn là lập trình viên Android chuyên nghiệp. Hãy triển khai PHASE 1 cho dự án "Scroll & Scroll" (Endless Toilet Paper) bằng Jetpack Compose:
1. Cập nhật `ui/theme/Color.kt`, `Type.kt`, `Theme.kt` với bảng màu Neo-Brutalism: Nền kem #FFFDF0, Viền đen #1E1E1E, Vàng chính #FFDE59, Cam Overdrive #FF5722, Xanh Mint #00E676, Đỏ Dấu Mộc #D32F2F.
2. Tạo `ui/components/NeoButton.kt`: Nút có viền đen 2.5dp, đổ bóng cứng offset (4dp, 4dp), khi nhấn xuống dịch chuyển (1dp, 1dp) tạo hiệu ứng nảy cơ học.
3. Tạo `ui/components/NeoCard.kt` và `ui/components/CircularIconButton.kt`.
4. Tạo `audio/SoundManager.kt` dùng SoundPool hỗ trợ phát âm thanh ASMR tức thì (roll loop, click, pop, fanfare) với chế độ bật/tắt an toàn.
5. Tạo `util/HapticManager.kt` hỗ trợ rung TICK, CLICK, OVERDRIVE_PULSE.
Đảm bảo code chuẩn Kotlin, không lỗi import, có Preview đầy đủ.
```

---

## PHASE 2: DATA LAYER & STATE MANAGEMENT

### 🎯 Mục tiêu:
Xây dựng tầng lưu trữ dữ liệu bền vững (Offline Data Persistence) bằng DataStore Preferences, tạo các Data Model cho Game, xây dựng `GameRepository` và `GameViewModel` quản lý StateFlow cho toàn bộ logic game.

### 📁 Danh sách File cần tạo/sửa:
1. `data/model/SkinItem.kt` (Enum/Data class 9 loại Skin: Thường, Hoa Hồng, Truyện Tranh, Vàng 24K, Bún Riêu, Đô La, Xác Ướp, Galaxy, Kim Cương kèm mốc mét mở khóa)
2. `data/model/BadgeItem.kt` (Danh sách 8 danh hiệu cà khịa và mốc mở khóa)
3. `data/model/GameStats.kt` (Data class chứa: `totalMeters`, `totalSwipes`, `currentSps`, `currentSkinId`, `isOverdrive`, `nickname`)
4. `data/preference/GamePreferences.kt` (Đọc/ghi DataStore cho: tổng mét, nickname, skin đang chọn, danh sách badge đã mở, cấu hình âm thanh/rung)
5. `data/repository/GameRepository.kt` (Kết nối DataStore và cung cấp Flow cho ViewModel)
6. `ui/game/GameViewModel.kt` (Tính toán tăng mét, kiểm tra mốc mở khóa skin/badge mới, tính SPS theo cửa sổ trượt 1s, kích hoạt Overdrive)

### 📋 Checklist Kiểm thử (Verification):
- [ ] Viết Unit Test hoặc chạy app thử: Tăng số mét $\rightarrow$ Khởi động lại app $\rightarrow$ Số mét và Skin vẫn được lưu chính xác từ DataStore.
- [ ] Khi tổng mét chạm các mốc (VD: $500\text{m}$, $1,000\text{m}$), StateFlow `unlockedBadges` tự động cập nhật danh hiệu mới.

---

### 🤖 PROMPT MẪU CHO AI THỰC HIỆN PHASE 2:
```markdown
Hãy triển khai PHASE 2: DATA LAYER & STATE MANAGEMENT cho dự án "Scroll & Scroll":
1. Cấu hình DataStore Preferences trong `data/preference/GamePreferences.kt` để lưu: totalMeters (Double), totalSwipes (Long), nickname (String), selectedSkinId (String), soundEnabled (Boolean), hapticEnabled (Boolean), unlockedBadges (Set<String>).
2. Tạo các Model: `SkinItem.kt` (9 loại skin với requiredMeters từ 0 đến 999,999m), `BadgeItem.kt` (8 danh hiệu hài hước), `GameStats.kt`.
3. Tạo `GameRepository.kt` cung cấp Data Streams qua Kotlin Flow.
4. Tạo `GameViewModel.kt`:
   - Hàm `addScrollDistance(pixels: Float)`: Quy đổi pixel thành mét, cập nhật tổng khoảng cách, lưu DataStore (có debounce định kỳ).
   - Hàm tính `currentSps` (Swipes Per Second): Đếm số cú vuốt trong 1 giây gần nhất. Nếu SPS >= 8.0 thì bật `isOverdrive = true` (nhân 1.5x mét).
   - StateFlow `uiState` phát ra trạng thái game toàn diện cho UI.
```

---

## PHASE 3: CORE GAMEPLAY - CANVAS CUỘN GIẤY & VẬT LÝ CUỘN

### 🎯 Mục tiêu:
Xây dựng "trái tim" của trò chơi: `ToiletPaperCanvas` bằng Compose Canvas với cử chỉ vuốt nhạy bén, cuộn giấy xoay mượt mà 60-120fps, dải giấy rơi uốn lượn, thanh đo SPS và hiệu ứng viền bốc lửa Overdrive rực rỡ.

### 📁 Danh sách File cần tạo/sửa:
1. `ui/game/ToiletPaperCanvas.kt` (Vẽ cuộn giấy 2D/3D + Dải giấy trôi rơi bằng Canvas, bắt cử chỉ `pointerInput` kéo Drag 1:1 và vuốt văng Fling có quán tính ma sát)
2. `ui/game/components/SpsGauge.kt` (Thanh đo tốc độ vuốt đổi màu Xanh $\rightarrow$ Vàng $\rightarrow$ Đỏ rực)
3. `ui/game/components/OverdriveEffect.kt` (Hiệu ứng viền màn hình bốc lửa và tia sáng RGB khi vào chế độ Overdrive)
4. `ui/components/OdometerText.kt` (Đồng hồ đếm số mét phong cách cơ học nhảy số mượt mà)
5. `ui/game/GameScreen.kt` (Lắp ráp Màn hình Game chính: Header, Odometer, Canvas cuộn giấy, SPS Gauge, Nút ASMR/Bằng khen)

### 📋 Checklist Kiểm thử (Verification):
- [ ] Chạy app trên máy thật/máy ảo: Chạm tay vuốt cuộn giấy xoay mượt mà theo ngón tay.
- [ ] Thả tay nhanh (Fling): Cuộn giấy tiếp tục quay thêm một đoạn theo quán tính rồi dừng êm ái.
- [ ] Âm thanh ASMR xột xoạt phát ra liên tục và khớp với tốc độ vuốt.
- [ ] Dùng 2 ngón tay vuốt liên thanh đạt $>8\text{ SPS}$: Màn hình bùng nổ hiệu ứng lửa Overdrive, số mét nhảy siêu nhanh.

---

### 🤖 PROMPT MẪU CHO AI THỰC HIỆN PHASE 3:
```markdown
Hãy triển khai PHASE 3: CORE GAMEPLAY & CANVAS CUỘN GIẤY cho dự án "Scroll & Scroll":
1. Tạo `ui/game/ToiletPaperCanvas.kt`:
   - Dùng Compose `Canvas` vẽ: Trục treo cuộn giấy, Cuộn giấy hình trụ tròn với các lớp giấy xếp tầng, Dải giấy rủ xuống uốn lượn chân thực.
   - Nhận diện cử chỉ qua `pointerInput`: Hỗ trợ Drag (kéo tay 1:1) và Fling (quán tính vuốt văng có ma sát làm chậm dần).
   - Mỗi lần cuộn giấy di chuyển: Gọi callback cập nhật số mét và kích hoạt âm thanh ASMR + Rung haptic nhẹ.
2. Tạo `ui/game/components/SpsGauge.kt` hiển thị thanh nhiệt huyết vuốt.
3. Tạo `ui/game/components/OverdriveEffect.kt` vẽ hiệu ứng viền lửa/hào quang RGB quanh màn hình khi `isOverdrive == true`.
4. Hoàn thiện `ui/game/GameScreen.kt` kết nối với `GameViewModel`.
```

---

## PHASE 4: HỆ THỐNG TIẾN HÓA SKIN & TỦ ĐỒ

### 🎯 Mục tiêu:
Xây dựng màn hình Tủ Đồ (`CabinetScreen`) với 2 tab: Tab 1 hiển thị lưới 9 loại Skin giấy vệ sinh (kèm trạng thái Đang Dùng / Mở Khóa / Đang Khóa + Thanh % tiến độ); Tab 2 hiển thị Danh Sách Bằng Khen. Cho phép chọn đổi Skin trực tiếp.

### 📁 Danh sách File cần tạo/sửa:
1. `ui/cabinet/components/SkinCard.kt` (Thẻ hiển thị skin phong cách Neo-Brutalism với 3 trạng thái rõ ràng)
2. `ui/cabinet/components/BadgeCard.kt` (Thẻ danh hiệu hiển thị huy hiệu và câu cà khịa hài hước)
3. `ui/cabinet/CabinetViewModel.kt` (Quản lý danh sách skin, badge và xử lý chọn skin)
4. `ui/cabinet/CabinetScreen.kt` (Giao diện 2 tab với hiệu ứng chuyển động mượt mà)

### 📋 Checklist Kiểm thử (Verification):
- [ ] Mở màn hình Cabinet: Các skin chưa đủ mét hiển thị icon ổ khóa 🔒 và thanh % tiến độ (VD: `420m / 500m`).
- [ ] Skin đã đủ mét: Có nút "SỬ DỤNG". Khi bấm chọn $\rightarrow$ Quay lại GameScreen, dải giấy trên Canvas lập tức đổi màu/họa tiết sang Skin mới (VD: Giấy Hoa Hồng hoặc Giấy Dát Vàng).

---

### 🤖 PROMPT MẪU CHO AI THỰC HIỆN PHASE 4:
```markdown
Hãy triển khai PHASE 4: HỆ THỐNG TIẾN HÓA SKIN & TỦ ĐỒ cho dự án "Scroll & Scroll":
1. Tạo `ui/cabinet/components/SkinCard.kt`: Hiển thị thông tin Skin (Tên, Mốc mở khóa, Hình ảnh preview, Hiệu ứng). Có 3 trạng thái: [ĐANG DÙNG] (viền xanh lá), [SỬ DỤNG] (nút vàng), [KHÓA 🔒] (nền xám + thanh progress bar % số mét còn thiếu).
2. Tạo `ui/cabinet/components/BadgeCard.kt`: Hiển thị danh hiệu trào phúng.
3. Tạo `ui/cabinet/CabinetViewModel.kt` và `CabinetScreen.kt` có 2 Tab: "Skin Giấy" & "Tủ Danh Hiệu".
4. Đảm bảo khi người dùng bấm chọn 1 skin đã mở khóa, skin đó được lưu vào DataStore và cập nhật tức thì ra màn hình Canvas chính.
```

---

## PHASE 5: BẰNG KHEN VIRAL & TRÌNH CHIA SẺ STORY

### 🎯 Mục tiêu:
Xây dựng Modal Bằng Khen (`CertificateDialog`) thiết kế phong cách "Viện Hàn Lâm Khoa Học Vô Tri" đóng dấu đỏ 100% Vô Tri, tích hợp logic render Card thành file ảnh Bitmap tỉ lệ 9:16 và gọi Android Share Sheet để người dùng đăng thẳng lên Story TikTok, Instagram, Facebook.

### 📁 Danh sách File cần tạo/sửa:
1. `ui/components/CertificateDialog.kt` (Giao diện bằng khen sang trọng châm biếm: Viền hoa văn vàng kim, Tên người dùng, Danh hiệu, Số mét, Con dấu đỏ, Nút Share)
2. `util/ShareHelper.kt` (Chuyển đổi Compose View thành Bitmap PNG độ phân giải cao, lưu vào cache và kích hoạt `Intent.ACTION_SEND` với URI qua FileProvider)
3. `app/src/main/res/xml/file_paths.xml` (Cấu hình cache path cho FileProvider)
4. `AndroidManifest.xml` (Khai báo `androidx.core.content.FileProvider`)

### 📋 Checklist Kiểm thử (Verification):
- [ ] Chạm mốc mét mới (VD: $1,000\text{m}$): Bằng khen tự động bật lên kèm tiếng kèn ăn mừng Fanfare.
- [ ] Bấm nút **[📲 CHIA SẺ NGAY LÊN STORY]**: Hệ thống mở khay chia sẻ của Android, chọn gửi sang ứng dụng mạng xã hội hoặc lưu ảnh thành công.

---

### 🤖 PROMPT MẪU CHO AI THỰC HIỆN PHASE 5:
```markdown
Hãy triển khai PHASE 5: BẰNG KHEN VIRAL & TRÌNH CHIA SẺ STORY cho dự án "Scroll & Scroll":
1. Tạo `ui/components/CertificateDialog.kt`:
   - Thiết kế tấm bằng khen trang trọng hài hước tỷ lệ 9:16 (Viền mạ vàng, Quốc hiệu "Cộng Hòa Vô Tri", Tên nickname, Danh hiệu mở khóa, Thành tích tiêu tốn X mét giấy, Con dấu đỏ "CHỨNG NHẬN 100% VÔ TRI", Mã QR).
   - Nút bấm [📲 CHIA SẺ NGAY LÊN STORY] và nút [💾 LƯU ẢNH HD].
2. Tạo `util/ShareHelper.kt`:
   - Hàm `shareCertificate(context, bitmap)`: Lưu bitmap vào cache directory, lấy Content URI qua FileProvider, bắn `Intent.ACTION_SEND` dạng `image/png`.
3. Cấu hình `AndroidManifest.xml` và `res/xml/file_paths.xml` cho FileProvider đầy đủ.
```

---

## PHASE 6: BẢNG XẾP HẠNG TOÀN CẦU (LEADERBOARD SCREEN)

### 🎯 Mục tiêu:
Xây dựng màn hình Bảng Xếp Hạng (`LeaderboardScreen`) với 3 tab (Hôm Nay, Toàn Cầu, Đại Chiến Quốc Gia), hỗ trợ hiển thị Top 100 người cuộn nhiều nhất, cờ quốc gia, thẻ Rank cá nhân ghim cố định ở đáy màn hình, và kiến trúc Repository sẵn sàng kết nối Firebase Firestore / REST API.

### 📁 Danh sách File cần tạo/sửa:
1. `data/model/LeaderboardUser.kt` (Model: rank, nickname, countryCode, totalMeters, skinId, badgeTitle)
2. `data/repository/LeaderboardRepository.kt` (Cung cấp mock data chuẩn & interface kết nối Firebase/Backend)
3. `ui/leaderboard/LeaderboardViewModel.kt` (Quản lý tab, search nickname, refresh danh sách)
4. `ui/leaderboard/LeaderboardScreen.kt` (Giao diện danh sách Top 100 với hiệu ứng vương miện vàng cho Top 1, bạc Top 2, đồng Top 3, thanh Rank cá nhân ở đáy)

### 📋 Checklist Kiểm thử (Verification):
- [ ] Mở màn hình Bảng Xếp Hạng: Danh sách tải nhanh, cuộn mượt mà.
- [ ] Thẻ Rank của chính mình luôn hiển thị chính xác số mét hiện tại ở thanh đáy.
- [ ] Chuyển đổi giữa 3 Tab "Hôm Nay", "Toàn Cầu", "Quốc Gia" trơn tru.

---

### 🤖 PROMPT MẪU CHO AI THỰC HIỆN PHASE 6:
```markdown
Hãy triển khai PHASE 6: BẢNG XẾP HẠNG TOÀN CẦU cho dự án "Scroll & Scroll":
1. Tạo `data/model/LeaderboardUser.kt` và `LeaderboardRepository.kt` (hỗ trợ cả mock data phong phú lẫn phương thức sync điểm lên Server).
2. Tạo `ui/leaderboard/LeaderboardViewModel.kt`.
3. Tạo `ui/leaderboard/LeaderboardScreen.kt`:
   - 3 Tab: "Hôm Nay (Daily)", "Toàn Cầu (All-Time)", "Đại Chiến Quốc Gia (Popcat style)".
   - Danh sách Card người dùng phong cách Neo-Brutalism, hiển thị Top 1 Vương miện vàng, cờ quốc gia, nickname, skin tag, số mét.
   - Thẻ Rank cá nhân ghim ở đáy: "Vị trí của bạn: #Rank - X mét (Vuốt thêm Y mét để lên Top tiếp theo)".
```

---

## PHASE 7: NAVIGATION, POLISHING & BUILD RELEASE

### 🎯 Mục tiêu:
Hoàn thiện kiến trúc điều hướng toàn diện với Compose Navigation, tích hợp Dialog Onboarding nhập Nickname lần đầu, Dialog Cài Đặt (ASMR, Rung), xử lý nút Back hệ thống (troll người dùng khi thoát app), tối ưu hiệu năng mượt mà 120fps và kiểm tra bản build release APK / AAB.

### 📁 Danh sách File cần tạo/sửa:
1. `ui/navigation/Screen.kt` (Khai báo Sealed class các route màn hình: `Game`, `Leaderboard`, `Cabinet`)
2. `ui/navigation/AppNavigation.kt` (NavHost kết nối các màn hình với hiệu ứng chuyển cảnh Spring Slide)
3. `ui/onboarding/NicknameDialog.kt` (Dialog nhập tên bựa có nút xúc xắc 🎲 random tên)
4. `ui/settings/SettingsDialog.kt` (Cài đặt bật/tắt ASMR, Rung, đổi Nickname)
5. `MainActivity.kt` (Khởi chạy AppNavigation và kích hoạt Edge-to-Edge)

### 📋 Checklist Kiểm thử Toàn diện (Final Verification):
- [ ] Cài mới app: Hiện dialog nhập Nickname $\rightarrow$ Nhập tên xong vào thẳng Game.
- [ ] Vuốt cuộn giấy: Âm thanh ASMR, rung haptic, số mét nhảy chuẩn.
- [ ] Chuyển qua lại giữa Game, Leaderboard, Tủ Đồ Skin mượt mà không giật lag.
- [ ] Chạm mốc mét mới: Bằng khen tự động nổ ra, share story bình thường.
- [ ] Nhấn nút Back ở màn hình chính: Hiện Toast hài hước *"Bạn định bỏ cuộc thật à? Cuộn nốt 100m nữa đi!"*.
- [ ] Chạy `./gradlew assembleDebug` hoặc `assembleRelease` thành công không lỗi.

---

### 🤖 PROMPT MẪU CHO AI THỰC HIỆN PHASE 7:
```markdown
Hãy triển khai PHASE 7: NAVIGATION & HOÀN THIỆN DỰ ÁN cho "Scroll & Scroll":
1. Cấu hình `ui/navigation/Screen.kt` và `AppNavigation.kt` dùng Compose Navigation với hiệu ứng slide ngang mượt mà.
2. Tạo `ui/onboarding/NicknameDialog.kt` (bật lên nếu chưa có nickname) và `ui/settings/SettingsDialog.kt`.
3. Cập nhật `MainActivity.kt` khởi chạy toàn bộ flow hoàn chỉnh.
4. Xử lý BackHandler tại màn hình chính để hiện Toast troll khi người dùng bấm Back.
5. Kiểm tra build dự án để đảm bảo ứng dụng sẵn sàng xuất xưởng.
```

---

## 🏁 BẢNG THEO DÕI TIẾN ĐỘ THỰC HIỆN (PROGRESS TRACKER)

| Giai đoạn (Phase) | Nội dung công việc chính | Trạng thái | Ghi chú & Đánh giá |
| :--- | :--- | :--- | :--- |
| **Phase 0** | Cấu hình hạ tầng, Gradle Libs & Packages | 🟢 **ĐÃ HOÀN TẤT** | Đã cấu hình `libs.versions.toml`, `app/build.gradle.kts`, tạo package structure, build thành công 100%. |
| **Phase 1** | Foundation, Material Design 3, Sound & Haptics | 🟢 **ĐÃ HOÀN TẤT** | Áp dụng 100% chuẩn Material Design 3 (M3 Color Tokens, M3 Typography 15 cấp độ, M3PrimaryButton, M3ElevatedCard, M3IconButton, SoundManager SoundPool, HapticManager). Build pass 100%. |
| **Phase 2** | Data Layer, DataStore, Models & GameViewModel | 🟡 **TIẾP THEO (SẴN SÀNG)** | Bước tiếp theo cần triển khai. |
| **Phase 3** | Core Gameplay, Canvas Cuộn Giấy & Overdrive Physics | ⚪ *Chờ thực thi* | Phụ thuộc Phase 2. |
| **Phase 4** | Hệ thống Tiến Hóa 9 Skin & Tủ Đồ Cabinet | ⚪ *Chờ thực thi* | Phụ thuộc Phase 3. |
| **Phase 5** | Bằng Khen Vô Tri & Trình Xuất Ảnh Share Story | ⚪ *Chờ thực thi* | Phụ thuộc Phase 4. |
| **Phase 6** | Bảng Xếp Hạng Toàn Cầu & Đại Chiến Quốc Gia | ⚪ *Chờ thực thi* | Phụ thuộc Phase 5. |
| **Phase 7** | App Navigation, Polishing & Build Xuất Xưởng | ⚪ *Chờ thực thi* | Phụ thuộc Phase 6. |

---

## 📝 NHẬT KÝ THỰC THI (EXECUTION CHANGELOG & AUDIT TRAIL)
> **Mục này ghi lại lịch sử các bước đã làm để bất kỳ AI Agent nào tiếp quản dự án đều nắm rõ hiện trạng.**

### 📌 [2026-08-19] - Hoàn Tất Phase 0 & Phase 1:
1. **Phase 0 (Infrastructure & Dependencies)**:
   - Thêm `androidx-navigation-compose` (2.8.8), `androidx-datastore-preferences` (1.1.3), `androidx-compose-material-icons-extended`, `androidx-lifecycle-viewmodel-compose` vào `gradle/libs.versions.toml` và `app/build.gradle.kts`.
   - Kết quả biên dịch: `./gradlew compileDebugSources` $\rightarrow$ **BUILD SUCCESSFUL**.
   - Git Commit: `8f351e7` đã push lên branch `main`.

2. **Phase 1 (Material Design 3 & Sound/Haptics)**:
   - Chuyển đổi toàn bộ Theme sang chuẩn **Material Design 3 (Material You)** của Google.
   - `ui/theme/Color.kt`: Khai báo đầy đủ M3 Tonal Palette (`Primary`, `Secondary`, `Tertiary`, `Surface`, `Background`, `Outline` cho cả Light & Dark theme).
   - `ui/theme/Type.kt`: Cung cấp 15 cấp độ Typography M3 (`displayLarge`, `headline`, `title`, `body`, `label`).
   - `ui/theme/Theme.kt`: Cấu hình `MaterialTheme` M3 với Window Insets controller an toàn cho Edge-to-Edge.
   - `ui/components/M3Components.kt`: Các component tái sử dụng M3 (`M3PrimaryButton`, `M3TonalButton`, `M3OutlinedButton`, `M3ElevatedCard`, `M3IconButton`).
   - `audio/SoundManager.kt`: Engine âm thanh ASMR độ trễ thấp bằng `SoundPool` với bộ tổng hợp sóng âm PCM (roll, click, pop, fanfare, overdrive).
   - `util/HapticManager.kt`: Quản lý rung xúc giác nhẹ (Tick, Click, Heavy Pulse).
   - `ui/components/DesignSystemPreview.kt`: Màn hình Showcase hỗ trợ Preview cả Light & Dark mode trên Android Studio.
   - `AndroidManifest.xml`: Đã khai báo quyền `VIBRATE`.
   - Kết quả biên dịch: `./gradlew compileDebugSources` $\rightarrow$ **BUILD SUCCESSFUL (0 errors, 0 warnings)**.

3. **Hướng dẫn cho AI tiếp theo**:
   - **Bước tiếp theo là Phase 2**: Tạo các Data Model trong `data/model/`, DataStore Preferences trong `data/preference/GamePreferences.kt`, `data/repository/GameRepository.kt`, và `ui/game/GameViewModel.kt`. Xem chi tiết tại mục [PHASE 2](#phase-2-data-layer--state-management).

---
*Tài liệu Kế hoạch Kỹ thuật dự án Scroll & Scroll. Bất kỳ AI Agent nào cũng có thể đọc tài liệu này và tiếp tục thực hiện.*
