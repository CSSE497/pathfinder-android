package xyz.thepathfinder.android;

public class Path {

    public static String DEFAULT_PATH = "/default";

    private String path;

    public Path(String path) {
        if(path != null && path.equals("")) {
            throw new IllegalArgumentException("Path cannot be an empty string");
        }

        if(path == null) {
            this.path = Path.DEFAULT_PATH;
        } else {
            this.path = path;
        }
    }

    public static boolean isValidName(String name) {
        return !name.contains("/");
    }

    public String getChildPath(String name) {
        if(Path.isValidName(name)) {
            return this.path + "/" + name;
        } else {
            throw new IllegalArgumentException("Invalid path name: " + name);
        }
    }

    public String getName() {
        int lastSlashIndex = this.path.lastIndexOf('/') + 1;
        return this.path.substring(lastSlashIndex);
    }

    public String getPath() {
        return path;
    }

    public String getParentPath() {
        int lastSlashIndex = this.path.lastIndexOf('/');
        return this.path.substring(0, lastSlashIndex);
    }

    @Override
    public boolean equals(Object path) {
        return this.path.equals(path);
    }
}
