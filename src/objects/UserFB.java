package objects;


public class UserFB {

    String id;
    String name;
    String url;

    /** @param id
     * @param name
     * @param url */
    public UserFB(String id, String name, String url) {

            this.id = id;
            this.name = name;
            this.url = url;
    }

    public String getId() {

            return id;
    }

    public String getName() {

            return name;
    }

    public String getUrl() {

            return url;
    }

    public void setId(String id) {

            this.id = id;
    }

    public void setName(String name) {

            this.name = name;
    }

    public void setUrl(String url) {

            this.url = url;
    }
}
