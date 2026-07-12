package constants;

public class APIConstants {
    
    private APIConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String BASE_URI = "https://jsonplaceholder.typicode.com";
    public static final String CREATE_POST = "/posts/";
    public static final String UPDATE_POST = "/posts/{id}";
    public static final String USERS = "/users/";
    public static final String ALBUMS = "/albums/";
    public static final String PHOTOS = "/photos/";
    public static final String TODOS = "/todos/";
    public static final String CONTENT_TYPE = "application/json";

}
