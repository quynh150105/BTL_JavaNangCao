# 🚀 DEPLOYMENT GUIDE - OPhim Full Support Integration

## ✅ PRE-DEPLOYMENT CHECKLIST

- [x] Tất cả code đã compile không có lỗi
- [x] Thêm @JsonProperty mapping cho OPhim API fields
- [x] Entity được cập nhật với các columns mới
- [x] DTOs được cập nhật với các fields bổ sung
- [x] Services được cập nhật để map đầy đủ dữ liệu
- [ ] Database migration đã prepare
- [ ] Local testing hoàn thành
- [ ] Git commits đã ready

---

## 📝 DEPLOY STEPS

### 1. LOCAL TESTING (BEFORE PUSH)

```bash
# 1a. Clean build
mvn clean install

# 1b. Run dev mode
mvn quarkus:dev

# 1c. Test endpoints
# - POST /movies/favorites/{slug} (add to favorites)
# - GET /movies/favorites (list favorites)
# - GET /movies/detail/{slug} (movie detail)
# - GET /movies (movie list)

# 1d. Verify response includes:
# ✓ view, cinemaScreen, actors, directors, exclusiveSubtitle
# ✓ episodes with serverData
# ✓ categories, countries
```

### 2. COMMIT & PUSH

```bash
# 2a. Stage changes
git add .

# 2b. Commit with message
git commit -m "✨ feat: OPhim full API support

- Add @JsonProperty mapping for snake_case JSON fields
- Support new fields: view, cinemaScreen, actors, directors, exclusiveSubtitle, notify
- Implement episode data mapping with server links
- Update DTOs to include all OPhim fields
- Enhance MovieService & FavoriteService mappers
- Add ExternalEpisodeDTO for episode structure"

# 2c. Push to GitHub
git push origin main
```

### 3. RENDER DEPLOYMENT

**Render tự động trigger deployment khi push to main**

```bash
# Monitor deployment logs
# https://dashboard.render.com → Your Service → Logs
```

### 4. DATABASE MIGRATION

#### Option A: Auto-schema (Quarkus Hibernate DDL)

**Quarkus sẽ tự động tạo columns nếu config:**

```properties
# application-prod.properties
quarkus.hibernate-orm.database.generation=create  # hoặc update, validate
```

#### Option B: Manual SQL Execution

Nếu dùng Render PostgreSQL:

```bash
# 1. Connect to Render PostgreSQL
# - Get connection string từ Render Dashboard
# - Hoặc dùng Render PostgreSQL GUI

# 2. Execute migration script
# - Chạy: db/migrations/V1__Add_OPhim_Full_Support.sql
# - Hoặc import file
```

#### Option C: Flyway (Recommended)

```bash
# Config trong pom.xml đã có
# Flyway tự động scan db/migration/ folder
# File naming: V{version}__{description}.sql
```

---

## 🔍 POST-DEPLOYMENT VERIFICATION

### 1. Check Application Status

```bash
# Verify app is running
curl https://your-app.onrender.com/health
# Expected: {"status":"UP"}
```

### 2. Test OPhim Integration

```bash
# Test 1: Add movie to favorites
curl -X POST https://your-app.onrender.com/movies/favorites/ten-phim-moi \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json"

# Test 2: Get favorites list
curl https://your-app.onrender.com/movies/favorites?page=1 \
  -H "Authorization: Bearer YOUR_TOKEN"

# Test 3: Get movie detail
curl https://your-app.onrender.com/movies/detail/ten-phim-moi
```

### 3. Verify Database Columns

```sql
-- Connect to Render PostgreSQL
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'movies' 
AND column_name IN ('view', 'cinema_screen', 'exclusive_subtitle', 'notify');

-- Expected output:
-- | column_name      | data_type        |
-- |------------------|------------------|
-- | view             | bigint           |
-- | cinema_screen    | boolean          |
-- | exclusive_subtitle | boolean        |
-- | notify           | character varying|
```

---

## ❌ TROUBLESHOOTING

### Issue 1: JSON Mapping Errors

**Symptoms**: Some fields are null in response

**Solution**:
```
1. Check OPhim API response structure
2. Verify @JsonProperty annotations match exactly
3. Review Jackson configuration
```

### Issue 2: Episode Data Missing

**Symptoms**: Episodes field is empty

**Solution**:
```
1. Verify ExternalEpisodeDTO structure
2. Check OPhim API returns episodes field
3. Debug mapper.mapEpisodes() logic
```

### Issue 3: Lazy Loading Exception

**Symptoms**: `LazyInitializationException` in logs

**Solution**:
```
1. Add @Transactional to getFavorites()
2. Eager load relationships: movie.getCategories().size()
3. Or use fetch join in query
```

### Issue 4: Database Column Not Created

**Symptoms**: SQL error when saving new fields

**Solution**:
```
1. Check Hibernate DDL generation setting
2. Run manual migration script
3. Verify Flyway migrations executed
```

---

## 📊 EXPECTED RESPONSE FORMAT

### GET /movies/favorites?page=1
```json
{
  "status": 200,
  "message": "Lấy danh sách yêu thích thành công",
  "data": [
    {
      "id": 123,
      "name": "Phim Hay",
      "slug": "phim-hay",
      "thumbUrl": "https://...",
      "posterUrl": "https://...",
      "year": "2024",
      "quality": "HD",
      "lang": "Vietnamese",
      "episodeCurrent": "10/24",
      "view": 5000,
      "cinemaScreen": true,
      "actors": ["Actor 1", "Actor 2"],
      "directors": ["Director 1"],
      "exclusiveSubtitle": false,
      "categories": [
        {
          "id": 1,
          "name": "Hành động",
          "slug": "hanh-dong"
        }
      ],
      "countries": [
        {
          "id": 1,
          "name": "Mỹ",
          "slug": "my"
        }
      ]
    }
  ]
}
```

### GET /movies/detail/{slug}
```json
{
  "status": 200,
  "message": "Lấy phim thành công",
  "data": {
    "id": 123,
    "name": "Phim Hay",
    "originName": "Original Title",
    "slug": "phim-hay",
    "content": "Nội dung phim...",
    "thumbUrl": "https://...",
    "posterUrl": "https://...",
    "year": "2024",
    "quality": "HD",
    "lang": "Vietnamese",
    "episodeTotal": "24",
    "status": "Ongoing",
    "type": "Phim Bộ",
    "view": 5000,
    "cinemaScreen": true,
    "actors": ["Actor 1", "Actor 2"],
    "directors": ["Director 1"],
    "exclusiveSubtitle": false,
    "notify": "Cập nhật tập mới",
    "categories": [...],
    "countries": [...],
    "episodes": [
      {
        "id": 1,
        "serverName": "VietSub",
        "isAi": false,
        "serverData": [
          {
            "name": "Tập 1",
            "slug": "tap-1",
            "filename": "tap-1-server-1",
            "linkEmbed": "https://...",
            "linkM3u8": "https://..."
          }
        ]
      }
    ]
  }
}
```

---

## 🔄 ROLLBACK PLAN

Nếu có issue sau deployment:

```bash
# 1. Revert commit
git revert <commit-hash>
git push origin main

# 2. Render sẽ auto-redeploy
# Monitor: https://dashboard.render.com

# 3. Manual database rollback (if needed)
# Connect to PostgreSQL and run:
-- db/migrations/V1__Add_OPhim_Full_Support.sql (ROLLBACK section)
```

---

## 📞 QUICK REFERENCE

| Action | Command |
|--------|---------|
| Local test | `mvn quarkus:dev` |
| Build | `mvn clean package` |
| Check logs | Dashboard → Logs |
| SSH to instance | Dashboard → Shell |
| Database access | Render PostgreSQL connection |
| Redeploy | Git push / Manual redeploy |

---

## ✅ DEPLOYMENT COMPLETE CHECKLIST

- [ ] Local tests passed ✓
- [ ] Git push successful ✓
- [ ] Render build completed ✓
- [ ] App is running ✓
- [ ] Database migration applied ✓
- [ ] API endpoints responding correctly ✓
- [ ] OPhim data mapping working ✓
- [ ] Episodes data displaying ✓
- [ ] No errors in logs ✓
- [ ] Performance acceptable ✓

---

**Date Deployed**: [Current Date]
**Deployed By**: [Your Name]
**Version**: v1.0.0-OPhim-Full-Support

