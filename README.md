# Movie Webservice

Webservice Java 17 đọc dữ liệu phim đã crawl ở Bài 2 từ SQLite và trả về JSON.

## Công nghệ

- Java 17
- Maven
- JDK `HttpServer`
- SQLite JDBC
- Gson

## Luồng xử lý

```text
GET /movie?url=...
→ MovieHandler
→ MovieService
→ MovieRepository
→ SQLite
→ Movie
→ Gson
→ JSON Response
```

## Build

Chạy tại thư mục gốc của project:

```bash
mvn clean package
```

File JAR được tạo tại:

```text
target/movie-webservice.jar
```

## Chạy webservice

```bash
java -jar target/movie-webservice.jar
```

Mặc định:

- Port: `8080`
- Database: `data/movies.db`

Có thể truyền cấu hình:

```bash
java -jar target/movie-webservice.jar --port=8080 --db=data/movies.db
```

## API

### Health check

```bash
curl.exe http://localhost:8080/health
```

Kết quả:

```json
{
  "status": "UP"
}
```

### Lấy phim theo URL

```bash
curl.exe --get "http://localhost:8080/movie" --data-urlencode "url=https://toivote.com/movie/2d9acb2c-dcb9-4a8b-8ab5-61d0c61fd50c"
```

Kết quả trả về gồm:

- `sourceUrl`
- `title`
- `productionYear`
- `country`
- `genres`
- `directors`
- `actors`

### HTTP Status

- `200`: tìm thấy phim.
- `400`: thiếu URL hoặc URL không hợp lệ.
- `404`: không tìm thấy phim trong database.
- `405`: sử dụng HTTP method khác `GET`.
- `500`: lỗi database hoặc server.

## Debug theo yêu cầu đề bài

Mở `MovieService.java` và đặt conditional breakpoint tại dòng:

```java
int actorNameLength = actorName.length();
```

Điều kiện:

```java
actorName.startsWith("A")
```

Chạy `App.java` bằng chế độ Debug, sau đó gọi API `/movie`.

Debugger sẽ dừng tại:

```text
actorName = "Amy Adams"
```

Không thêm câu lệnh `if` vào source code để phục vụ debug.

## Kết quả kiểm tra

- `mvn clean package` → `BUILD SUCCESS`.
- Webservice chạy thành công.
- `/health` trả trạng thái `UP`.
- `/movie` trả đúng dữ liệu phim dạng JSON.
- Conditional breakpoint dừng đúng tại `Amy Adams`.
