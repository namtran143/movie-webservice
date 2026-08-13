# Movie Webservice

Webservice Java 17 đọc dữ liệu phim đã crawl ở Bài 2 từ SQLite và trả về JSON được format đẹp.

## Công nghệ

- Java 17
- Maven
- JDK `HttpServer`
- SQLite JDBC
- Gson

## Luồng xử lý

`GET /movie?url=...` → `MovieHandler` → `MovieService` → `MovieRepository` → SQLite → JSON response.

## Build project

Chạy tại thư mục gốc của project:

```bash
mvn clean package
```

Sau khi build thành công, file chạy được tạo tại:

```text
target/movie-webservice.jar
```

## Chạy webservice

Chạy từ thư mục gốc để đường dẫn mặc định `data/movies.db` hoạt động:

```bash
java -jar target/movie-webservice.jar
```

Có thể truyền cấu hình riêng:

```bash
java -jar target/movie-webservice.jar --port=8080 --db=data/movies.db
```

## API

### Kiểm tra trạng thái server

```http
GET http://localhost:8080/health
```

Kết quả mẫu:

```json
{
  "status": "UP"
}
```

### Lấy thông tin phim theo URL đã crawl

```http
GET http://localhost:8080/movie?url=https%3A%2F%2Ftoivote.com%2Fmovie%2F2d9acb2c-dcb9-4a8b-8ab5-61d0c61fd50c
```

Có thể dùng Postman và nhập URL gốc vào query parameter `url`; Postman sẽ tự encode.

Kết quả trả về gồm:

- `sourceUrl`
- `title`
- `productionYear`
- `country`
- `genres`
- `directors`
- `actors`

### Mã phản hồi

- `200`: tìm thấy phim.
- `400`: thiếu hoặc sai URL.
- `404`: URL hợp lệ nhưng không tồn tại trong database.
- `405`: sử dụng HTTP method khác `GET`.
- `500`: lỗi database hoặc lỗi server.

## Debug theo yêu cầu đề bài

1. Mở file `MovieService.java`.
2. Đặt breakpoint tại dòng:

```java
int actorNameLength = actorName.length();
```

3. Chọn **Edit Breakpoint** hoặc **Add Conditional Breakpoint**.
4. Nhập điều kiện:

```java
actorName.startsWith("A")
```

5. Chạy `App.java` bằng chế độ Debug.
6. Gọi API phim mẫu phía trên.

Dữ liệu mẫu có diễn viên `Amy Adams`, vì vậy debugger sẽ dừng khi:

```text
actorName = "Amy Adams"
```

Không thêm câu lệnh `if` vào source code để phục vụ debug. Điều kiện chỉ được đặt trong breakpoint của IDE.

## Kết quả kiểm tra

- Maven build: `BUILD SUCCESS`
- Health API hoạt động.
- Movie API trả JSON đúng định dạng.
- Conditional breakpoint dừng đúng tại diễn viên có tên bắt đầu bằng chữ `A`.
