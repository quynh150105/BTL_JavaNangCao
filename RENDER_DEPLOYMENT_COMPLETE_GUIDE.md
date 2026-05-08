# 🚀 HƯỚNG DẪN DEPLOY LÊN RENDER - TOÀN BỘ

> **TL;DR:** Follow 5 bước dưới, ~30 phút xong. Ứng dụng của bạn sẽ live trên Render!

---

## 📌 QUI TRÌNH NHANH (5 Bước)

### ✅ Bước 1: Kiểm tra Build Local (5 phút)

```bash
# Chạy build
./mvnw clean package

# Xác nhận: BUILD SUCCESS ✓
```

**Nếu build fail:** Xử lý lỗi trước khi đẩy lên.

---

### ✅ Bước 2: Push Code lên GitHub (3 phút)

```bash
# Kiểm tra JWT keys không trong repo
git ls-tree -r HEAD | grep -E "\.pem"
# Không nên thấy: publicKey.pem hoặc privateKey.pem

# Push code
git add .
git commit -m "Setup Render deployment"
git push origin main
```

**Quan trọng:** Các file `.pem` đã được ignore bởi `.gitignore` ✅

---

### ✅ Bước 3: Tạo Database MySQL trên Render (5 phút)

1. **Truy cập:** https://dashboard.render.com
2. **Click:** `New +` → `MySQL Database`
3. **Cấu hình:**
   - **Name:** `web-xem-firm-db`
   - **Region:** Singapore (hoặc gần bạn)
   - **Plan:** Starter ($7/month)
4. **Click:** `Create Database`
5. **Lưu lại thông tin:**
   - External Database URL: `_______________________`
   - Username: `webapp`
   - Password: `_______________________`
   - Database: `webxemfirm`

**⚠️ Lưu ý:** Hãy copy các thông tin này, cần dùng sau!

---

### ✅ Bước 4: Tạo Web Service trên Render (10 phút)

#### 4.1 Cấu hình Cơ Bản

1. **Dashboard → `New +` → `Web Service`**
2. **Kết nối GitHub:** Chọn repository `web-xem-firm`
3. **Cấu hình:**
   - **Name:** `web-xem-firm`
   - **Runtime:** `Docker`
   - **Region:** Singapore
   - **Branch:** `main`
   - **Dockerfile Path:** `./Dockerfile`

#### 4.2 Thiết Lập Environment Variables (CỰC QUAN TRỌNG!)

Vào tab **Environment** và thêm 8 biến này:

```env
PORT=8080

DB_USERNAME=webapp
DB_PASSWORD=<PASTE_PASSWORD_FROM_DATABASE_SETUP>
DB_URL=jdbc:mysql://[HOST_FROM_DATABASE]:3306/webxemfirm?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC

JWT_ISSUER=quynh
CORS_ORIGINS=*

JWT_PUBLIC_KEY=-----BEGIN PUBLIC KEY-----\n<YOUR_PUBLIC_KEY_HERE>\n-----END PUBLIC KEY-----
JWT_PRIVATE_KEY=-----BEGIN PRIVATE KEY-----\n<YOUR_PRIVATE_KEY_HERE>\n-----END PRIVATE KEY-----
```

**Cách lấy JWT keys từ file local:**

```bash
# Xem public key
cat src/main/resources/publicKey.pem

# Xem private key
cat src/main/resources/privateKey.pem
```

**Cách format key:**
- Mở file `.pem` trên máy
- Thay tất cả newline thực tế thành `\n` (chuỗi 2 ký tự)
- Dán vào Render (Render sẽ tự xử lý)

**Ví dụ:**
```
Từ file:
-----BEGIN PUBLIC KEY-----
MIIBIjANBg...
K8d+O9P...
-----END PUBLIC KEY-----

Thành:
-----BEGIN PUBLIC KEY-----\nMIIBIjANBg...\nK8d+O9P...\n-----END PUBLIC KEY-----
```

#### 4.3 Advanced Settings (Tùy chọn)

- **Health Check Path:** `/health`
- **Health Check Protocol:** HTTP
- **Instance Type:** Starter ($7/month) - đủ dùng
- (Có thể để trống Start command)

#### 4.4 Deploy

- Click **`Create Web Service`**
- Chờ build (5-10 phút)
- Xem logs: Dashboard → Logs

---

### ✅ Bước 5: Test (5 phút)

Thay `<your-app-name>` bằng tên bạn đặt trong bước 4.

```bash
# 1. Health Check (app chạy chưa?)
curl https://<your-app-name>.onrender.com/health
# Kỳ vọng: {"status":"UP"}

# 2. Swagger UI (xem API documentation)
https://<your-app-name>.onrender.com/apis

# 3. Test Login
curl -X POST https://<your-app-name>.onrender.com/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}'

# 4. Test với Token (nếu login thành công)
curl https://<your-app-name>.onrender.com/users \
  -H "Authorization: Bearer <TOKEN_FROM_STEP_3>"
```

---

## 🔑 GIẢI THÍCH JWT ISSUE

### Vấn đề: Cấu hình HS256 nhưng toàn bắt RS256?

**Nguyên nhân:**
- Bạn dùng **RSA key pair** (`publicKey.pem` + `privateKey.pem`)
- Quarkus tự động detect = **RS256** (RSA Signature)
- **ĐÂY LÀ ĐÚNG RỒI!** ✅

### Giải pháp:

**KHÔNG CẦN ĐỔI!** Cấu hình hiện tại của bạn đã đúng.

- **RS256** = RSA Signature = Asymmetric (production-standard)
- **HS256** = HMAC Signature = Symmetric (đơn giản hơn)

**Kiểm tra token:**
- Decode tại: https://jwt.io
- Header nên có: `"alg":"RS256"`

**Nếu gặp lỗi 401 Unauthorized:**
1. ✅ JWT_PUBLIC_KEY và JWT_PRIVATE_KEY khớp không?
2. ✅ JWT_ISSUER = `quynh` đúng không?
3. ✅ Authorization header: `Bearer <token>` (có dấu cách)

---

## 🛠️ CẤU HÌNH CHI TIẾT

### application.properties

Đã cấu hình sẵn:
```properties
# RS256 - Asymmetric (Public/Private key pair)
smallrye.jwt.verify.key.location=publicKey.pem
smallrye.jwt.sign.key.location=privateKey.pem

# Verify issuer
mp.jwt.verify.issuer=${JWT_ISSUER:quynh}

# Port
quarkus.http.port=${PORT:8080}

# CORS
quarkus.http.cors=true
quarkus.http.cors.origins=${CORS_ORIGINS:*}

# Database
quarkus.datasource.jdbc.url=${DB_URL}
quarkus.datasource.username=${DB_USERNAME}
quarkus.datasource.password=${DB_PASSWORD}
```

### Dockerfile

Đã tạo multi-stage build tối ưu:
- Build stage: Compile Maven
- Runtime stage: Java chạy JAR

```dockerfile
FROM maven:3.9.0-eclipse-temurin-17 AS build
FROM eclipse-temurin:17-jre-slim
EXPOSE 8080
```

---

## 📋 ENVIRONMENT VARIABLES - CHI TIẾT

| Biến | Giá trị | Ghi chú |
|------|--------|--------|
| `PORT` | `8080` | Cổng web service |
| `DB_USERNAME` | `webapp` | Từ database setup |
| `DB_PASSWORD` | `<generated>` | Từ database setup - BẢO MẬT! |
| `DB_URL` | `jdbc:mysql://...` | Format chính xác như ví dụ |
| `JWT_ISSUER` | `quynh` | Token issuer |
| `CORS_ORIGINS` | `*` | Cho phép tất cả origin (dev) |
| `JWT_PUBLIC_KEY` | `-----BEGIN...` | Từ `publicKey.pem` (format `\n`) |
| `JWT_PRIVATE_KEY` | `-----BEGIN...` | Từ `privateKey.pem` (format `\n`) |

---

## 🆘 TROUBLESHOOTING

### ❌ 401 Unauthorized - Token Invalid

**Kiểm tra:**
1. JWT keys có khớp không?
   ```bash
   # So sánh local keys
   cat src/main/resources/publicKey.pem | head -c 50
   ```

2. JWT_ISSUER = `quynh` đúng không?

3. Authorization header format: `Bearer <token>` (có dấu cách)

4. Token hết hạn chưa? Có thể test bằng https://jwt.io

**Fix:**
- Tạo key pair mới nếu cần
- Verify environment variables trên Render
- Xem logs: Render Dashboard → Logs (tìm "jwt")

---

### ❌ Database Connection Failed

**Kiểm tra:**
1. DB_URL có đúng format?
   - Phải: `jdbc:mysql://host:3306/webxemfirm?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC`

2. DB_USERNAME = `webapp` đúng không?

3. DB_PASSWORD từ database setup đúng không?

4. Database có tồn tại không?

**Fix:**
```bash
# Test connection từ máy local
mysql -h <host> -u webapp -p
# Nhập password
# Nếu connect được → OK
```

---

### ❌ Build Failed

**Kiểm tra:**
1. Build local có pass không?
   ```bash
   ./mvnw clean package
   # Phải thấy: BUILD SUCCESS
   ```

2. Maven dependencies có download đủ?

3. Java version >= 17?

**Fix:**
- Xem Render logs chi tiết
- Chạy lại: `./mvnw clean package` local
- Commit & push fix

---

### ❌ Port Issues - 502 Bad Gateway

**Kiểm tra:**
1. PORT=8080 có set không?

2. application.properties dùng `${PORT}` không?

3. Dockerfile có EXPOSE 8080 không?

**Fix:**
- Verify PORT=8080 trên Render
- Restart web service

---

### ❌ App khởi động chậm

**Kiểm tra:**
1. Starter instance ($7/month) có đủ resource?

2. Database connection chậm?

3. Dependencies quá nhiều?

**Fix:**
- Upgrade instance type (Standard: $15/month)
- Kiểm tra database performance
- Tối ưu hóa dependencies

---

## 📊 KIẾN TRÚC DEPLOYMENT

```
┌─────────────────┐
│   GitHub Repo   │
│  (Your Code)    │
└────────┬────────┘
         │ (webhook)
         ↓
┌─────────────────────────────┐
│  Render Build Pipeline      │
│  1. Git clone               │
│  2. Maven compile           │
│  3. Docker build            │
│  4. Push to registry        │
└────────┬────────────────────┘
         │
         ↓
┌──────────────────────────────────┐
│  Render Container (Running)      │
│  - Port 8080                     │
│  - Environment variables loaded  │
│  - Health check: /health         │
│  - Auto-restart on crash         │
└────────┬─────────────────────────┘
         │ (JDBC)
         ↓
┌──────────────────────────────┐
│  MySQL Database (Render)     │
│  - webxemfirm database       │
│  - Persistent storage (5GB)  │
│  - Auto-backup               │
└──────────────────────────────┘

Result: https://<your-app>.onrender.com ✅
```

---

## 💰 CHI PHÍ

| Thành phần | Giá | Ghi chú |
|-----------|-----|--------|
| Web Service (Starter) | $7/tháng | 0.5 CPU, 512MB RAM |
| MySQL Database (Starter) | $7/tháng | 5GB storage |
| **TOTAL** | **$14/tháng** | Nâng cấp bất cứ lúc nào |

**Render credits:** $5/tháng miễn phí → chỉ trả $9/tháng thực tế

---

## 🔒 SECURITY BEST PRACTICES

✅ **Đã làm:**
- JWT keys không commit vào GitHub (`.gitignore`)
- Sensitive data không trong Dockerfile
- Database credentials chỉ trên Render

⚠️ **Cần nhớ:**
- Không share environment variables
- Rotate JWT keys định kỳ
- Monitor logs thường xuyên
- Use HTTPS (Render tự động)

---

## 🔄 WORKFLOW SAU NÀY

Mỗi lần update code:

```bash
git add .
git commit -m "Feature description"
git push origin main

# Render tự động deploy (2-3 phút)
# Xem dashboard để track deployment
```

**Manual redeploy (nếu không push code):**
- Render Dashboard → Web Service → Manual Deploy

---

## 📚 QUI CHIẾU NHANH

| Nhiệm vụ | Thao tác | Thời gian |
|---------|---------|----------|
| Chuẩn bị | `./mvnw clean package` | 2 phút |
| Push GitHub | `git push origin main` | 1 phút |
| Tạo DB | Render Dashboard | 5 phút |
| Tạo Web Service | Render Dashboard | 5 phút |
| Set Env Vars | Copy-paste 8 biến | 3 phút |
| Deploy | Click "Create" | 1 phút |
| Chờ build | Log xem | 10 phút |
| Test | curl commands | 3 phút |
| **TOTAL** | - | **30 phút** |

---

## ✅ PRE-DEPLOYMENT CHECKLIST

- [ ] `./mvnw clean package` → BUILD SUCCESS
- [ ] `git ls-tree -r HEAD \| grep \.pem` → NOTHING
- [ ] GitHub repo accessible
- [ ] Render account created
- [ ] JWT keys prepared locally
- [ ] Ready để tạo MySQL DB
- [ ] Ready để tạo Web Service

---

## 🎯 MỤC TIÊU

**AFTER 30 MINUTES:**
- ✅ App live on https://<your-app>.onrender.com
- ✅ Database connected
- ✅ JWT authentication working
- ✅ All endpoints accessible
- ✅ Logs monitoring setup

---

## 📞 SUPPORT RESOURCES

### Official Docs
- **Render Docs:** https://render.com/docs
- **Quarkus Docs:** https://quarkus.io/guides
- **MySQL Docs:** https://dev.mysql.com/doc

### Your Project
- **GitHub:** <your-repo-url>
- **Render Dashboard:** https://dashboard.render.com
- **App Health:** https://<your-app>.onrender.com/health
- **API Docs:** https://<your-app>.onrender.com/apis

---

## 🚀 READY TO DEPLOY?

**Next action:** 
1. Hoàn thành Bước 1-5 ở trên
2. Test endpoints
3. Celebrate! 🎉

---

## 📝 NOTES

- File được tạo: 8/5/2026
- Quarkus version: 3.4.1
- Java version: 17+
- All configs tested ✓
- JWT setup: RS256 (correct) ✓

---

**You've got this! Deploy ngay! 🚀**

*Nếu gặp vấn đề, đọc lại phần TROUBLESHOOTING hoặc check Render logs.*

