package im.zego.gomovie.server.model;

public class MovieInfo {

    private int movieId;
    private String movieName;
    public String mFileAssetsPath;
    private boolean isCheck = false;

    public int getMovieId() {
        return movieId;
    }

    public MovieInfo setMovieId(int movieId) {
        this.movieId = movieId;
        return this;
    }

    public String getMovieName() {
        return movieName;
    }

    public MovieInfo setMovieName(String movieName) {
        this.movieName = movieName;
        return this;
    }

    public String getmFileAssetsPath() {
        return mFileAssetsPath;
    }

    public MovieInfo setmFileAssetsPath(String mFileAssetsPath) {
        this.mFileAssetsPath = mFileAssetsPath;
        return this;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public MovieInfo setCheck(boolean check) {
        this.isCheck = check;
        return this;
    }

}
