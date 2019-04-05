package temple.edu.lab7;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Book implements Parcelable {
    private int bookId;
    private String title;
    private String author;
    private int duration;
    private int published;
    private String coverURL;

    public Book(int id, String title, String author, int duration, int published, String coverURL ) {
        this.bookId = id;
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.published = published;
        this.coverURL = coverURL;
    }

    public static Book getBook(JSONObject obj) throws JSONException {
        Log.d("Recieve oject", obj.getString("title"));
        Book book = new Book(obj.getInt("book_id"), obj.getString("title"),obj.getString("author"), obj.getInt("duration"), obj.getInt("published"), obj.getString("cover_url"));
        Log.d("book title? ", book.getTitle());
        return book;
    }

    public JSONObject getBookAsJSON(){
        JSONObject bookObject = new JSONObject();
        try {
            bookObject.put("book_id", bookId);
            bookObject.put("title", title);
            bookObject.put("author", author);
            bookObject.put("duration", duration);
            bookObject.put("published", published);
            bookObject.put("cover_url", coverURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bookObject;
    }

    public int getId() {
        return this.bookId;
    }
    public String getTitle() { return this.title; }
    public String getAuthor() { return this.author; }
    public int getDuration() { return this.duration; }
    public int getPublished() { return this.published; }
    public String getCoverURL() { return this.coverURL; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeInt(duration);
        dest.writeInt(published);
        dest.writeString(coverURL);
    }

    private Book(Parcel in) {
        bookId = in.readInt();
        title = in.readString();
        author = in.readString();
        duration = in.readInt();
        published = in.readInt();
        coverURL = in.readString();
    }
    public static final Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}